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

## My review
- I read the code and the tests. The class keeps a set of active job IDs, for jobs that are waiting or running.
- tryAdd returns true if the ID was added and false if the ID is already active. This matches the requirement that a duplicate job is skipped.
- The AI used a thread safe set. This means that two threads cannot both add the same ID. I checked that a test sends the same ID 50 times and expects only one success.
- finish removes the ID, so the same job can be added again after it is done. I checked that a test covers this.
- A null or blank ID throws an error. The slide only says bad IDs are rejected, so the error type is the AI's choice.
- The AI keeps one set for both waiting and running jobs. This is simple and matches the requirement, but it does not tell the two states apart.
- I did not run the JUnit tests myself yet. The AI environment could only run a plain Java check of the same cases, and it passed.

## Changes after review
No changes. I kept the AI code after review.
