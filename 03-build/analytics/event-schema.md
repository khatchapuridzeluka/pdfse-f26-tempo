# Event Schema

Your metric is only as good as your instrumentation. Every row here becomes one `capture()` call in Week 7 and one dashboard tile. Names are copied from this file, never retyped.

**North star:** Alerts resolved per active owner per week.

**North star event:** `owner_resolves_alert`

**How the north star is computed from events:** count of `owner_resolves_alert`, per owner with at least one `owner_views_alert` in the window, per calendar week (Monday to Sunday). A week covers at least one full gap between the safety specialist's visits (01, 02).

## Events

| Step | Event (actor_verb_object) | Fired by | Properties (never a name, phone, email, photo, or message text) | Owner | Live |
|---|---|---|---|---|---|
| Acquisition | `owner_opens_invite_link` | owner (browser, once on load of the owner page) | owner_id, site_id, source (whatsapp, in_person, other) | LK | yes, 27 Nov (browser, production) |
| Activation (setup) | `owner_connects_telegram` | owner (server, when the bot receives /start with the owner's token) | owner_id, site_id | LK | no: the Telegram bot is not built (slip rule) |
| Journey step 2 | `system_detects_violation` | system (server, after the alert row is inserted) | owner_id, site_id, camera_id, alert_id, violation_type (no_helmet), people_in_view, seconds_confirmed | GG | yes, 27 Nov (server, production, test record) |
| Journey step 3 | `system_sends_alert` | system (server, after the Telegram call returns) | owner_id, site_id, alert_id, channel (telegram, screen), delivered (true, false), seconds_since_detect | LK | yes, 27 Nov, `channel` = `screen` only (server, production, test record) |
| Activation (precursor) | `owner_views_alert` | owner (browser, once on load of the alert page) | owner_id, site_id, alert_id, minutes_since_alert, source (telegram, list) | TT | not yet from production: fires when the alert is opened; seen in the local test on 26 Nov |
| Activation (north star) ★ | `owner_resolves_alert` | owner (server, after the update returns a row) | owner_id, site_id, alert_id, minutes_since_alert | TT | yes, 27 Nov (server, production, test record) |
| Retention | `owner_resolves_alert` again in a later week | Retention insight on the north star event, no new event | | TT | |

## Rules we are following

- One name per event, one spelling, lowercase with underscores.
- `owner_id` (our own id, like `o_7f3a`) on every event. Nothing that identifies a real person.
- Never on an event: snapshots or their URLs, names, phone numbers, Telegram chat ids, camera IP addresses, RTSP links.
- The north star event is fired by the person who receives the value.

## Reconcile plan

Before we believe the dashboard, we count the north star event a second way.

**Event most likely to fail silently:** `system_detects_violation`
**How it would fail:** double fire: the detector retries a POST that timed out, the server inserts the alert twice, and one violation becomes two alerts and two Telegram messages.
**The check that would catch it:** count the rows in `alerts` for the same window and compare them to the event count; also check that no camera has two `open` alerts at the same time.
**Who runs the check in Week 7:** TT



## Gate reconcile (Lab 7)

**Production URL walked:** https://web-inky-five-8pqepadzuq.vercel.app · **walked by:** [initials of LK or GG, who did not build the north star step] · on a phone, mobile data: [yes or no]

**So far (27 Nov):** one test alert on `o_test` / `site_test` through the live API: created once, the retried create inserted nothing, resolved once, a second resolve changed nothing. PostHog Activity shows one `owner_resolves_alert` from production; the database has one resolved alert. Recount both in the window of the phone walk below.

| Source | Count | Window | Exactly how it was counted |
|---|---|---|---|
| PostHog | [N] | [start] to [end] | Trends insight, total count of `owner_resolves_alert` |
| Database | [N] | same | `select count(*) from alerts where status = 'resolved' and resolved_at >= '[start]' and resolved_at < '[end]';` |

**Match:** [yes or no]

**If no, first:** [the two numbers, what caused the gap, what we changed (commit link), and the two numbers after the fix. Keep the wrong numbers here.]

**Silent failure we predicted:** double fire of `system_detects_violation` · **Did it happen?** [yes or no, and how we know] · **Guard built in:** the database allows one open alert per camera, so a retried or repeated create inserts nothing and fires nothing (checked on 27 Nov)

**Internal filter set in PostHog:** `site_id is not site_test` and `Host does not contain localhost` · **our test record is:** owner `o_test`, site `site_test`

**Checked by:** [initials] · **events still not Live, with owner and by when:** `owner_connects_telegram` (LK, needs the Telegram bot); `owner_views_alert` from production (first phone walk)