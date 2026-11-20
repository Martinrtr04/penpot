# AI interaction log: Export Duplicate Prevention (M4S8)

Feature: Export Duplicate Prevention.
Spec source: Group D M4S6 experiment slide: stops the same export job from being added
to the queue twice, so two workers never do the same job. Every job needs a valid ID.
If a job with that ID is already waiting or running, the new one is skipped.

## Prompt 1 (context and requirements)
Asked the AI for a small Java 21 module (Maven, JUnit 5), package `app.penpot.exporter.*`,
in the style of the `export-job-queue` module. I gave the requirements above.

## AI output
- `DuplicateJobGuard` with `tryAdd(id)`, `finish(id)`, `isActive(id)`
- 8 JUnit tests (add, duplicate, two different jobs, add again after finish,
  unknown finish, null or blank ID, 50 threads sending the same ID)

## My review (write this in your own words)
- The AI chose one set of "active" IDs for both waiting and running jobs. Check: is this enough?
- The AI used a thread safe set, so two threads cannot both add the same ID.
- Choice to check: a null or blank ID throws IllegalArgumentException (the slide only says "rejected").
- Not verified by the AI environment: the JUnit tests were not run (no access to Maven Central).
  A plain Java check of the same cases passed, including 200 rounds of the 50 thread test.
  Run `mvn test` to confirm.

## Changes after review
(none yet, add yours here)
