# Sprint 2: the field week

Sprint 2 runs from Lab 7 to Lab 8. Real people use the product this week, and Lab 8 opens by reading what they did.

## Sprint 1 review

Our definition of done: merged to `main` through a pull request, deployed at the public URL (for the detection software: running against the C220), walked on a phone by a teammate who did not build it, events in PostHog's Activity view.

The Sprint 1 stories were written on 22 Nov, after we switched to the camera (MVP boundary v2), so no code was written for them before the Week 7 lab. There is no `app/` folder on `main` and no production URL.

| Story | Owner | Done by our definition? | If not: which clause is missing, and what happened |
|---|---|---|---|
| S1 Shota resolves the alert | TT | No | Every clause: the alert page and the Resolved button are not built; no code on `main` or on a branch |
| S2 Owner link + Telegram | LK | No | Every clause: the owner page and the Telegram bot are not built; no code on `main` or on a branch |
| S3 Camera catches no helmet | GG | No | "running against the C220": the pipeline works in the demo on phone video (`feat/helmet-detection-demo`, 30 Oct), not on the C220's live video. TT set up the C220 and watched its stream in VLC on the local network (`03-build/architecture/camera-setup.md`, 22 Nov), but the pipeline is not connected to that stream yet, and the branch is not merged |
| S4 Shota gets the message | LK | No | Every clause: the Telegram message is not built; it also needs S2 (the chat id) and S3 (the alert) |
| S5 Shota opens the alert | TT | No | Every clause: the alert page is not built; it needs the link from S4 |
| S6 Deploy and instrument | LK | No | "deployed at the public URL" and "events in PostHog's Activity view": no Vercel project, no database, no PostHog project yet |

**Slip rule:** fired. S4 was not done by the evening before the Week 7 lab, so the Telegram message moved to Sprint 2, and until it is built alerts show on Shota's owner page only (`channel` = `screen`). S3 and S1 were not done either; they stay, as written.

## The gate

| Rung | Reached? | Evidence: a link or a number | If not: owner, and by when |
|---|---|---|---|
| 1 Production URL loads on a phone | Partly | https://web-inky-five-8pqepadzuq.vercel.app loads publicly (checked 27 Nov from a laptop) | Phone on mobile data: LK or GG, at the next walk |
| 2 Journey completes there | No | The journey works on public construction footage ("Use a video instead"), tested locally on 26 Nov | LK or GG walks it on a phone: video → alert → Open → Resolved |
| 3 North star event in Activity, from production | Yes | `owner_resolves_alert` in PostHog Activity on 27 Nov, sent by the live server (test record `site_test`) | |
| 4 Counts reconcile | No | Test alert so far: PostHog 1 = database 1 | TT, in the window of the phone walk (`event-schema.md`, Gate reconcile) |
| 5 Dashboard, linked in README | Partly | "TEMPO gate: helmet alerts" with Trends, Funnel, Retention and the internal filter | TT: turn on the public link and put it in the README |

**Gate change (27 Nov):** the gate demo runs on public construction videos instead of the C220's live stream, as our lecturer allowed; the C220 stays for the field week (see the DECISIONS.md line).

## Real hours before Lab 8

| Initials | Hours free | When, specifically |
|---|---|---|
| LK | 10 | Tue and Thu 19:00 to 22:00, Sat 12:00 to 16:00 |
| TT | 7 | Wed and Fri 19:00 to 21:00, Sun 14:00 to 17:00 |
| GG | 7 | Mon 21:00 to 23:00 (after standup), Thu 19:00 to 21:00, Sat 10:00 to 13:00 |
| **Total** | **24** | |

## Stories this week

Missing rungs first, then what slipped from Sprint 1; no new story. Sum: 24 of 24 hours. No hours are held this time; if the first walk breaks something, F1 moves to the next weekend and R5 goes first.

### S6 · Deploy and instrument (rung 1) · owner LK · reviewer TT · 3 h

