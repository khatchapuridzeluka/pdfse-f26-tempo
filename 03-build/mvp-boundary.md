# MVP Boundary

The smallest complete user journey that tests whether the product creates value. Complete: the person gets the value, start to finish. Smallest: nothing that is not on that path is in Sprint 1.

**Value hypothesis under test:** An owner who gets a photo alert of a safety violation on his phone on the days between the specialist's visits, and sees the fix confirmed with a second photo, will get violations fixed before an inspector finds them (problem statement, sentences 2, 3 and 5; logs 01, 02, 04).

**The one person walking this journey:** Shota, 56, runs a mid-sized construction company with sites in Tbilisi and Kutaisi; his safety specialist visits each site every 4–5 days, and between visits he only hears about violations when an inspector finds them.

## The journey

1. Shota opens his private owner link and adds one site; he forwards the site's engineer link to his site engineer on WhatsApp
2. The site engineer sees a violation (a helmet off, an edge fence removed after lifting), photographs it and sends it with the violation type
3. Shota sees the alert on his phone: the photo, the site, the time, the violation type
4. Shota taps "Call site engineer" and tells him to fix it
5. The site engineer fixes it and uploads a photo of the fix
6. Shota sees the fix photo and marks the alert resolved

**Why this is complete:** After step 6 a violation that used to stay on site until the inspector arrived is gone, and Shota has seen it gone himself instead of having "no idea" until the Labor Inspection comes (log 04).

**Why this is smallest:** We considered removing step 5. Without the fix photo, "resolved" is the engineer's word again, which is exactly the verbal oversight that fails between visits: "Until the next visit, monitoring that issue relies solely on my verbal oversight" (log 01).

**Open risk:** Log 01 says the engineer "can't chase every worker every minute". Whether an engineer or foreman will take the photos in step 2 is untested; we ask it in every test session.

## Cut list

| Feature | Reason (cite the log, session, or hypothesis) | Comes back when |
|---|---|---|
| A role for the safety specialist | He is on site one day in four (02, 03); the journey is about the days he is not there, and we work alongside him, not instead of him | If sessions show owners want the specialist to see the fixes |
| An app or notifications for workers | Workers don't pay the fines (01, 07) and treat helmets as a personal choice (07); the owner is who our problem statement is about | Not planned |
| Litter and site-order as a separate category | Lower cost so far (02, 05); `violation_type = other` covers it | If "other" becomes a large share of alerts |
| Checking work against architectural drawings | One log only (05), and a different problem from site safety | If more logs raise it |
| Several sites per owner | The trial is one site for one month (prototype plan, screen 3) | After the trial |
| Login with a password | A private owner link reaches every step; owners coordinate by phone (04) and nobody asked for an account | If a link is ever shared by mistake |
| SMS or WhatsApp delivery of alerts | Step 4's call uses the phone Shota already phones engineers with (04); the hypothesis is tested once he opens the alert | If sessions show owners miss alerts because they don't open the link |

## Step to event

| Step | Event (actor_verb_object) | In the schema? |
|---|---|---|
| 1 | `owner_opens_invite_link` | yes |
| 2 | `engineer_reports_violation` | added today (fired by engineer; site_id, alert_id, violation_type; owner LK) |
| 3 | `owner_views_alert` | yes |
| 4 | `owner_calls_engineer` | yes |
| 5 | `engineer_uploads_fix` | added today (fired by engineer; site_id, alert_id, minutes_since_alert; owner LK) |
| 6 | `owner_resolves_alert` ★ | yes |

**North star step:** 6

## The DECISIONS.md line

`Wk6 · MVP boundary: 6-step journey (owner opens his link to owner marks the alert resolved); cut camera helmet detection, payments, reports dashboard · evidence: 03-build/mvp-boundary.md, 02-design/prototype-plan.md, 01-discovery/interview-logs/01-Engineer-Kutaisi.md, 04-Construction Company Director-Kutaisi.md · owner: TT`
