# Positive point input review guide

PR #52 adds a reusable calculation service. Callers enter a positive integer;
Bonus events produce positive adjustments and Infraction events produce
negative adjustments within the event's configured signed range.

## Scope and validation

Production behavior is unchanged by this review update. The focused suite now
has 28 passing cases, covering allowed bounds, fixed ranges, case-insensitive
event types, missing event/amount, zero and negative input, out-of-range
amounts, unsupported types, reversed ranges, invalid range signs, and the
largest positive Java integer. Invalid input returns HTTP 400; invalid event
configuration returns HTTP 409.

From `LearningCommonsProject`:

```sh
JAVA_HOME=$(/usr/libexec/java_home -v 17) bash mvnw -B clean -Dtest=PointCalculationServiceTest test
```

These are calculation unit tests. They do not start a database, exercise a
form, persist an adjustment, or validate MySQL migrations.

## Relationship to PR #53

The original service and 13 tests in #52 were copied into the submission
confirmation branch through a cherry-pick. Both files were byte-for-byte
identical when compared before this update. The additional tests are also
carried into #53 so the shared implementation and coverage remain aligned.

Review #52 as the calculation foundation and #53 as its submission integration.
Do not reimplement the service or apply the original commit a second time.
If #52 merges first, reconcile #53 against the updated base and retain one
copy of each file. If #53 merges first with this work included, verify #52
has no remaining functional changes and then let the maintainer decide
whether to close it as superseded. Neither PR is automatically closed or
merged by this update.

The service returns a signed adjustment; adding it to a staff balance and
handling balance overflow are responsibilities of the submission service.
An input field backed by Java `Integer` cannot represent the magnitude
2147483648, even if an event range includes Integer.MIN_VALUE. The largest
representable entered amount is 2147483647; it negates safely.
