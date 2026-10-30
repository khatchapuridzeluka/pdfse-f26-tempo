// Port of PersonDetector.kt, HelmetClassifier.kt and SafetyAnalyzer.kt — keep them in step.
//
// People first (MediaPipe EfficientDet-Lite0, COCO "person"), then a helmet check on each
// person's crop (Qualcomm GearGuardNet, run with ONNX Runtime Web).

import { FilesetResolver, ObjectDetector } from "https://cdn.jsdelivr.net/npm/@mediapipe/tasks-vision@0.10.20/vision_bundle.mjs";

const MEDIAPIPE_WASM = "https://cdn.jsdelivr.net/npm/@mediapipe/tasks-vision@0.10.20/wasm";
const ORT_WASM = "https://cdn.jsdelivr.net/npm/onnxruntime-web@1.20.1/dist/";

const PERSON_MODEL = "models/efficientdet_lite0.tflite";
const HELMET_MODEL = "models/gear_guard_net.onnx";

// PersonDetector
const PERSON_MIN_SCORE = 0.5;
const MAX_PEOPLE = 8;
// SafetyAnalyzer
const MAX_CHECKED = 6;
const MIN_PERSON_HEIGHT = 0.1;
const HEAD_EDGE = 0.02;
const SIDE_PAD = 0.1;
const TOP_PAD = 0.15;
const MIN_CROP_W = 16;
const MIN_CROP_H = 32;
// HelmetClassifier
const IN_W = 192;
const IN_H = 320;
const CLASS_HELMET = 0;
const CLASS_VEST = 1;
const HELMET_SCORE = 0.35;
const VEST_SCORE = 0.4;
const HEAD_ZONE = 0.45;

/** Downloads a file and reports progress as bytes arrive. */
async function fetchWithProgress(url, onBytes) {
  const res = await fetch(url);
  if (!res.ok) throw new Error(`Could not download ${url} (${res.status})`);
  if (!res.body) {
    const buf = new Uint8Array(await res.arrayBuffer());
    onBytes(buf.length);
    return buf;
  }
  const reader = res.body.getReader();
  const chunks = [];
  let total = 0;
  for (;;) {
    const { done, value } = await reader.read();
    if (done) break;
    chunks.push(value);
    total += value.length;
    onBytes(value.length);
  }
  const out = new Uint8Array(total);
  let offset = 0;
  for (const c of chunks) {
    out.set(c, offset);
    offset += c.length;
  }
  return out;
}

export class SafetyAnalyzer {
  /** @param {(loadedBytes:number) => void} onProgress */
  static async create(onProgress) {
    let loaded = 0;
    const progress = (n) => onProgress((loaded += n));

    const ort = window.ort;
    ort.env.wasm.wasmPaths = ORT_WASM;
    // Static hosting has no cross-origin isolation, so WASM threads are unavailable anyway.
    ort.env.wasm.numThreads = 1;

    const [personBytes, helmetBytes, fileset] = await Promise.all([
      fetchWithProgress(PERSON_MODEL, progress),
      fetchWithProgress(HELMET_MODEL, progress),
      FilesetResolver.forVisionTasks(MEDIAPIPE_WASM),
    ]);

    const detector = await ObjectDetector.createFromOptions(fileset, {
      baseOptions: { modelAssetBuffer: personBytes, delegate: "CPU" },
      runningMode: "IMAGE",
      scoreThreshold: PERSON_MIN_SCORE,
      categoryAllowlist: ["person"],
      maxResults: MAX_PEOPLE,
    });
    const session = await ort.InferenceSession.create(helmetBytes, {
      executionProviders: ["wasm"],
      graphOptimizationLevel: "all",
    });
    return new SafetyAnalyzer(detector, session);
  }

  constructor(detector, session) {
    this.detector = detector;
    this.session = session;
    this.letterbox = document.createElement("canvas");
    this.letterbox.width = IN_W;
    this.letterbox.height = IN_H;
    this.letterboxCtx = this.letterbox.getContext("2d", { willReadFrequently: true });
    this.input = new Float32Array(3 * IN_W * IN_H);
  }

