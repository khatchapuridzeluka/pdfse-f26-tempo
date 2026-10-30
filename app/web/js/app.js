// Web version of app/mobile/.../MainActivity.kt: camera, overlay, status bar, alarm, snapshot.

import { SafetyAnalyzer, MODEL_BYTES } from "./pipeline.js";
import { PersonTracker, Status } from "./tracker.js";

const ANALYSIS_LONG_SIDE = 640; // same as the Android app's 640x480 analysis stream
const ALARM_EVERY_MS = 3000;

const COLORS = {
  [Status.HELMET]: "#43a047",
  [Status.NO_HELMET]: "#e53935",
  [Status.SUSPECTED]: "#e53935",
  [Status.CHECKING]: "#ffa000",
  [Status.HEAD_HIDDEN]: "#9e9e9e",
};

const $ = (id) => document.getElementById(id);
const video = $("video");
const overlay = $("overlay");
const overlayCtx = overlay.getContext("2d");
const frame = document.createElement("canvas");
const frameCtx = frame.getContext("2d", { willReadFrequently: true });

const tracker = new PersonTracker();
let analyzer = null;
let stream = null;
let facing = "environment";
let running = false;
let lastPeople = [];
let lastAlarmAt = 0;
let soundOn = true;
let audio = null;
let wakeLock = null;

$("start").addEventListener("click", start);
$("flip").addEventListener("click", flip);
$("snapshot").addEventListener("click", saveSnapshot);
$("sound").addEventListener("click", () => {
  soundOn = !soundOn;
  $("sound").textContent = soundOn ? "Sound on" : "Sound off";
  $("sound").setAttribute("aria-pressed", String(soundOn));
});
document.addEventListener("visibilitychange", () => {
  if (document.visibilityState === "visible" && running) keepScreenOn();
});

function showMessage(text, isError = false) {
  $("message").textContent = text;
  $("message").classList.toggle("error", isError);
}

async function start() {
  if (!testVideo && (!window.isSecureContext || !navigator.mediaDevices?.getUserMedia)) {
    showMessage("The camera only works when this page is opened over https.", true);
    return;
  }
  $("start").disabled = true;
  // Created inside the tap so iOS allows the alarm sound later.
  try {
    audio = new (window.AudioContext || window.webkitAudioContext)();
  } catch {
    audio = null;
  }

  try {
    showMessage("Asking for the camera…");
    await openCamera(facing);
  } catch (e) {
    $("start").disabled = false;
    showMessage(
      e.name === "NotAllowedError"
        ? "Camera access was blocked. Allow the camera for this site in your browser settings, then reload."
        : `Could not open the camera: ${e.message}`,
      true,
    );
    return;
  }

  if (!analyzer) {
    $("progress").hidden = false;
    showMessage("Downloading AI models…");
    try {
      analyzer = await SafetyAnalyzer.create((bytes) => {
        const pct = Math.min(100, (100 * bytes) / MODEL_BYTES);
        $("bar").style.width = `${pct}%`;
        showMessage(`Downloading AI models… ${(bytes / 1e6).toFixed(0)} / ${(MODEL_BYTES / 1e6).toFixed(0)} MB`);
      });
    } catch (e) {
      console.error(e);
      $("start").disabled = false;
      showMessage(`Could not load the AI models: ${e.message}`, true);
      return;
    }
  }

  $("intro").hidden = true;
  $("status").hidden = false;
  $("controls").hidden = false;
  keepScreenOn();
  running = true;
  requestAnimationFrame(loop);
}

// Testing aid: ?video=<same-site file> plays that clip in place of the camera.
const testVideo = new URLSearchParams(location.search).get("video");

async function openCamera(mode) {
  if (testVideo) {
    video.src = testVideo;
    video.loop = true;
    await video.play();
    return;
  }
  stream?.getTracks().forEach((t) => t.stop());
  stream = await navigator.mediaDevices.getUserMedia({
    audio: false,
    video: { facingMode: { ideal: mode }, width: { ideal: 1280 }, height: { ideal: 720 } },
  });
  video.srcObject = stream;
  await video.play();
  // Mirror the selfie camera like a mirror; boxes are mirrored to match when drawn.
  video.classList.toggle("mirrored", mode === "user");
}