Slipped from Sprint 1. Done on Tue evening, with the course repo's `Lab-6/setup/SHIP-STACK-SETUP.md`, when the production URL loads on a phone on mobile data and is in the README's Live product row.

### S3, S4, S2, S5, S1 · Camera to resolved alert (rungs 2 and 3) · owners as in Sprint 1 · reviewer TT · 18 h

Slipped from Sprint 1, same criteria and hours (S3 6 h, S2 4 h, S4 3 h, S1 3 h, S5 2 h). Done when, on `site_test`, a teammate takes his helmet off in front of the C220, the owner phone gets the Telegram message, opens it, the helmet goes back on, the owner taps Resolved, and `owner_resolves_alert` shows in PostHog Activity from production. Walked by TT on a phone on mobile data.

### R4 · Reconcile and internal filter (rung 4) · owner TT · reviewer LK · 1 h

Done when the Gate reconcile block in `03-build/analytics/event-schema.md` is filled in, PostHog equals the database, the `Live` column is filled, and the internal filter is set (`site_id` is not `site_test`, Host does not contain `localhost`).

### R5 · Dashboard (rung 5) · owner TT · reviewer GG · 1 h

Done when the dashboard has Trends on `owner_resolves_alert`, a Funnel `system_sends_alert` → `owner_views_alert` → `owner_resolves_alert`, and Retention on `owner_resolves_alert`, each with the internal filter, shared as a public link in the README's Analytics dashboard row.

### F1 · Camera on one real site · owner LK · reviewer TT · 1 h

On Sat, after the journey is walked on `site_test`. Done when the C220 and the laptop are installed at Owner-1's site with his written yes and a sign for the workers (`03-build/architecture/camera-setup.md`), and his owner page shows "Watching" for a full working day.

## Field week: who uses it

3 to 5 real people from our ICP, not classmates. Code names and roles only, as in our logs. Each gets the production URL, never a preview link. An owner only produces real events if a camera watches his site, so we also ask each one whether his site already has cameras (log 07).

| Code name and role | Log or session they come from | How the link reaches them | Sent by, and by when | Opened? (fill in during the week) |
|---|---|---|---|---|
| Owner-1, owner, mid-sized company, Tbilisi | Log 02, test-plan tester 1 | In person when F1 installs the camera; we connect Telegram on his phone with him | LK, Sat | |
| Director-4, founder, Kutaisi | Log 04, test-plan tester 2 | Phone call, then the link on WhatsApp; we ask whether his site cameras can stream to our software | TT, Wed | |
| New-1, owner or director | Test-plan tester 3 (Georgian construction Facebook groups) | Message on Facebook, where we found them, then a call; same camera question | GG, Thu | |

**What we tell them:** "This is the safety alert we showed you, now working with a camera on your site. When the camera sees someone without a helmet, you get a photo on Telegram; tap Resolved when it is fixed. It is a student project: we count alerts and taps, never names, and photos are deleted after 30 days."

## Prediction

Written in the lab, before the week starts. Never edited afterwards; Lab 8 reads it as written.

**By Lab 8:** 3 `owner_resolves_alert` events from real people, not our team, all from Owner-1's site, because it is the only site with our camera. Also: 5 `system_detects_violation` on Owner-1's site.

**We are wrong if:** Owner-1 opens fewer than half of his alerts (`owner_views_alert` below half of `system_sends_alert` for his site).

**Mid-week look:** TT opens the dashboard on Sun, after Owner-1's first day with the camera, and writes one line here: what the counts are and one thing we change because of them, or nothing.

## The DECISIONS.md line

`Wk7 · Gate open at rung 0: missing the production URL, the camera-to-resolved journey on it, owner_resolves_alert firing from production, the PostHog = database count, and the dashboard; owners and times in sprint-2.md · evidence: 03-build/sprints/sprint-2.md, 03-build/analytics/event-schema.md · owner: TT`

When the gate closes, a new "Gate passed" line links this one; this line is never edited.