  /**
   * @param {HTMLCanvasElement} frame upright frame, long side about 640 px
   * @returns {Promise<{box:number[], headVisible:boolean, hasHelmet:boolean, helmetScore:number, hasVest:boolean}[]>}
   */
  async analyze(frame) {
    const W = frame.width;
    const H = frame.height;
    const boxes = this.detector
      .detect(frame)
      .detections.map((d) => {
        const b = d.boundingBox;
        return [b.originX, b.originY, b.originX + b.width, b.originY + b.height];
      })
      // Very small boxes are mostly false people, and too blurry to judge a helmet anyway.
      .filter((b) => b[3] - b[1] >= MIN_PERSON_HEIGHT * H)
      .sort((a, b) => (b[2] - b[0]) * (b[3] - b[1]) - (a[2] - a[0]) * (a[3] - a[1]))
      .slice(0, MAX_CHECKED);

    const results = [];
    for (const box of boxes) {
      // A body cut off at the top of the frame has no head to check: judging it gave false alarms.
      if (box[1] <= HEAD_EDGE * H) {
        results.push({ box, headVisible: false, hasHelmet: false, helmetScore: 0, hasVest: false });
        continue;
      }
      // Widen the crop a little and extend it upward so a helmet is never cut off.
      const bw = box[2] - box[0];
      const bh = box[3] - box[1];
      const left = Math.max(0, Math.trunc(box[0] - SIDE_PAD * bw));
      const top = Math.max(0, Math.trunc(box[1] - TOP_PAD * bh));
      const right = Math.min(W, Math.trunc(box[2] + SIDE_PAD * bw));
      const bottom = Math.min(H, Math.trunc(box[3]));
      if (right - left < MIN_CROP_W || bottom - top < MIN_CROP_H) continue;

      const gear = await this.checkGear(frame, left, top, right - left, bottom - top);
      results.push({ box, headVisible: true, ...gear });
    }
    return results;
  }

  async checkGear(frame, sx, sy, sw, sh) {
    // Letterbox the crop into the 192x320 portrait input, keeping its aspect ratio.
    const scale = Math.min(IN_W / sw, IN_H / sh);
    const w = sw * scale;
    const h = sh * scale;
    const padX = (IN_W - w) / 2;
    const padY = (IN_H - h) / 2;
    const ctx = this.letterboxCtx;
    ctx.fillStyle = "rgb(114,114,114)";
    ctx.fillRect(0, 0, IN_W, IN_H);
    ctx.drawImage(frame, sx, sy, sw, sh, padX, padY, w, h);

    // RGBA pixels -> planar RGB floats in 0..1 (NCHW).
    const px = ctx.getImageData(0, 0, IN_W, IN_H).data;
    const plane = IN_W * IN_H;
    for (let i = 0, p = 0; i < plane; i++, p += 4) {
      this.input[i] = px[p] / 255;
      this.input[plane + i] = px[p + 1] / 255;
      this.input[2 * plane + i] = px[p + 2] / 255;
    }
    const out = await this.session.run({ image: new window.ort.Tensor("float32", this.input, [1, 3, IN_H, IN_W]) });
    const boxes = out.boxes.data;
    const scores = out.scores.data;
    const classes = out.class_idx.data;

    // A helmet only counts if it sits on the head: centre in the top part of the crop.
    const headLimit = HEAD_ZONE * sh;
    let helmetScore = 0;
    let hasVest = false;
    for (let i = 0; i < scores.length; i++) {
      const score = scores[i];
      if (classes[i] === CLASS_HELMET && score >= HELMET_SCORE) {
        const centreY = ((boxes[4 * i + 1] + boxes[4 * i + 3]) / 2 - padY) / scale;
        if (centreY < headLimit) helmetScore = Math.max(helmetScore, score);
      } else if (classes[i] === CLASS_VEST && score >= VEST_SCORE) {
        hasVest = true;
      }
    }
    return { hasHelmet: helmetScore > 0, helmetScore, hasVest };
  }
}

/** Total download size of both models, for the progress bar. */
export const MODEL_BYTES = 13836895 + 25137192;
