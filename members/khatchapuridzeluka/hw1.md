# Homework 1 · Luka Khachapuridze (@khatchapuridzeluka) · Team TEMPO

## 1. My interview log

| Log file | My role | What this interview changed |
|---|---|---|
| `01-discovery/interview-logs/02-Consturction company owner-Tbilisi.md` | asked | Killed both of our side beliefs: PPE is on site but workers find it uncomfortable, and the specialist does leave a checklist. Gave us the real cost: about 150,000 GEL in fines over 4 months from repeated violations. |
| `01-discovery/interview-logs/03-Safety specialist-Tbilisi.md` | asked | Moved the problem away from PPE: the most common violation is edge fences removed while lifting materials and not put back. Confirmed the gap from his side: he can't be on site all the time. |
| `01-discovery/interview-logs/05-Construction team lead-Tbilisi.md` | asked | Showed the cause from inside the crew: the fence comes off for a real reason (lifting) and is forgotten, not refused. Added litter and a new outlier: work not matching the architectural drawings. |

**The quote I heard myself that mattered most:**

> "I can't be on site 24 hours a day or watch for simple violations like this every moment." (03, reconstructed from notes)

**One moment I broke the Mom Test:** In log 02 I discussed our solution idea with the owner during the interview, so his reactions became compliments and opinions about the future instead of facts about the past. The log records that those reactions are not counted as evidence, and Script v2 has no solution questions; it only asks about the last real case, such as "Tell me about the last time safety rules were not followed. How did you find out?"

## 2. Pattern journal, Weeks 1 to 4

### Week 1 · Act like an owner, not a role
Brought the construction safety problem from my father's company, D Group: a 5,000 GEL fine and a 14-day work stoppage for a PPE violation that had already been spotted and never fixed. Outside my lane: [what you did beyond your role in Week 1]. Hack: [one shortcut you used]. `00-foundation/problem-pool.md`

### Week 2 · Evidence over opinion
Scored the candidate problems with the Four Filters; construction safety won over junior CS resume tailoring because we could reach site owners and specialists right away through my father's network. Outside my lane: set up the outreach list in the ICP and contacted the owner, a large company in Saburtalo and D Group's safety specialist myself. Hack: [one shortcut you used]. `00-foundation/four-filters-scorecard.md`

### Week 3 · Kill your favorite idea
My belief was that workers skip essential PPE (helmets, guardrails) when the specialist is away. Half of it died: PPE is on site, and workers skip it because it is uncomfortable, not because it is missing (02). The guardrail part got stronger, but for a different reason: fences are removed during normal work to lift materials and nobody puts them back (03, 05). Outside my lane: reviewed a teammate's Script v2 and flagged citations that did not match the logs. Hack: got the specialist's number directly from the owner at the end of interview 02 and interviewed him two days later. `01-discovery/interview-logs/03-Safety specialist-Tbilisi.md`

### Week 4 · The prototype is the conversation
Turned Logs 2, 3 and 5 into the first synthesis: three patterns in the affinity map, ranked in the patterns analysis, and the first draft of the problem statement. Wrote the Week 3 verdict into the problem hypothesis and `DECISIONS.md`, and updated the ICP to v2 with evidence lines. Outside my lane: fixed our repo workflow after edits kept going to a OneDrive copy that git does not track. Hack: used Claude to draft the synthesis files from the logs, [logged in `docs/ai-usage-log.md`]. `01-discovery/synthesis/patterns-analysis.md`

## 3. My DECISIONS.md citations

| The line, copied | What I did for it |
|---|---|
| `Wk2 · Investigate: construction site safety compliance and PPE violations in Georgia · evidence: 00-foundation/problem-pool.md (Luka), 00-foundation/four-filters-scorecard.md · runner-up: junior CS resume and application tailoring · owner: LK, TT` | Wrote Statement 1 in the problem pool, the fine and stoppage this line's evidence starts from. |
| `Wk3 · Prediction "site managers tell us that workers maintain full PPE compliance without oversight, or that fines from unannounced inspections are rare/negligible risks they actively ignore" verdict: confirmed · evidence: 01-discovery/interview-logs/01-Engineer-Kutaisi.md, 02-Consturction company owner-Tbilisi.md, 03-Safety specialist-Tbilisi.md, 04-Construction Company Director-Kutaisi.md, 05-Construction team lead-Tbilisi.md · next: refine the problem around edge fences and site order, not only PPE · owner: LK` | Asked three of its five evidence logs (02, 03, 05) and wrote the verdict. |
| `Wk4 · Problem statement v1: owners of mid-sized construction companies pay fines and daily delay penalties for simple, repeated violations because nobody checks the site between the safety specialist's visits, and safety is only performed when an inspector is expected · evidence: 01-discovery/problem-statement.md, 01-discovery/synthesis/patterns-analysis.md · owner: LK, TT, GG` | Wrote the first patterns analysis and problem statement draft from Logs 2, 3 and 5. |