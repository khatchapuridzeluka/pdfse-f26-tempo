# TEMPO Safety: helmet detection demo (Android)

> **iPhone or no Android phone?** Use the web version: https://rubikg6.github.io/tempo-helmet-web/ (source and details in [`web/`](web/README.md)).

Point the phone camera at a construction site. The app finds every person in view and marks each one:

- **Solid green, "HELMET 91%"**: a safety helmet is on the person's head.
- **Dashed amber, "CHECKING…"**: new person, fewer than 3 frames seen.
- **Dashed red, "NO HELMET?"**: looks helmet-less, but not yet for 1.5 s. No alarm.
- **Solid red, "NO HELMET"**: helmet-less for 1.5 s in a row. A red banner appears and the phone vibrates.
- **Dashed grey, "HEAD NOT VISIBLE"**: the head is cut off by the top of the frame, so the person is not judged.
- **"· vest"** is added to the label when a hi-vis vest is visible too.

Each person is followed from frame to frame and judged by a majority vote over their last 5 frames, so one bad frame does not flip the label. A person who disappears for under 1 s (bending down) keeps their history. On 10 test videos of building sites this gave no false alarms on workers with helmets and caught 4 of 5 clips with workers without helmets, 2 to 5.5 s after they appeared.

The top bar shows `People / Helmet / No helmet` counts and how long each frame took. **Save photo** stores the current frame with its boxes, time and counts in `Pictures/TEMPO`, as evidence of a violation. **Flip** switches to the front camera, which is handy for a demo in a classroom.

Everything runs on the phone. No internet, no server, no account, and no images leave the device.

## Install on a phone

1. Copy [`release/tempo-helmet-demo.apk`](release/tempo-helmet-demo.apk) to an Android phone (Android 8.0 or newer), or download it from GitHub on the phone.
2. Open it. Allow "install unknown apps" for your file manager or browser when Android asks.
3. Open **TEMPO Safety** and allow the camera.

No hard hat at hand? Point the camera at a photo of a construction site on a laptop screen.

## How it works

```
camera frame (CameraX, 640x480)
   │
   ├─ PersonDetector ── EfficientDet-Lite0 (MediaPipe, COCO "person") ──► person boxes
   │
   └─ for each person (largest 6, at least 10% of frame height):
        head cut off at the top edge? ──► "head not visible", not judged
        crop (+10% sides, +15% above the head)
        └─ HelmetClassifier ── GearGuardNet (Qualcomm, helmet + vest) ──► helmet on head?
             a helmet counts only if its score ≥ 0.35 and its centre is in the top 45% of the crop
   │
   └─ PersonTracker: match people to last frame, vote over 5 frames, confirm "no helmet" for 1.5 s
   │
   └─ OverlayView draws the boxes; MainActivity updates counts, banner, vibration
```

| File | What it does |
|---|---|
| `mobile/src/main/java/com/tempo/helmetdemo/MainActivity.kt` | Camera, permissions, status bar, alarm, snapshot |
| `.../PersonDetector.kt` | MediaPipe object detector limited to people |
| `.../HelmetClassifier.kt` | Runs GearGuardNet on one person crop with LiteRT |
| `.../SafetyAnalyzer.kt` | Joins the two: people first, then a helmet check per person |
| `.../PersonTracker.kt` | Follows people between frames, votes, confirms violations |
| `.../OverlayView.kt` | Draws the boxes and labels |
| `mobile/src/main/assets/` | The two models |

The thresholds were tuned on a handful of public construction photos (Wikimedia Commons) and checked on 10 free Pexels site videos. It is a prototype: expect misses on people who are very small, far away, or seen from behind, and on old-style hats; a headscarf over a cap is sometimes read as a helmet.

## Models and licences

| Model | Source | Licence |
|---|---|---|
| `efficientdet_lite0.tflite` | [MediaPipe object detector](https://ai.google.dev/edge/mediapipe/solutions/vision/object_detector) | Apache 2.0 |
| `gear_guard_net.tflite` | [Qualcomm AI Hub, PPE-Detection](https://huggingface.co/qualcomm/PPE-Detection) (float TFLite) | BSD-3-Clause |

## Build it yourself

Needs JDK 17 or newer and the Android SDK (platform 35). Either open this `app/` folder in Android Studio and press Run, or from a terminal:

```bash
cd app
./gradlew assembleDebug
```

The APK lands in `mobile/build/outputs/apk/debug/mobile-debug.apk`. If Gradle cannot find the SDK, create `app/local.properties` with `sdk.dir=<path to your Android SDK>` (this file is git-ignored).
