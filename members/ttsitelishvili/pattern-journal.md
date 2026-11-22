# Pattern journal (@ttsitelishvili)

Same prompt every week: Where did you apply this pattern this week, where did you exercise agency beyond your assigned lane, and what did you hack together to test something faster than the plan allowed? Link the DECISIONS.md lines you drove.

## Week 1 · Act like an owner, not a role

- **Applied the pattern:** I  observed peers trying to apply for job applications, trying to assamble teams, and I, myself, felt what it is like to look for a gift for your friend. 
- **Agency beyond assigned lane:** Contributed to organizing team meetings.
- **Hacked faster:** Conducted a rapid manual time-cost audit across my 3 observed scenarios to immediately highlight measurable pain points for our team comparison.
- **Driven DECISIONS.md lines:**
  - `Wk1, Problem pool from Luka, Tekla, and Giorgi · evidence: 00-foundation/problem-pool.md · owner: TT`

---

## Week 2 · Evidence over opinion

- **Applied the pattern:** Objectively evaluated candidate problems using the Four Filters Scorecard, agreeing to pivot team focus toward Problem A, despite my initial focus on student job applications.
- **Agency beyond assigned lane:** Co-authored the scorecard criteria, verifying our team could reach active construction site contacts and supervisors immediately.
- **Hacked faster:** Wrote down testability risks of the candidate problems before filling out the official Four Filters Scorecard markdown.
- **Driven DECISIONS.md lines:**
  - `Wk2 · 00-foundation/four-filters-scorecard.md · runner-up: Junior CS resume and application tailoring · owner: TT`


## Week 3 · Kill your favorite idea

- **Applied the pattern:** My belief in the problem hypothesis was that site managers "lack a structured, lightweight way to track daily safety habits." The interviews narrowed it down. The structure already exists: the specialist leaves a checklist (02), and the company keeps a safety log (04). But nobody uses either one between visits. L.G. told me the safety log "is still done by hand at the last minute right before an inspector arrives" (04). So the problem is not missing structure, it is that nobody follows it up day to day. The other belief, that workers skip PPE because equipment is missing, was killed by my own interview: I.T. said "we had the equipment fully available on-site; it's simply a matter of habit" (01). But I don't know 
- **Agency beyond assigned lane:** Reviewed and merged teammates' pull requests so their Week 3 work was in the repo before the lab, Wrote out tasks to be implemented and distributed them in the team for more productive outcomes.
- **Hacked faster:** L.G. could not meet in person, so I ran interview 04 as a phone call instead of waiting for a site visit. Before the call I rewrote our "we are wrong if" line for a founder instead of a site manager, so the interview could actually test it.
- **Links:**`01-discovery/interview-logs/01-Engineer-Kutaisi.md`


### Week 4 · The prototype is the conversation
Before Milestone 1 I checked every rubric row against our repo and brought the synthesis, competitive landscape and problem statement up to all 7 logs instead of 3. Outside my lane: renamed the logs and the hypothesis file to the required paths and wrote our tracking system into the team contract. Hack: used Claude Code to check the repo against the rubric in one pass, logged in `docs/ai-usage-log.md`. `01-discovery/problem-statement.md`


### Week 5 · Prototype Build and North Star
I ran our north star worksheet and rejected the two numbers that would have looked best on a slide, alerts sent and trial sign-ups, because neither makes the owner's problem smaller. We kept "alerts resolved per active owner per week": an alert only matters when the violation is gone before the inspector comes. Outside my lane: merged Giorgi's prototype plan so the test plan could copy its frozen test word for word, and turned the Lab 5 homework into GitHub issues. Hack: instead of recruiting strangers first, I put two people we had already interviewed, Owner-1 (02) and Director-4 (04), at the top of the tester list, so the first sessions could be booked with one message each. `DECISIONS.md` (Wk6 north star line), `02-design/user-testing/test-plan.md`


### Week 6 · The smallest complete journey

I wrote MVP boundary v1: six steps from the owner opening his private link to him marking the alert resolved, with the site engineer's phone photo standing in for a camera, so we could test whether owner acts on an alert before building any detection. When Luka gave me our Tapo C220, I unboxed it, set it up in the Tapo app on my phone, and got a live view of what the camera sees on my laptop in VLC; then I helped Luka set it up himself. Outside my lane, I tested Giorgi's helmet-detection demo on 30 Oct, the day he pushed it to `feat/helmet-detection-demo`. With a working camera and a working demo, we planned how the detection would connect to the camera, and that plan became the Week 7 change. `03-build/mvp-boundary.md` (v1, commit 87c3189), `DECISIONS.md` (Wk6 MVP boundary line)  - Added in week 7 :(

### Week 7 · Ship to learn

v1's open risk was that the engineer would not take the photos, because he "can't chase every worker every minute" (log 01), so I reversed my own Week 6 cut and rewrote the MVP boundary around the camera: it sees no helmet, the owner gets a Telegram message, and he taps Resolved. What the camera taught us shaped the plan: its stream only works on the site's local network, so in the architecture I put the detection software on a laptop on site, and I wrote the camera steps down so anyone on the team can repeat them. I added every journey event to the event schema and wrote Sprint 1, Sprint 2, and the Telegram and gate lines. Outside my lane, I cleaned the repo into the required structure and removed Giorgi's duplicate pattern journal. My hack: VLC as the viewer, so we checked the camera stream in minutes instead of writing a player. `03-build/mvp-boundary.md` (Week 7 change), `03-build/architecture/camera-setup.md`, `03-build/architecture/architecture.md`, `03-build/sprints/sprint-2.md`, `DECISIONS.md` (Wk7 lines)