async function flip() {
  facing = facing === "environment" ? "user" : "environment";
  tracker.reset();
  lastPeople = [];
  try {
    await openCamera(facing);
  } catch (e) {
    facing = facing === "environment" ? "user" : "environment";
    alert(`Could not switch camera: ${e.message}`);
  }
}

async function keepScreenOn() {
  try {
    wakeLock = await navigator.wakeLock?.request("screen");
  } catch {
    wakeLock = null; // not supported or not allowed: the screen may dim, nothing else breaks
  }
}

async function loop() {
  if (!running) return;
  if (video.readyState >= 2 && video.videoWidth > 0) {
    const started = performance.now();
    // Downscale the camera frame like the phone app does before analysis.
    const s = ANALYSIS_LONG_SIDE / Math.max(video.videoWidth, video.videoHeight);
    const w = Math.round(video.videoWidth * s);
    const h = Math.round(video.videoHeight * s);
    if (frame.width !== w || frame.height !== h) {
      frame.width = w;
      frame.height = h;
    }
    frameCtx.drawImage(video, 0, 0, w, h);

    try {
      lastPeople = tracker.update(await analyzer.analyze(frame), performance.now());
    } catch (e) {
      console.error("Analysis failed", e);
      lastPeople = [];
    }
    drawOverlay(lastPeople);
    showStatus(lastPeople, Math.round(performance.now() - started));
  }
  requestAnimationFrame(loop);
}

function drawOverlay(people) {
  const dpr = window.devicePixelRatio || 1;
  const cw = overlay.clientWidth;
  const ch = overlay.clientHeight;
  if (overlay.width !== Math.round(cw * dpr) || overlay.height !== Math.round(ch * dpr)) {
    overlay.width = Math.round(cw * dpr);
    overlay.height = Math.round(ch * dpr);
  }
  overlayCtx.setTransform(dpr, 0, 0, dpr, 0, 0);
  overlayCtx.clearRect(0, 0, cw, ch);
  // Same mapping as the video's object-fit: cover: scale to fill, centre, crop.
  const scale = Math.max(cw / frame.width, ch / frame.height);
  const dx = (cw - frame.width * scale) / 2;
  const dy = (ch - frame.height * scale) / 2;
  const mirror = facing === "user" ? cw : null;
  drawPeople(overlayCtx, people, scale, dx, dy, mirror, 1);
}

/** Shared by the live overlay and the saved snapshot. */
function drawPeople(ctx, people, scale, dx, dy, mirrorWidth, unit) {
  ctx.font = `700 ${15 * unit}px system-ui, -apple-system, sans-serif`;
  ctx.textBaseline = "top";
  for (const p of people) {
    let left = p.box[0] * scale + dx;
    let right = p.box[2] * scale + dx;
    if (mirrorWidth !== null) [left, right] = [mirrorWidth - right, mirrorWidth - left];
    const top = p.box[1] * scale + dy;
    const bottom = p.box[3] * scale + dy;
    const color = COLORS[p.status];

    ctx.strokeStyle = color;
    ctx.lineWidth = 3 * unit;
    // Dashed until decided: only a solid red box is a real violation.
    ctx.setLineDash(p.status === Status.HELMET || p.status === Status.NO_HELMET ? [] : [10 * unit, 6 * unit]);
    ctx.strokeRect(left, top, right - left, bottom - top);
    ctx.setLineDash([]);

    let label = {
      [Status.HELMET]: `HELMET ${Math.round(p.helmetScore * 100)}%`,
      [Status.NO_HELMET]: "NO HELMET",
      [Status.SUSPECTED]: "NO HELMET?",
      [Status.CHECKING]: "CHECKING…",
      [Status.HEAD_HIDDEN]: "HEAD NOT VISIBLE",
    }[p.status];
    if (p.hasVest) label += " · vest";
    const pad = 4 * unit;
    const textH = 15 * unit;
    const labelTop = Math.max(0, top - textH - 2 * pad);
    ctx.fillStyle = color;
    ctx.fillRect(left, labelTop, ctx.measureText(label).width + 2 * pad, textH + 2 * pad);
    ctx.fillStyle = "#fff";
    ctx.fillText(label, left + pad, labelTop + pad);
  }
}

