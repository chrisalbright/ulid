package ulid;

import com.chrisalbright.ulid.OverflowException;
import com.chrisalbright.ulid.ULID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class ULIDTest {

    @Test
    public void ulidsGeneratedInSameMillisecondAreMonotonicallyIncreasing() {
        long currentTimestamp = System.currentTimeMillis();
        Supplier<Long> sameMillisecondGenerator = () -> currentTimestamp;
        Random r = new Random(1L);
        ULID.Generator ulid = new ULID.Generator(r, sameMillisecondGenerator);
        ULID u1 = ulid.generate();
        ULID u2 = ulid.generate();

        Assertions.assertEquals(u1.timestamp(), u2.timestamp());
        Assertions.assertEquals(u1.lsb() + 1, u2.lsb());
    }

    @Test
    public void ulidsGeneratedInDifferentMillisecondsAreRandom() {
        Supplier<Long> differentMillisecondGenerator = new Supplier<>() {
            long currentTimestamp = System.currentTimeMillis();

            @Override
            public Long get() {
                return currentTimestamp++;
            }
        };
        Random r = new Random();

        ULID.Generator ulid = new ULID.Generator(r, differentMillisecondGenerator);
        ULID u1 = ulid.generate();
        ULID u2 = ulid.generate();

        Assertions.assertNotEquals(u1.timestamp(), u2.timestamp());
        Assertions.assertNotEquals(u1.next(), u2);
    }

    @Test
    public void ulidMinMax() {
        String minUlidStr = "00000000000000000000000000";
        String maxUlidStr = "7ZZZZZZZZZZZZZZZZZZZZZZZZZ";
        ULID expectedMin = ULID.parse(minUlidStr);
        ULID expectedMax = ULID.parse(maxUlidStr);

        ULID generatedMin = ULID.minFor(0);
        ULID generatedMax = ULID.maxFor(ULID.MAX_TIMESTAMP);

        Assertions.assertEquals(expectedMin, generatedMin);
        Assertions.assertEquals(expectedMax, generatedMax);
    }

    @Test
    public void ulidsSort() {
        final int idCount = 1000000;
        ULID[] ids = new ULID[idCount];
        ULID[] sortedIds = new ULID[idCount];
        Supplier<Long> differentMillisecondGenerator = new Supplier<>() {
            long currentTimestamp = System.currentTimeMillis();

            @Override
            public Long get() {
                return currentTimestamp++;
            }
        };
        Random r = new Random();
        ULID.Generator ulid = new ULID.Generator(r, differentMillisecondGenerator);
        for (int i = 0; i < ids.length; i++) {
            ids[i] = ulid.generate();
            sortedIds[i] = ids[i];
        }
        Arrays.sort(sortedIds);
        Assertions.assertArrayEquals(ids, sortedIds);
    }

    @Test
    public void randomPartMustNotRollOver() {
        Assertions.assertThrows(OverflowException.class, () -> {
            ULID.maxFor(Instant.now()).next();
        });
    }

    @Test
    public void ulidsGenerateQuickly() {
        final int idCount = 10_000_000;
        ULID[] ids = new ULID[idCount];
        Supplier<Long> differentMillisecondGenerator = new Supplier<>() {
            long currentTimestamp = System.currentTimeMillis();

            @Override
            public Long get() {
                return currentTimestamp++;
            }
        };
        Random r = new Random();
        ULID.Generator ulid = new ULID.Generator(r, differentMillisecondGenerator);
        final long start = System.nanoTime();
        for (int i = 0; i < ids.length; i++) {
            ids[i] = ulid.generate();
        }
        final long end = System.nanoTime();
        final long runTime = end-start;
        Assertions.assertTrue(runTime <= TimeUnit.MILLISECONDS.toNanos(1200));
    }

}
