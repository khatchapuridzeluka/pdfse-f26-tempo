# Sprint 1

## Definition of done

A story is done when it is merged to `main` through a pull request, deployed at the public URL (for the detection software: running against the C220), walked on a phone by a teammate who did not build it, and its events show in PostHog's Activity view.

## Real hours before the Week 7 lab

| Initials | Hours free | When, specifically |
|---|---|---|
| LK | 10 | Tue and Thu 19:00 to 22:00, Sat 12:00 to 16:00 |
| TT | 7 | Wed and Fri 19:00 to 21:00, Sun 14:00 to 17:00 |
| GG | 7 | Mon 21:00 to 23:00 (after standup), Thu 19:00 to 21:00, Sat 10:00 to 13:00 |
| **Total** | **24** | |

## The board

Where it lives: GitHub Issues on this repo, https://github.com/khatchapuridzeluka/pdfse-f26-tempo/issues. Four columns: next, doing, review, done. One card per story below. Merge to `main` every day you work.

## Stories

### S1 · Shota resolves the alert · owner TT · reviewer LK · 3 h

**As** Shota, **I** tap Resolved once the violation is fixed **so that** I know it is gone before an inspector finds it.

**Acceptance criteria:**
- Given an open alert at `/o/[token]/a/[id]`, when I tap Resolved, then the alert shows Resolved and the time
- And the `alerts` row has status `resolved` and `resolved_at`; tapping again changes nothing
- And `owner_resolves_alert` fires once from the server with owner_id, site_id, alert_id, minutes_since_alert

**Smallest demo that proves it:** resolve an alert on `site_test` on a phone, show the row, show the event.

### S2 · Shota opens his link and connects Telegram · owner LK · reviewer TT · 4 h

**As** Shota, **I** open my private link and connect Telegram **so that** alerts from my site reach my phone without me checking.

**Acceptance criteria:**
- Given `/o/[token]` on a phone, when it loads, then I see my site and "Watching", or "Camera offline since HH:MM"
- And when I tap Connect Telegram and press Start in the bot, then the page shows "Telegram connected" and my chat id is in `owners`
- And `owner_opens_invite_link` fires once on load with owner_id, site_id, source; `owner_connects_telegram` fires once from the server with owner_id, site_id

**Smallest demo that proves it:** open the link on a phone, connect Telegram, show both events.

### S3 · The camera catches a worker without a helmet · owner GG · reviewer TT · 6 h

**As** Shota, **I** want the camera to notice a worker without a helmet **so that** nobody on site has to report it (log 01).

**Acceptance criteria:**
- Given the detection software running on the C220's live video, when a teammate stands in view without a helmet for 10 seconds, then one `open` alert with one snapshot exists
- And lifting the helmet for 3 seconds creates nothing; while the camera has an open alert, no second alert is created
- And `system_detects_violation` fires once per alert with owner_id, site_id, camera_id, alert_id, violation_type, people_in_view, seconds_confirmed

**Smallest demo that proves it:** take a helmet off in front of the camera, show the alert row and the event.

### S4 · Shota gets the message · owner LK · reviewer GG · 3 h

**As** Shota, **I** get a Telegram message with the photo **so that** I know the same day, not when the inspector comes (log 04).

**Acceptance criteria:**
- Given my Telegram is connected, when an alert is created, then within 60 seconds my Telegram shows the snapshot, the site, the time, "No helmet" and a link
- And with Telegram not connected, the alert still shows on my owner page
- And `system_sends_alert` fires once with owner_id, site_id, alert_id, channel, delivered, seconds_since_detect

**Smallest demo that proves it:** trigger S3, show the message arriving on a phone, show the event.

### S5 · Shota opens the alert · owner TT · reviewer LK · 2 h

**As** Shota, **I** open the alert from the message **so that** I see what is wrong and where.

**Acceptance criteria:**
- Given the link in the message, when I open it on my phone, then I see the snapshot, the site, the time and "No helmet", with no pinch-zoom needed
- And an alert id with another owner's token shows "not found"
- And `owner_views_alert` fires once on load with owner_id, site_id, alert_id, minutes_since_alert, source

**Smallest demo that proves it:** tap the link in Telegram on a phone, show the page and the event.

### S6 · Deploy and instrument · owner LK · reviewer TT · 3 h

Do this story first, on the first evening, with `Lab-6/setup/SHIP-STACK-SETUP.md`, so every later story deploys the day it merges. Public URL in the README's Live product row, every schema event visible in PostHog's Activity view, reconcile check written. The Week 7 lab adds the dashboard.

**Story hours:** 21 of 24. The other 3 are held for reviews and whatever the first walk breaks.

## Slip rule

If S4 is not done by the evening before the Week 7 lab, the Telegram message moves to Sprint 2 and alerts show on Shota's owner page only (`channel` = `screen`); S3 and S1 stay no matter what, because without the camera's alert and the resolve the value does not land.
