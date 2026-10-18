# Affinity Map - Customer Discovery Synthesis

**Team Name:** TEMPO
**Date:** 18 October 2026
**Interviews Analyzed:** 3 (Interviews 2, 3, 5)
**Lab:** Lab 4 - Week 4

**Miro board:** https://miro.com/app/board/uXjVJ1DMmxI=/?share_link_id=665865061975
**Board export:** [affinity-map-photo.png](affinity-map-photo.png)

---

## Overview

We took every answer from the interview logs, put each insight on its own sticky note in Miro, and grouped the notes by what they say. Each cluster is named as a pattern: one sentence stating what the evidence shows.

| Interview | Who | Date |
|---|---|---|
| 2 | Owner, mid-sized construction company (Tbilisi) | 4 Oct 2026 |
| 3 | Safety specialist at the same company | 6 Oct 2026 |
| 5 | Construction team lead (Tbilisi) | 9 Oct 2026 |

Only statements the interviewees made are used. Answers marked *(assumption)* or *(from Interview 2)* in Logs 3 and 5 are not counted as evidence.

**Tool used:** Miro affinity diagram

---

## Clusters (named as patterns)

### Pattern 1: Safety slips in the days between the specialist's visits, because nobody is checking

**Interviews:** 2, 3 (2 of 3)

**Insights in this cluster:**
1. The specialist comes once every 4 days and tells workers what not to violate; nothing is checked in between. (Interview 2)
2. The company paid about 150,000 GEL in fines over 4 months because workers repeated the same violations and the specialist was not always on site. (Interview 2)
3. The specialist leaves a checklist, but nobody makes sure it is followed between visits. (Interview 2)
4. The specialist says he cannot be on site 24 hours a day or watch for simple violations at every moment. (Interview 3)

**What it shows:** The owner and the specialist describe the same gap from both sides: rules and guidance exist, but no one enforces them between visits.

---

### Pattern 2: Protection is available on site but gets undone during normal work

**Interviews:** 2, 3, 5 (3 of 3)

**Insights in this cluster:**
1. PPE is almost always available; workers don't wear all of it because it is uncomfortable. (Interview 2)
2. Missing edge fences are the most common violation: they are removed while lifting materials and not put back. (Interview 3)
3. The team removed an edge fence to lift something and nobody put it back. (Interview 5)

**What it shows:** The equipment is not missing. It is taken off or left off while the work moves on, and nobody notices afterwards.

---

### Pattern 3: Site-order violations like litter count too, not only PPE and fences

**Interviews:** 2, 5 (2 of 3)

**Insights in this cluster:**
1. The latest labor inspection gave a warning for litter around the site, while workers were wearing full PPE. (Interview 2)
2. The team lead names litter left around the site as a common violation. (Interview 5)

**What it shows:** Inspectors look at the whole site. A team can get PPE right and still be warned for site order.

---

## Outliers

1. **Work that doesn't match the architectural drawings** (Interview 5)
   - Why it's unique: it is a design-compliance problem, not a site-safety one, and needs checking against plans rather than rules.
   - Decision: set aside for now; note it and ask about it in the next interviews.

2. **The violation was found by the inspector, not by the team** (Interview 2)
   - Why it's unique: only one interview says who found the violation, so it can't be a pattern yet.
   - Decision: worth exploring; Script v2 asks "How did you find out?" for this reason.

---

## Cluster relationships

```
[Pattern 2: protection is undone during work]
        ↓  nobody notices because
[Pattern 1: no one checks between visits]
        ↓  so the inspector finds it
[Pattern 3 + outlier 2: warnings and fines, including for site order]
```

Pattern 2 is how violations start; Pattern 1 is why they stay unfixed.

---

## Surprises and hypothesis check

**What surprised us:**
- The most common violation is edge fences, not PPE (Interviews 3, 5).
- Violations come from forgetting during work, not from refusing the rules (Interview 5).

**What validated our hypothesis:**
- Compliance slips without oversight, and fines are a large, real cost (Interviews 2, 3).

**What contradicted our hypothesis:**
- We assumed PPE was missing on site; it is available (Interview 2).
- We assumed workers lacked guidance; the specialist leaves a checklist (Interview 2).

---

## Strongest patterns (preliminary)

1. **Pattern 1: safety slips between the specialist's visits**, 2 of 3 interviews, largest cost
2. **Pattern 2: protection is available but gets undone during work**, 3 of 3 interviews
3. **Pattern 3: site-order violations count too**, 2 of 3 interviews

These are analyzed further in [patterns-analysis.md](patterns-analysis.md).

---

## Next steps

1. Export the Miro board as `affinity-map-photo.png` and put it in `/01-discovery/synthesis/`.
2. Add Interviews 1 and 4 to the board and update the counts.
3. Use `patterns-analysis.md` to draft the problem statement.

---

**Document status:** Draft
**File location:** `/01-discovery/synthesis/affinity-map.md`