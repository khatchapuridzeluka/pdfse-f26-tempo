# Homework 2 · Giorgi Gagua (@rubikg6) · TEMPO

Individual, 5 points, due before Lab 8. I didn't write my journal entries during their weeks. I'm writing them now (23 Nov).

## 1. Ship report

What I personally built, wired, deployed, tested, or reviewed since Lab 6.

| What I shipped | Story or rung | Commit link(s), from my account | Live at the production URL? | Walked or checked by |
|---|---|---|---|---|
| Helmet detection demo for Android. It finds people on the phone camera, checks if each one has a helmet, and only raises the alarm after 1.5 seconds without one | S3 (detection side) | https://github.com/khatchapuridzeluka/pdfse-f26-tempo/commit/ead058d, https://github.com/khatchapuridzeluka/pdfse-f26-tempo/commit/01762eb, https://github.com/khatchapuridzeluka/pdfse-f26-tempo/commit/f2ce409 | No. It's on `feat/helmet-detection-demo` and not merged | TT tested it on 30 Oct, the day I pushed it |
| The same demo as a web page, so it also works on an iPhone or a laptop | S3 (detection side) | https://github.com/khatchapuridzeluka/pdfse-f26-tempo/commit/940e3f6 | No. It runs on GitHub Pages (rubikg6.github.io/tempo-helmet-web), not on the team's production URL | TT |

I built the demo with Claude's help. It's in `docs/ai-usage-log.md`.

**The piece I am least sure works for a real person, and how I will know by Lab 8:** The helmet detection on a real site. I only tried it on photos and videos I picked myself. I never ran it on a real construction site, and never on the C220's live video. Before Lab 8 I'll run it on the C220 stream at `site_test` for a set time, count the misses and the false alarms, and put both numbers in `03-build/sprints/sprint-2.md`.

**One thing I did that was not my assigned story:** I made the web version because the Android app only installs on Android phones, and I wanted people with iPhones to be able to try it too: https://github.com/khatchapuridzeluka/pdfse-f26-tempo/commit/940e3f6

## 2. Pattern journal, Weeks 5 to 7

### Week 5 · One metric that matters

I wrote the prototype plan by answering questions, so the words are mine. The riskiest assumption I picked is that construction companies will pay for software that helps them avoid safety fines. To test it we don't build real detection, just one clickable alert on a phone. If 3 of 5 owners don't take the trial or name a price, we stop planning live alerts. Tekla merged it and copied the test into the test plan. `02-design/prototype-plan.md`

### Week 6 · The smallest complete journey

Our MVP boundary had cut camera detection, but I wanted us to see what an automatic check would look like, so I built a helmet demo that runs on a phone camera. Someone only counts as having no helmet after 1.5 seconds in a row, so one bad frame doesn't set off a false alarm. To go fast I used two ready-made models (a MediaPipe person detector and Qualcomm's helmet model) and trained nothing. I only tested it on photos and videos I picked myself, never on a real site. `app/README.md` on `feat/helmet-detection-demo`

### Week 7 · Ship to learn

I shipped nothing live this week. Tekla set up the C220 camera and I didn't touch it, so my demo still runs on phone video and not on the camera stream. That's why S3 is marked not done in `sprint-2.md`. The Wk7 camera line in `DECISIONS.md` uses my demo as evidence. `03-build/sprints/sprint-2.md`, `DECISIONS.md`

## 3. My DECISIONS.md lines

> Wk7 · MVP boundary v2: a Tapo C220 camera streams live video to our detection software; when it sees a worker without a helmet, the owner gets a Telegram message with a snapshot, opens it, gets it fixed and marks it resolved. Reverses the camera cut in the Wk6 MVP boundary line · evidence: 03-build/mvp-boundary.md (Week 7 change), 01-discovery/interview-logs/01-Engineer-Kutaisi.md ("I can't chase every worker every minute"), 07-Site worker-Kutaisi.md (cameras already around the work area), 04-Construction Company Director-Kutaisi.md, 02-design/prototype-plan.md section 2, app/README.md on feat/helmet-detection-demo · owner: TT, LK

I supplied the `app/README.md` evidence, the demo that shows detection working on phone video. It hasn't been tested on a real site yet.

## 4. My person this week

**The field-week person I am responsible for:** New-1, owner or director, from the Georgian construction Facebook groups (test-plan tester 3) · **how and when the link reaches them:** I message them on Facebook, where we found them, then call them, by Thursday. On the call I ask whether their site already has cameras.
