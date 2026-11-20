package app.penpot.exporter.dedupe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

class DuplicateJobGuardTest {

    @Test
    void newJobIsAdded() {
        DuplicateJobGuard guard = new DuplicateJobGuard();
        assertTrue(guard.tryAdd("job-1"));
        assertTrue(guard.isActive("job-1"));
    }

    @Test
    void sameJobIsNotAddedTwice() {
        DuplicateJobGuard guard = new DuplicateJobGuard();
        assertTrue(guard.tryAdd("job-1"));
        assertFalse(guard.tryAdd("job-1"));
    }

    @Test
    void differentJobsAreBothAdded() {
        DuplicateJobGuard guard = new DuplicateJobGuard();
        assertTrue(guard.tryAdd("job-1"));
        assertTrue(guard.tryAdd("job-2"));
    }

    @Test
    void jobCanBeAddedAgainAfterItIsFinished() {
        DuplicateJobGuard guard = new DuplicateJobGuard();
        guard.tryAdd("job-1");
        guard.finish("job-1");
        assertFalse(guard.isActive("job-1"));
        assertTrue(guard.tryAdd("job-1"));
    }

    @Test
    void finishingAnUnknownJobDoesNothing() {
        DuplicateJobGuard guard = new DuplicateJobGuard();
        guard.finish("never-added");
        assertFalse(guard.isActive("never-added"));
    }

    @Test
    void nullIdIsRejected() {
        DuplicateJobGuard guard = new DuplicateJobGuard();
        assertThrows(IllegalArgumentException.class, () -> guard.tryAdd(null));
    }

    @Test
    void blankIdIsRejected() {
        DuplicateJobGuard guard = new DuplicateJobGuard();
        assertThrows(IllegalArgumentException.class, () -> guard.tryAdd("   "));
        assertThrows(IllegalArgumentException.class, () -> guard.tryAdd(""));
    }

    @Test
    void sameJobSentManyTimesAtOnceIsAddedOnlyOnce() throws Exception {
        DuplicateJobGuard guard = new DuplicateJobGuard();
        ExecutorService pool = Executors.newFixedThreadPool(8);
        List<Callable<Boolean>> tasks = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            tasks.add(() -> guard.tryAdd("job-1"));
        }
        int added = 0;
        for (Future<Boolean> result : pool.invokeAll(tasks)) {
            if (result.get()) {
                added++;
            }
        }
        pool.shutdown();
        assertEquals(1, added);
    }
}