function showStatus(people, elapsedMs) {
  const withHelmet = people.filter((p) => p.status === Status.HELMET).length;
  const without = people.filter((p) => p.status === Status.NO_HELMET).length;
  const suspected = people.filter((p) => p.status === Status.SUSPECTED).length;
  $("counts").textContent =
    `People: ${people.length}   ✓ Helmet: ${withHelmet}   ✗ No helmet: ${without}` +
    (suspected ? `   ? Checking: ${suspected}` : "");
  $("speed").textContent = `${elapsedMs} ms per frame`;

  // Only confirmed violations (helmet-less for 1.5 s) raise the alarm.
  const alarm = without > 0;
  $("banner").hidden = !alarm;
  $("status").classList.toggle("alarm", alarm);
  const now = performance.now();
  if (alarm && now - lastAlarmAt > ALARM_EVERY_MS) {
    lastAlarmAt = now;
    navigator.vibrate?.(300);
    if (soundOn) beep();
  }
}

function beep() {
  if (!audio) return;
  audio.resume?.();
  const osc = audio.createOscillator();
  const gain = audio.createGain();
  osc.frequency.value = 880;
  gain.gain.setValueAtTime(0.25, audio.currentTime);
  gain.gain.exponentialRampToValueAtTime(0.001, audio.currentTime + 0.35);
  osc.connect(gain).connect(audio.destination);
  osc.start();
  osc.stop(audio.currentTime + 0.35);
}

/** Saves the current frame with its boxes, time and counts, as evidence of a violation. */
async function saveSnapshot() {
  if (!frame.width) return;
  const shot = document.createElement("canvas");
  shot.width = frame.width;
  shot.height = frame.height;
  const ctx = shot.getContext("2d");
  ctx.drawImage(frame, 0, 0);
  drawPeople(ctx, lastPeople, 1, 0, 0, null, frame.width / 400);

  const now = new Date();
  const pad2 = (n) => String(n).padStart(2, "0");
  const stamp = `${now.getFullYear()}-${pad2(now.getMonth() + 1)}-${pad2(now.getDate())} ${pad2(now.getHours())}:${pad2(now.getMinutes())}:${pad2(now.getSeconds())}`;
  const without = lastPeople.filter((p) => p.status === Status.NO_HELMET).length;
  const size = frame.width / 28;
  ctx.font = `${size}px system-ui, sans-serif`;
  ctx.fillStyle = "#fff";
  ctx.shadowColor = "#000";
  ctx.shadowBlur = 4;
  ctx.fillText(`TEMPO · ${stamp} · people ${lastPeople.length} · no helmet ${without}`, size / 2, shot.height - size / 2);

  const blob = await new Promise((resolve) => shot.toBlob(resolve, "image/jpeg", 0.92));
  const name = `tempo_${stamp.replace(/[-: ]/g, "").slice(0, 8)}_${stamp.slice(11).replace(/:/g, "")}.jpg`;
  const file = new File([blob], name, { type: "image/jpeg" });
  // On phones the share sheet offers "Save Image"; elsewhere fall back to a download.
  if (navigator.canShare?.({ files: [file] })) {
    try {
      await navigator.share({ files: [file], title: "TEMPO safety photo" });
      return;
    } catch (e) {
      if (e.name === "AbortError") return;
    }
  }
  const a = document.createElement("a");
  a.href = URL.createObjectURL(blob);
  a.download = name;
  a.click();
  setTimeout(() => URL.revokeObjectURL(a.href), 5000);
}
