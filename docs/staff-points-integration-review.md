# Staff and points integration rehearsal

This branch is a review proposal based on `Staff_Information`, not an approved
upgrade for an existing database. Use a fresh disposable database. Do not merge
this alongside all source PRs without reconciling their overlapping changes.

## What was combined

| Source | Snapshot | Retained work |
| --- | --- | --- |
| PR #61, Staff_Information | dc14191 | Staff entity/DTO/mapper/service, CRUD validation, current point-event UI and API |
| PR #63, feature/program-filter | 65c530d | Program entity/repository/API, memberships and filter |
| PR #36, event_log | 2bcf009 | Point entry schema and log API, through the source feature branches |
| PR #53, submission confirmation | cb4275d | Form/review/submission/receipt and calculation from #52 |
| PR #51, eligibility | ea3ebe4 | Threshold lookup |
| PR #50, semester reset | 6a81e42 | Explicit roster reset and history |
| PR #49, reversal | 4b3946b | Reversal and audit history |

Only feature-specific files were ported from the older branches. The latest
staff CRUD implementation was retained; older copies of StaffInfoController,
StaffInfoService and StaffInfoDto were not copied over it.

There is one StaffInfo entity with its two position/supervisor fields and a
shared point balance. Program membership uses the existing join table. Filtering
returns the current StaffInfoDto shape instead of introducing another staff
controller or exposing a second entity model. DTO updates explicitly preserve
program associations. No program-assignment editor is added here.

Integration-specific fixes reject reversals with no staff balance, hide inactive
point events on the entry form, and recheck event activity during both review
and submission. These fixes are isolated to this rehearsal branch.

## Why merging the existing migrations directly fails

| Version | Competing meanings on source branches |
| --- | --- |
| V6 | Staff second position/supervisor versus program tables |
| V7 | Staff column ordering, point-event active flag on an older reset branch, or destructive event-log replacement |
| V9 | Nullable second position/supervisor versus reversal history |
| V11 | Program tables versus submission confirmation columns |

Matching version numbers do not mean matching migration contents. Git conflict
resolution alone does not reconcile an already-applied Flyway history.

## Proposed sequence for a fresh database

The staff branch's V1–V10 files are unchanged. This branch adds:

| Version | Purpose |
| --- | --- |
| V11 | Program schema and the source branch's five program seed names |
| V12 | Widen the staff balance to INT |
| V13 | Semester reset history |
| V14 | Archive the legacy event table, then create the relational point-entry log |
| V15 | Submission token, saved balance and receipt snapshots |
| V16 | Reversal history |

V14 renames the old table to `legacy_event_log`; it does not drop its rows.
The legacy table has name/email/position fields rather than staff/event foreign
keys, so automatically translating it into point entries would require a data
mapping decision. The new application does not expose the archived rows.

The proposal was tested on MySQL 8.4 with `lower_case_table_names=1`, matching
the case-insensitive naming assumed by the historical mixed-case migrations.
Case-sensitive MySQL requires a separate coordinated fix to that old lineage.

**Existing databases:** do not apply this proposal to a database previously
migrated from `event_log`, or one containing another meaning for V6/V7/V9/V11.
Do not delete Flyway history, repair checksums, or rename applied files to force
it through. The team must first inventory the deployed history and data, then
design an upgrade for that exact history. No existing developer database was
changed during this review.

## Validation evidence

- Full Java 17 `clean verify`: **103 tests passed**, executable JAR built.
- Fresh disposable MySQL 8.4: Flyway successfully applied V1–V16.
- A synthetic legacy event inserted before V14 remained in the archived table.
- The combined integration test passed against that Flyway-created MySQL schema,
  with Hibernate DDL disabled.
- Combined API sequence: program filter → NEW semester reset (120 → 65) →
  bonus submission (+60 → 125) → eligibility lookup → reversal (125 → 65).
  Original receipt/history and program membership survive. Duplicate reversal
  and semester reset return 409. Updating staff details preserves membership.

This does not establish concurrent-request behavior, browser layout, email
delivery, authentication, or compatibility with a deployed database history.

## Reproduce

From `LearningCommonsProject`, ordinary tests use isolated H2 databases:

```sh
JAVA_HOME=$(/usr/libexec/java_home -v 17) bash mvnw -B clean verify
```

For the optional MySQL integration test, first create a disposable local
database named `lc_points_review`, then run Flyway with explicit URL/user/password
and `filesystem:src/main/resources/db/migration` as the migration location.
Do not rely on the repository's developer connection defaults. After migration:

```sh
# Set these to the disposable database's URL and disposable root password.
# The test accepts only jdbc:mysql://127.0.0.1:<port>/lc_points_review URLs.
JAVA_HOME=$(/usr/libexec/java_home -v 17) bash mvnw -B \
  -Dtest=StaffPointsIntegrationTest \
  -Dintegration.mysql.url="$REVIEW_MYSQL_URL" \
  -Dintegration.mysql.password="$REVIEW_MYSQL_PASSWORD" test
```

Use a new disposable database for each run; the test inserts a complete review
scenario and deliberately does not delete unrelated records.

## Review sections and team decisions

1. `015f640`: inspect the proposed migration ordering and legacy-table preservation.
2. `6410d8e`: inspect consolidated staff/program contracts and feature ports;
   comments mark DTO membership preservation and activity rechecks.
3. `d6fcebc`: inspect isolated tests, including the combined MySQL-capable scenario.
4. Documentation commit: inspect this guide and the README entry point.

Before adopting this branch, the maintainers of #61/#63/#36 should agree on the
staff DTO/filter contract and migration lineage. The program seed still contains
the five names from #63; the previously requested ten-name catalog is not
invented here and remains a program-PR follow-up. Han's dashboard-specific API
wiring is also separate from this rehearsal.

Choose one integration approach: port these resolutions into the individual
PRs, or review an aggregate PR and explicitly mark overlapping source work as
included. Then reconcile the source PRs rather than merging duplicate copies.
No source PR was closed or merged by this work.
