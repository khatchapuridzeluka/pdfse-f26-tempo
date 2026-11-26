# Homework 2 · Tekla Tsitelishvili (@ttsitelishvili) · TEMPO

Individual, 5 points, due before Lab 8. Every claim points at a commit, a file, or a DECISIONS.md line.

## 1. Ship report

What I personally built, wired, deployed, tested, or reviewed since Lab 6.

| What I shipped | Story or rung | Commit link(s), from my account | Live at the production URL? | Walked or checked by |
|---|---|---|---|---|
| MVP boundary v1 and the Wk6 boundary line | Lab 6 | https://github.com/khatchapuridzeluka/pdfse-f26-tempo/commit/87c3189 | no (docs) | Talked about it at team meeting |
| Architecture: diagram, stack, journey through the stack | Lab 6 | https://github.com/khatchapuridzeluka/pdfse-f26-tempo/commit/4dd8003 | no (docs) | LK & GG |
| Sprint 1 plan with real hours (24) and six stories | Lab 6 | https://github.com/khatchapuridzeluka/pdfse-f26-tempo/commit/7c6dc6b | no (docs) | Talked about it at team meeting |
| Tapo C220 set up and streaming in VLC, steps in `camera-setup.md` | S3 (camera side) | https://github.com/khatchapuridzeluka/pdfse-f26-tempo/commit/0a43eb5 | no: the stream works on the local network only | LK |
| Tested GG's helmet-detection demo the day he pushed it | S3 (testing) | None from my account (testing only); the demo I tested: https://github.com/khatchapuridzeluka/pdfse-f26-tempo/commit/940e3f6 on `feat/helmet-detection-demo` | no: on a branch, not merged | TT (me, on GG's work) |
| MVP boundary v2 (camera), journey events in the schema, reconcile plan, Wk7 lines | Lab 7, rung 4 prep | https://github.com/khatchapuridzeluka/pdfse-f26-tempo/commit/64581db | no (docs) | Planned at team meeting |
| Sprint 2: Sprint 1 review, gate table, field week, prediction | Lab 7 | https://github.com/khatchapuridzeluka/pdfse-f26-tempo/commit/433ebdf | no (docs) | Talked about it at team meeting |
| Wk7 Telegram line and gate line in DECISIONS.md | Lab 7 | https://github.com/khatchapuridzeluka/pdfse-f26-tempo/commit/21ad6f0 | no (docs) | Talked about it at team meeting |

**The piece I am least sure works for a real person, and how I will know by Lab 8:** the Resolved button (S1, my story, not built yet). Even once it works on `site_test`, I am not sure a real owner will come back to tap it: Owner-1 may open the alert, call his engineer to fix it, and never return to the page, so a fixed violation stays open in our data and the north star undercounts. By Lab 8 I compare `owner_resolves_alert` with `owner_views_alert` for Owner-1's site; if fewer than half of the alerts he opens get resolved, I call him and ask what he did after opening one.

**One thing I did that was not my assigned story:** I set up the Tapo C220 and its live stream, which is part of GG's story S3, so the team learned how the camera works before building on it; that is how we found out the stream only works on the site's local network: https://github.com/khatchapuridzeluka/pdfse-f26-tempo/commit/0a43eb5. I also wrote the architecture before my teammates started building, so each of us knows which part we own: https://github.com/khatchapuridzeluka/pdfse-f26-tempo/commit/4dd8003

## 2. Pattern journal, Weeks 5 to 7

### Week 5 · One metric that matters

I filled in our north star worksheet and rejected the two numbers that would have looked best on a slide: alerts sent, which our system fires, and trial sign-ups, which fix nothing. I kept "alerts resolved per active owner per week", because an alert only matters when the violation is gone before the inspector comes (logs 02 and 04). Outside my lane, I merged Giorgi's prototype plan so the test plan could copy its frozen test word for word, and turned the Lab 5 homework into GitHub issues. My hack: I put two people we had already interviewed, Owner-1 (02) and Director-4 (04), at the top of the tester list, so each first session took one message to book. `DECISIONS.md` (Wk6 north star line), `02-design/user-testing/test-plan.md`

### Week 6 · The smallest complete journey

