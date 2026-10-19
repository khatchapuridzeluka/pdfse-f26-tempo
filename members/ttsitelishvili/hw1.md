# Homework 1 · Tekla Tsitelishvili (@ttsitelishvili) · Team TEMPO

## 1. My interview log

| Log file | My role | What this interview changed |
|---|---|---|
| `01-discovery/interview-logs/01-Engineer-Kutaisi.md` | asked | Killed our "PPE is missing" belief: the gear is on site, workers skip it out of habit, and between visits the specialist's feedback lives only in the engineer's word. |
| `01-discovery/interview-logs/04-Construction Company Director-Kutaisi.md` | asked | Gave us the real cost (a fine that stops a site brings delay penalties of up to 5,000 GEL a day) and showed that safety is staged for the inspector. |

**The quote I heard myself that mattered most:**

> "As the founder, I have no idea whether workers on-site are actually wearing helmets and harnesses today, until the Labor Inspection arrives." (04)

**One moment I broke the Mom Test:** In log 01 I asked "what is usually the main reason, a lack of equipment or just force of habit?", an either/or question that handed him the answer. Script v2 replaced it with "Tell me about the last time safety rules were not followed. How did you find out?"

## 2. Pattern journal, Weeks 1 to 4

### Week 1 · Act like an owner, not a role
Observed peers applying for jobs and assembling teams, and felt myself what it is like to look for a gift for a friend. Outside my lane: helped organize team meetings. Hack: a quick manual time-cost audit across my 3 observed scenarios to show measurable pain points. `00-foundation/problem-pool.md`

### Week 2 · Evidence over opinion
Scored the candidate problems with the Four Filters and agreed to move to construction safety, though my own pick was student job applications. Outside my lane: co-wrote the scorecard criteria and checked we could reach site contacts right away. Hack: wrote down each problem's testability risks before filling in the scorecard. `00-foundation/four-filters-scorecard.md`

### Week 3 · Kill your favorite idea
My belief was that site managers lack a lightweight way to track safety. The interviews showed the structure exists, a checklist (02) and a safety log (04), but nobody follows it up between visits: the log "is still done by hand at the last minute right before an inspector arrives" (04). My own interview killed "PPE is missing": "we had the equipment fully available on-site; it's simply a matter of habit" (01). Outside my lane: reviewed and merged teammates' pull requests before the lab and split the week's tasks. Hack: ran interview 04 by phone instead of waiting for a site visit, with the "we are wrong if" line rewritten for a founder. `01-discovery/interview-logs/04-Construction Company Director-Kutaisi.md`

### Week 4 · The prototype is the conversation
Before Milestone 1 I checked every rubric row against our repo and brought the synthesis, competitive landscape and problem statement up to all 7 logs instead of 3. Outside my lane: renamed the logs and the hypothesis file to the required paths and wrote our tracking system into the team contract. Hack: used Claude Code to check the repo against the rubric in one pass, logged in `docs/ai-usage-log.md`. `01-discovery/problem-statement.md`

## 3. My DECISIONS.md citations

| The line, copied | What I did for it |
|---|---|
| `Wk2 · Investigate: construction site safety compliance and PPE violations in Georgia · evidence: 00-foundation/problem-pool.md (Luka), 00-foundation/four-filters-scorecard.md · runner-up: junior CS resume and application tailoring · owner: LK, TT` | Co-wrote the Four Filters scorecard that is this line's evidence. |
| `Wk3 · Prediction "site managers tell us that workers maintain full PPE compliance without oversight, or that fines from unannounced inspections are rare/negligible risks they actively ignore" verdict: confirmed · evidence: 01-discovery/interview-logs/01-Engineer-Kutaisi.md, 02-Consturction company owner-Tbilisi.md, 03-Safety specialist-Tbilisi.md, 04-Construction Company Director-Kutaisi.md, 05-Construction team lead-Tbilisi.md · next: refine the problem around edge fences and site order, not only PPE · owner: LK` | Asked two of its five evidence logs (01, 04). |
| `Wk4 · Problem statement v1: owners of mid-sized construction companies pay fines and daily delay penalties for simple, repeated violations because nobody checks the site between the safety specialist's visits, and safety is only performed when an inspector is expected · evidence: 01-discovery/problem-statement.md, 01-discovery/synthesis/patterns-analysis.md · owner: LK, TT, GG` | Brought the problem statement and patterns analysis it cites up to Logs 1–7. |