# TEMPO Safety: helmet detection demo (Android)

Point the phone camera at a construction site. The app finds every person in view and marks each one:

- **Green box, "HELMET 91%"**: a safety helmet is on the person's head.
- **Red box, "NO HELMET"**: no helmet found on the head. After 3 frames in a row with a violation, a red banner appears and the phone vibrates.
- **"· vest"** is added to the label when a hi-vis vest is visible too.

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
   └─ for each person (largest 6):
        crop (+10% sides, +15% above the head)
        └─ HelmetClassifier ── GearGuardNet (Qualcomm, helmet + vest) ──► helmet on head?
             a helmet counts only if its score ≥ 0.35 and its centre is in the top 45% of the crop
   │
   └─ OverlayView draws the boxes; MainActivity updates counts, banner, vibration
```

| File | What it does |
|---|---|
| `mobile/src/main/java/com/tempo/helmetdemo/MainActivity.kt` | Camera, permissions, status bar, alarm, snapshot |
| `.../PersonDetector.kt` | MediaPipe object detector limited to people |
| `.../HelmetClassifier.kt` | Runs GearGuardNet on one person crop with LiteRT |
| `.../SafetyAnalyzer.kt` | Joins the two: people first, then a helmet check per person |
| `.../OverlayView.kt` | Draws green/red boxes and labels |
| `mobile/src/main/assets/` | The two models |

The thresholds were tuned on a handful of public construction photos (Wikimedia Commons). It is a prototype: expect misses on people who are very small, far away, or seen from behind, and on old-style hats.

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
