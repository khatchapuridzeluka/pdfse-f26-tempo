// Port of app/mobile/.../PersonTracker.kt — keep the two in step.
//
// Follows each person from frame to frame (by box overlap) and decides helmet / no helmet by
// majority vote over their last few frames. A "no helmet" verdict only becomes a violation after
// it has held for CONFIRM_MS, so a few bad frames never raise a false alarm.

export const Status = Object.freeze({
  CHECKING: "CHECKING",
  HELMET: "HELMET",
  SUSPECTED: "SUSPECTED",
  NO_HELMET: "NO_HELMET",
  HEAD_HIDDEN: "HEAD_HIDDEN",
});

/** How long a person must look helmet-less before it counts (tested on 10 site videos). */
export const CONFIRM_MS = 1500;
const WINDOW = 5;
const MIN_VOTES = 3;
const MIN_IOU = 0.3;
const FORGET_MS = 1000;
const BOX_SMOOTHING = 0.6;

export class PersonTracker {
  constructor() {
    this.tracks = [];
  }

  reset() {
    this.tracks = [];
  }

  /**
   * @param {{box:number[], headVisible:boolean, hasHelmet:boolean, helmetScore:number, hasVest:boolean}[]} detections
   *   box is [left, top, right, bottom] in frame pixels.
   * @param {number} nowMs
   */
  update(detections, nowMs) {
    // Greedy matching: best-overlapping detection/track pairs first.
    const pairs = [];
    detections.forEach((d, i) => {
      for (const t of this.tracks) {
        const overlap = iou(d.box, t.box);
        if (overlap >= MIN_IOU) pairs.push([i, t, overlap]);
      }
    });
    pairs.sort((a, b) => b[2] - a[2]);

    const matched = new Set();
    const seen = new Set();
    for (const [i, track] of pairs) {
      if (matched.has(i) || seen.has(track)) continue;
      matched.add(i);
      seen.add(track);
      observe(track, detections[i], nowMs);
    }
    detections.forEach((d, i) => {
      if (matched.has(i)) return;
      const track = newTrack(d.box, nowMs);
      this.tracks.push(track);
      seen.add(track);
      observe(track, d, nowMs);
    });

    // People hidden for a moment (bending down, walking behind something) keep their history.
    this.tracks = this.tracks.filter((t) => nowMs - t.lastSeenMs <= FORGET_MS);

    return this.tracks
      .filter((t) => seen.has(t))
      .map((t) => {
        const helmetFrames = t.helmetScores.filter((s) => s > 0);
        return {
          box: [...t.box],
          status: status(t, nowMs),
          helmetScore: helmetFrames.length ? helmetFrames.reduce((a, b) => a + b, 0) / helmetFrames.length : 0,
          hasVest: t.vestVotes.filter(Boolean).length * 2 > t.vestVotes.length,
        };
      });
  }
}

function newTrack(box, nowMs) {
  return {
    box: [...box],
    lastSeenMs: nowMs,
    helmetVotes: [],
    vestVotes: [],
    helmetScores: [],
    verdict: null, // null | "HELMET" | "NO_HELMET"
    headVisible: true,
    noHelmetSinceMs: null,
  };
}

function status(t, nowMs) {
  if (!t.headVisible) return Status.HEAD_HIDDEN;
  if (t.verdict === null) return Status.CHECKING;
  if (t.verdict === "HELMET") return Status.HELMET;
  return t.noHelmetSinceMs !== null && nowMs - t.noHelmetSinceMs >= CONFIRM_MS ? Status.NO_HELMET : Status.SUSPECTED;
}

function observe(track, d, nowMs) {
  track.lastSeenMs = nowMs;
  // Smooth the box a little so it does not jitter.
  track.box = track.box.map((old, k) => old + (d.box[k] - old) * BOX_SMOOTHING);

  track.headVisible = d.headVisible;
  if (!d.headVisible) {
    // No vote without a head, and the violation timer starts over once it is back.
    track.noHelmetSinceMs = null;
    return;
  }

  push(track.helmetVotes, d.hasHelmet);
  push(track.vestVotes, d.hasVest);
  push(track.helmetScores, d.helmetScore);

  const votes = track.helmetVotes.length;
  if (votes >= MIN_VOTES) {
    const yes = track.helmetVotes.filter(Boolean).length;
    if (yes * 2 > votes) track.verdict = "HELMET";
    else if (yes * 2 < votes) track.verdict = "NO_HELMET";
    // a tie keeps the last decision
  }
  if (track.verdict === "NO_HELMET") {
    if (track.noHelmetSinceMs === null) track.noHelmetSinceMs = nowMs;
  } else {
    track.noHelmetSinceMs = null;
  }
}

function push(list, value) {
  list.push(value);
  if (list.length > WINDOW) list.shift();
}

function iou(a, b) {
  const w = Math.min(a[2], b[2]) - Math.max(a[0], b[0]);
  const h = Math.min(a[3], b[3]) - Math.max(a[1], b[1]);
  if (w <= 0 || h <= 0) return 0;
  const inter = w * h;
  return inter / ((a[2] - a[0]) * (a[3] - a[1]) + (b[2] - b[0]) * (b[3] - b[1]) - inter);
}
