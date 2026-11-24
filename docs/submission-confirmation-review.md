# Submission confirmation review guide

PR #53 provides `/entries/new` → review → submit → saved receipt.
Positive point input becomes a signed bonus or infraction adjustment. Review
does not write to the database; submission saves the event and staff balance
in one service transaction.

## Review sections

1. **Package alignment:** staff, program, staff repository, and staff controller
   use the team's singular `entity`, `repository`, and `controller` packages.
   Existing program associations and endpoints are preserved. This is not a
   merge of every change from the current staff branch.
2. **Missing balances:** `currentPoints` uses nullable `Integer`, matching the
   current staff model. Review and submission return HTTP 409 for a missing
   balance instead of failing while calculating. Submission rechecks because
   the staff record can change after review.
3. **Flow tests:** real Spring MVC controllers, rendered Thymeleaf templates,
   repositories, and service transactions run together against isolated H2.
   There is no enclosing test transaction, so database assertions observe
   committed writes. Comments explain this choice in the test setup.

## Validation

From `LearningCommonsProject`:

```sh
JAVA_HOME=$(/usr/libexec/java_home -v 17) bash mvnw -B clean -Dtest=EventSubmissionFlowTest,EventSubmissionServiceTest,PointCalculationServiceTest test
```

The focused suite contains 41 cases: 9 flow checks, 4 existing submission
service cases, and 28 point calculation cases. Flow checks cover:

- Bonus and infraction form → review → saved receipt, including balance and
  history assertions, receipt refresh, repeat POST, and opening another form.
- Editing preserved fields and rejecting an obsolete review token.
- Missing balance rejection at review and again at submission.
- Recalculating a balance changed after review.
- Overflow and an event deleted after review, with no saved entry or balance change.
- Infraction details validation, expired drafts, and a missing receipt.

## Limits and integration work

H2 tests disable Flyway and create their own schema. They do not verify MySQL
migrations, browser layout, concurrent requests, authentication, or email.
The existing blank home page is outside these changes; the tested entry point
is `/entries/new`.

This branch depends on `event_log` and includes the point calculation work
from #20. The shared calculation service and its 28 tests match PR #52;
review that PR as the calculation foundation rather than a second implementation.
Its staff model retains the program association from that branch;
coordinate it with the staff and program PRs before merging.

Migration V11 on this branch conflicts with V11 on the program branch. That
conflict still requires a coordinated integration decision, especially if
either migration has already been applied. This update does not rename or
rewrite applied migrations, repair Flyway history, or merge the PR.
