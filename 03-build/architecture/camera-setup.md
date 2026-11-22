# Camera setup: Tapo C220

How we got live video from the camera. No passwords, IP addresses or stream links in this file; they live in `app/detector/.env`.

| Item | Detail |
|---|---|
| Camera | TP-Link Tapo C220 (Wi-Fi, pan and tilt) |
| Viewer | VLC media player |

## Steps

1. Set up the camera in the Tapo app and connect it to the Wi-Fi.
2. Tapo app: the camera, Settings, Advanced Settings, **Camera Account**: create a username and password (separate from the Tapo cloud account).
3. Find the camera's IP address (Tapo app device info, or the router's device list).
4. VLC: **Media, Open Network Stream**: `rtsp://<camera-account>:<password>@<camera-ip>:554/stream1` (full quality) or `stream2` (lower quality, used by the detection software).
5. The computer must be on the same Wi-Fi as the camera; the stream does not work from outside that network.



## Before the camera goes on a real site

-  The owner agrees in writing
-  Power and Wi-Fi at the mounting spot
-  A laptop that stays on next to it for the detection software
-  Workers are told, and a visible sign is up; we check what Georgia's personal data law requires for workplace video before installing
-  The Tapo app's own recording (SD card, cloud) stays off