I did not write this entry in Week 6; I am writing it now. I wrote MVP boundary v1, six steps from the owner's private link to Resolved, with the site engineer's phone photo standing in for a camera, so we could test whether an owner acts on an alert before building any detection. When Luka gave me our Tapo C220, I set it up in the Tapo app, got its live view on my laptop in VLC, and helped Luka set it up himself. Outside my lane, I tested Giorgi's helmet-detection demo on 30 Oct, the day he pushed it, and a working camera plus a working demo became the Week 7 change. `03-build/mvp-boundary.md` (v1, commit 87c3189), `DECISIONS.md` (Wk6 MVP boundary line)

### Week 7 · Ship to learn

v1's open risk was that the engineer would not take the photos, because he "can't chase every worker every minute" (log 01), so I reversed my own Week 6 cut and rewrote the MVP boundary around the camera. The camera taught us that its stream only works on the site's local network, so in the architecture I put the detection software on a laptop on site, and I wrote the camera steps down so anyone on the team can repeat them. I added every journey event to the event schema and wrote Sprint 1, Sprint 2, and the Telegram and gate lines. Outside my lane, I cleaned the repo into the required structure and removed Giorgi's duplicate pattern journal. My hack: VLC as the viewer, so we checked the camera stream in minutes instead of writing a player. `03-build/mvp-boundary.md` (Week 7 change), `03-build/architecture/camera-setup.md`, `03-build/architecture/architecture.md`, `03-build/sprints/sprint-2.md`

## 3. My DECISIONS.md lines

> Wk6 · North star: alerts resolved per active owner per week; rejected safety alerts sent per month (fired by the system, not value) and owners signed up for the trial (presence, cumulative) · evidence: 03-build/analytics/event-schema.md, 01-discovery/interview-logs/01-Engineer-Kutaisi.md, 02-Consturction company owner-Tbilisi.md, 04-Construction Company Director-Kutaisi.md · owner: TT

The evidence is the fines and the "no idea until the inspector arrives" in logs 02 and 04.

> Wk6 · MVP boundary: 6-step journey (owner opens his link to owner marks the alert resolved); cut camera helmet detection, payments, reports dashboard · evidence: 03-build/mvp-boundary.md, 02-design/prototype-plan.md, 01-discovery/interview-logs/01-Engineer-Kutaisi.md, 04-Construction Company Director-Kutaisi.md · owner: TT

Each cut in `03-build/mvp-boundary.md` cites a log or the prototype plan.

> Wk7 · MVP boundary v2: a Tapo C220 camera streams live video to our detection software; when it sees a worker without a helmet, the owner gets a Telegram message with a snapshot, opens it, gets it fixed and marks it resolved. Reverses the camera cut in the Wk6 MVP boundary line · evidence: 03-build/mvp-boundary.md (Week 7 change), 01-discovery/interview-logs/01-Engineer-Kutaisi.md ("I can't chase every worker every minute"), 07-Site worker-Kutaisi.md (cameras already around the work area), 04-Construction Company Director-Kutaisi.md, 02-design/prototype-plan.md section 2, app/README.md on feat/helmet-detection-demo · owner: TT, LK

This reverses my own Wk6 cut; the "Week 7 change" section of `03-build/mvp-boundary.md` records why.

> Wk7 · Alerts reach the owner by a Telegram bot, not WhatsApp or SMS: a Telegram bot is free and set up in minutes, while WhatsApp and SMS need business verification or a paid number; not yet checked whether owners use Telegram, so we ask every field-week owner · evidence: 03-build/mvp-boundary.md (cut list, SMS or WhatsApp row), course repo Lab-6/setup/messages-out.md · owner: LK, TT, GG

Whether owners use Telegram is still unchecked; the field week answers it.

> Wk7 · Gate open at rung 0: missing the production URL, the camera-to-resolved journey on it, owner_resolves_alert firing from production, the PostHog = database count, and the dashboard; owners and times in sprint-2.md · evidence: 03-build/sprints/sprint-2.md, 03-build/analytics/event-schema.md · owner: TT

Every missing rung has an owner and a day in `03-build/sprints/sprint-2.md`.

## 4. My person this week

**The field-week person I am responsible for:** Director-4, founder, Kutaisi (log 04, test-plan tester 2) · **how and when the link reaches them:** not reached yet. Rung 1 is not passed, so there is no production link to send. The day it loads on a phone, I phone him and then send the link on WhatsApp; on the call I ask whether his site cameras can stream to our software.
