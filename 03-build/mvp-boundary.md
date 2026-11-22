# MVP Boundary

The smallest complete user journey that tests whether the product creates value. Complete: the person gets the value, start to finish. Smallest: nothing that is not on that path is in Sprint 1.

**Value hypothesis under test:** An owner who gets a message with a photo when a camera sees a worker without a helmet, on the days between the specialist's visits, will get violations fixed before an inspector finds them (problem statement, sentences 2, 3 and 5; logs 01, 02, 04).

**The one person walking this journey:** Shota, 56, runs a mid-sized construction company with sites in Tbilisi and Kutaisi; his safety specialist visits each site every 4–5 days, and between visits he only hears about violations when an inspector finds them.

## The journey

1. Shota opens his private owner link, sees his site and the camera's status ("Watching"), and taps "Connect Telegram" so alerts reach his phone
2. A Tapo C220 camera on the site streams live video to our detection software, which checks every worker in view for a helmet
3. When the software sees a worker without a helmet, Shota gets a Telegram message with a snapshot from the camera, the site, the time and a link
4. Shota opens the alert on his phone
5. Shota gets it fixed on site and taps "Resolved"

**Why this is complete:** After step 5, a helmet violation that used to stay on site until the inspector arrived is gone, and Shota knew about it the same day instead of having "no idea" until the Labor Inspection comes (log 04). Nobody on site had to report it.

**Why this is smallest:** We considered removing step 5. Without it, the journey ends at a message our system sent, which says nothing about whether the violation was fixed (the Wk6 north star line rejected "alerts sent" for this reason). We also considered dropping the message in step 3 and letting Shota check a page instead, but an alert he has to go looking for is the same as today, when he learns about violations only when the inspector does (log 04).

**Open risks (we check each one in the field week):**
- **False alarms.** On 10 public site videos the detector raised 0 false alarms on 5 helmet clips and caught 4 of 5 no-helmet clips (`app/README.md` on branch `feat/helmet-detection-demo`). Those were videos, not a fixed camera on a real site. In the field week we look at every snapshot from a real site and count the false ones.
- **Getting a camera onto a real site:** power, Wi-Fi, the owner's agreement, and telling the workers.
- **Whether owners use Telegram.** The ICP coordinates by phone and WhatsApp. We ask every field-week owner.

## Cut list

| Feature | Reason (cite the log, session, or hypothesis) | Comes back when |
|---|---|---|
| A role for the safety specialist | He is on site one day in four (02, 03); the journey is about the days he is not there, and we work alongside him, not instead of him | If sessions show owners want the specialist to see the fixes |
| An app or notifications for workers | Workers don't pay the fines (01, 07) and treat helmets as a personal choice (07); the owner is who our problem statement is about | Not planned |
| Litter and site-order as a separate category | Lower cost so far (02, 05); `violation_type = other` covers it | If "other" becomes a large share of alerts |
| Checking work against architectural drawings | One log only (05), and a different problem from site safety | If more logs raise it |
| Several sites per owner | The trial is one site for one month (prototype plan, screen 3) | After the trial |
| Login with a password | A private owner link reaches every step; owners coordinate by phone (04) and nobody asked for an account | If a link is ever shared by mistake |
| SMS or WhatsApp delivery of alerts | Both need business verification or a paid number (course repo `Lab-6/setup/messages-out.md`); a Telegram bot is free and works in minutes | If field-week owners don't use Telegram |
| Camera helmet detection (cut in Week 6) | The prototype test was about whether owners want the alert, not whether we can detect a helmet (prototype plan, section 3) | Came back in Week 7: it is now step 2 (see "Week 7 change") |
| A camera photo proving the fix | For now "Resolved" is Shota's word | If field-week alerts are marked resolved while the violation is still on camera |
| Fence, harness and litter detection | Fences are the most common violation (03, 05), but no ready model detects them; helmets test the same hypothesis with a model we already run (GearGuardNet) | After owners resolve helmet alerts for 2 weeks |
| Recording or storing video, the camera's siren | The hypothesis needs one snapshot per alert, not footage; workers don't pay the fines (01, 07) | Not planned |
| Payments | The hypothesis is tested without money moving; the prototype test asks whether the owner would try a one-month trial or what he would pay (test plan) | After the pricing test (Lab 10) |
| Reports dashboard | The value is one violation fixed before the inspector comes, not a report; no log asked for reports | If owners prefer a weekly summary to live alerts (the alternative offer in the test plan's decision rule) |

## Step to event

| Step | Event (actor_verb_object) | In the schema? |
|---|---|---|
| 1 | `owner_opens_invite_link`, `owner_connects_telegram` | yes / added Wk7 |
| 2 | `system_detects_violation` | added Wk7 |
| 3 | `system_sends_alert` | added Wk7 |
| 4 | `owner_views_alert` | yes |
| 5 | `owner_resolves_alert` ★ | yes |

**North star step:** 5

## The DECISIONS.md line

`Wk7 · MVP boundary v2: a Tapo C220 camera streams live video to our detection software; when it sees a worker without a helmet, the owner gets a Telegram message with a snapshot, opens it, gets it fixed and marks it resolved. Reverses the camera cut in the Wk6 MVP boundary line · evidence: 03-build/mvp-boundary.md (Week 7 change), 01-discovery/interview-logs/01-Engineer-Kutaisi.md ("I can't chase every worker every minute"), 07-Site worker-Kutaisi.md (cameras already around the work area), 04-Construction Company Director-Kutaisi.md, 02-design/prototype-plan.md section 2, app/README.md on feat/helmet-detection-demo · owner: TT, LK`

## Week 7 change

v1 (Week 6) had the site engineer photograph violations by hand; it is in git history and in the Wk6 MVP boundary line of `DECISIONS.md`. v2 (above) replaces the engineer's photo with a camera that checks for helmets by itself. Why: v1's open risk was that the engineer would not take the photos, because he "can't chase every worker every minute" (log 01). Many sites already have cameras around the work area (log 07). And our prototype's one action was always "an alert with a photo when a person on site is not wearing a helmet" (`02-design/prototype-plan.md`, section 2). Removed with v1: `engineer_reports_violation`, `engineer_uploads_fix`, `owner_calls_engineer` (Shota calls from his own phone, as he does today).
