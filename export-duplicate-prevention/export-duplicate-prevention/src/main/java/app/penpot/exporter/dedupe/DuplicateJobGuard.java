package app.penpot.exporter.dedupe;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stops the same export job from being added to the queue twice,
 * so two workers never do the same job.
 *
 * Rules:
 * 1. Every job needs a valid ID (not null, not blank).
 * 2. If a job with that ID is already waiting or running, the new one is skipped.
 * 3. When a job is finished, its ID is free again.
 */
public final class DuplicateJobGuard {

    // IDs of jobs that are waiting or running. The set is safe for many threads.
    private final Set<String> activeJobIds = ConcurrentHashMap.newKeySet();

    /**
     * @return true if the job was added, false if a job with this ID is already active
     * @throws IllegalArgumentException if the ID is null or blank
     */
    public boolean tryAdd(String jobId) {
        checkId(jobId);
        return activeJobIds.add(jobId);
    }

    /** Frees the ID when a job is finished. Unknown IDs are ignored. */
    public void finish(String jobId) {
        checkId(jobId);
        activeJobIds.remove(jobId);
    }

    /** @return true if a job with this ID is waiting or running */
    public boolean isActive(String jobId) {
        checkId(jobId);
        return activeJobIds.contains(jobId);
    }

    private static void checkId(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            throw new IllegalArgumentException("jobId must not be null or blank");
        }
    }
}
