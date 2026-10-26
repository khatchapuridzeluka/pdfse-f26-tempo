# Prototype Test Plan

**Prototype v1 link:** [same link as the Prototype row in README.md]

**Riskiest assumption under test:** * Owners lose a lot to fines and delay penalties (02, 04), and nobody we interviewed uses any software for this. But no log shows what an owner would pay to fix it. No log tests this yet, which is why the prototype must.


## The frozen test (copied from `02-design/prototype-plan.md`, section 5, unchanged)

**Task, read aloud word for word:** "This alert just arrived on your phone from one of your sites. Show me what you would do." Then: "We can run this on one of your sites for a month. Would you try it?"

**Success:** he agrees to the one-month trial, or says what he would pay, in the session.

**Failure behaviors:**
1. He says "interesting" but gives no yes and no price.
2. He says the safety specialist already covers it.
3. He asks us to come back later.
4. He ignores the alert.

**Decision rule (out of 5):** if 3 or more of 5 owners fail, we stop planning to sell live alerts and test a different offer first, such as a weekly photo summary.

**Predicted first mistake:** he will ask whether it replaces the safety specialist.

## Five testers

All five are owners or directors of construction companies (our ICP). None are in this course. Code names only; real names and numbers stay in the team chat.

| # | Code name | Role (as in our ICP) | Why they fit the ICP | Session runner (asks) | Session logger (writes) | Target day | Status (messaged / booked / done) |
|---|---|---|---|---|---|---|---|
| 1 | Owner-1 | Owner of a mid-sized construction company (Tbilisi) | Log 02. Visiting safety specialist every 4 days; about 150,000 GEL in fines in 4 months | LK | GG | Wed 28 Oct | Called |
| 2 | Director-4 | Founder and managing director, residential construction, Kutaisi | Log 04. Delay penalties up to 5,000 GEL a day; has "no idea" about PPE until the inspector arrives | TT | GG | Thur 29 Oct | messaged |
| 3 | New-1 | Owner or director, construction company | New, from the Georgian construction Facebook groups. Confirm on the call that they run active sites with a visiting safety specialist | GG | TT | Wed 28 Oct | messaged |
| 4 | New-2 | Owner or director, construction company, Kutaisi | New, from a Kutaisi site visit. Confirm they pay the fines and are not on site daily | TT | LK | Thu 29 Oct | to message |
| 5 | New-3 | Owner, construction company | New, from Luka's father's network. Same check as above | LK | TT | Fri 30 Oct | to message |


## Session protocol (same every time)

1. The tester opens the link on their own phone. Not ours.
2. The runner reads the task word for word, then says nothing.
3. The logger starts the timer on the last word of the task and writes: time to first tap, time to the one action, every unprompted sentence verbatim (in Georgian, with the translation added afterwards).
4. When the tester stops, or at 3 minutes, the runner asks one question only: "What were you expecting to happen?"
5. The logger writes the verdict against the rule before anyone discusses the session.
6. The session goes into `02-design/user-testing/usability-findings.md` the same day, one block per session.
