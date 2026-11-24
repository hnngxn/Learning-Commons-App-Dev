# Semester reset review guide

PR #50 resets only the selected roster: NEW employees start at 65 points,
RETURNING employees start at 70, and employees omitted from the request keep
their balance. One reset batch is allowed per semester year and term.

## Review order

1. Migration: the reset migration is now V13, avoiding the staff branch's V10
   sample-data migration. SQL contents are unchanged.
2. Tests: transaction tests cover saved balances/history, excluded employees,
   deterministic employee order, invalid requests, repeated semesters, and
   rollback when a later employee is missing or has no balance. Controller
   tests cover HTTP 201, request binding failures, and HTTP 409.
3. This guide records validation and integration limits.

## Run the focused checks

From `LearningCommonsProject`:

```sh
JAVA_HOME=$(/usr/libexec/java_home -v 17) bash mvnw -B clean -Dtest=SemesterPointResetTransactionTest,SemesterPointResetControllerTest test
```

Result: 22 tests passed. H2 is a test-only dependency. Transaction tests run
outside an enclosing test transaction so the service's actual transaction
commits or rolls back before the assertions read the database. These tests
disable Flyway and do not validate MySQL migration syntax or concurrent requests.

## Migration compatibility

The staff branch already uses V10. Other feature branches use V11 and V12;
V13 was unused in the remote branches inspected on November 23, 2026.
Coordinate migration numbers again before merging concurrent feature branches.

A separate disposable MySQL 8.4 SQL smoke check applied the latest
`Staff_Information` V1–V10 SQL followed by this V13. It created reset history
successfully and rejected a duplicate semester through the unique constraint.
The check used `lower_case_table_names=1` because the historical migrations mix
table-name casing. This was a SQL check, not a Flyway upgrade test.

If an earlier version of this branch's reset V10 was applied to a local test
database, use a fresh disposable database for review. Renaming the file does
not migrate existing Flyway history. Do not delete history or run repair to
hide that mismatch; an existing deployed database needs a coordinated upgrade.

## Remaining scope

This PR depends on the staff model. It provides the backend reset operation;
authentication, email notifications, and the reset UI are separate work.
The tests do not establish production concurrency behavior or full application
integration with every other open feature branch.
