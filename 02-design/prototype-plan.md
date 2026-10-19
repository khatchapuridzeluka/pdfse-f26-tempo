# Prototype Plan

## 1. The riskiest assumption

> We assume that construction companies will pay for software that helps them avoid safety rule violations, because it saves them money.

**Why this one is riskiest:** Owners lose a lot to fines and delay penalties (02, 04), and nobody we interviewed uses any software for this. But no log shows what an owner would pay to fix it. No log tests this yet, which is why the prototype must.

## 2. The one action

> An owner can see an alert, with a photo, when a person on site is not wearing a safety helmet.

## 3. Fidelity

| Choice (paper / clickable / coded) | Why it is the cheapest thing that can kill the assumption in section 1 | Tool | Time we will spend |
|---|---|---|---|
| Clickable | The assumption is about paying, not about whether we can detect a helmet. A pre-made alert on a phone shows the owner exactly what he would get, and real detection would take days the test does not need. | AI prototyping tool, shared as a link | One evening |

## 4. Screens, each traced to evidence

| Screen | What the tester does on it | The log or problem-statement sentence that earns it |
|---|---|---|
| 1 | Sees the alert: a photo of a person in a cap, "No helmet, Site B, 10:42" | The owner only learns what happened when the inspector does (04) |
| 2 | Chooses what to do with it: call the site engineer, or dismiss | Owners phone site engineers to check before audits (04) |
| 3 | Is offered a one-month trial on one site, and answers | Fines and delay penalties cost owners the most (02, 04) |

**Will not appear, even if a tool generates it:**
- Real helmet detection from a camera
- Reports or charts

## 5. The test that can fail

- **Task, read aloud to the tester, word for word:** "This alert just arrived on your phone from one of your sites. Show me what you would do." Then: "We can run this on one of your sites for a month. Would you try it?"
- **Success looks like:** he agrees to the one-month trial, or says what he would pay, in the session.
- **Failure looks like:** he says "interesting" but gives no yes and no price; he says the safety specialist already covers it; he asks us to come back later; he ignores the alert.
- **Decision rule:** if 3 or more of 5 owners fail, we stop planning to sell live alerts and test a different offer first, such as a weekly photo summary.
- **Our prediction of the first thing a tester gets wrong:** he will ask whether it replaces the safety specialist.

## 6. Five testers

People from our customer group, not friends.

| Code name | From log, or new | Who on the team books them |
|---|---|---|
| Owner-2 | log 02 | LK |
| Director-4 | log 04 | TT |
| New-1 | new, from the construction Facebook groups | GG |
| New-2 | new, a Kutaisi site visit | TT |
| New-3 | new, from Luka's father's network | LK |
