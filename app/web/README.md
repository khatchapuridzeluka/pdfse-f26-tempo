# TEMPO Safety: web version (iPhone, Android, laptop)

The same helmet check as the Android app, in the browser. Open the link, tap **Start camera**, point at people.

- **Live:** https://rubikg6.github.io/tempo-helmet-web/
- Works in Safari on iPhone and in Chrome on Android. Must be opened over `https` (the camera is blocked otherwise).
- First start downloads about 40 MB of models; after that the browser caches them.
- Everything runs on the phone. No video or photo is uploaded anywhere.

Same boxes as the app: solid green helmet, dashed amber checking, dashed red "NO HELMET?" while it confirms, solid red + banner + beep once a person has been helmet-less for 1.5 s, dashed grey when the head is out of view. **Save photo** opens the share sheet on phones ("Save Image") and downloads elsewhere. Vibration works on Android only; iPhone gets the beep.

## How it is built

No build step: plain HTML, CSS and JavaScript modules.

| File | Mirrors (Android) | What it does |
|---|---|---|
| `js/app.js` | `MainActivity.kt`, `OverlayView.kt` | Camera, overlay, status bar, alarm, snapshot |
| `js/pipeline.js` | `PersonDetector.kt`, `HelmetClassifier.kt`, `SafetyAnalyzer.kt` | Person detection, then helmet check per person crop |
| `js/tracker.js` | `PersonTracker.kt` | Frame-to-frame tracking, voting, 1.5 s confirmation |

Constants are copied one for one from the Kotlin files; change both together.

| Runtime | Model | Loaded from |
|---|---|---|
| [MediaPipe Tasks Vision](https://www.npmjs.com/package/@mediapipe/tasks-vision) 0.10.20 (WASM) | `models/efficientdet_lite0.tflite` (people) | jsDelivr CDN |
| [ONNX Runtime Web](https://www.npmjs.com/package/onnxruntime-web) 1.20.1 (WASM, 1 thread) | `models/gear_guard_net.onnx` (helmet, vest) | jsDelivr CDN |

`gear_guard_net.onnx` is Qualcomm's float ONNX export of the same GearGuardNet model the Android app uses (BSD-3-Clause), with its external weights merged into one file. Its outputs match the TFLite version to within 1e-4.

## Run locally

```bash
cd app/web
python -m http.server 8123
```

Open http://localhost:8123 (localhost counts as secure, so the camera works). To test with a clip instead of the camera, put it in `app/web/testvideos/` (git-ignored) and open `http://localhost:8123/?video=testvideos/<file>.mp4`.

## Deploy

The live site is this folder pushed to the `main` branch of `rubikg6/tempo-helmet-web`, served by GitHub Pages:

```bash
git subtree split --prefix app/web -b web-deploy
git push https://github.com/rubikg6/tempo-helmet-web.git web-deploy:main --force
```
