package io.github.chrisalbright.ulid;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;


public class ULID implements Comparable<ULID>, Serializable {

    public static final long MAX_TIMESTAMP = 0x0000_FFFF_FFFF_FFFFL;
    public static final long ROLLOVER_VALUE = 0xFFFF_FFFF_FFFF_FFFFL;
    public static final long MSB_MASK = 0xFFFFL;
    public static final int BASE32_BITS = 5;
    public static final int BASE32_MASK = 0x1F;

    public static char[] ENCODE_TABLE = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q',
            'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z',
    };

    private final long msb;
    private final long lsb;

    public static class Generator {
        private final Random r;
        private final Supplier<Long> systemTime;
        private ULID value;
        private Long lastCallTime;

        public Generator(Random r, Supplier<Long> systemTime) {
            this.r = r;
            this.systemTime = systemTime;
            this.lastCallTime = Long.MIN_VALUE;
        }

        private ULID next() {
            value = value.next();
            return value;
        }

        private ULID generate(long ts) {
            value = new ULID(ts, r.nextLong(), r.nextLong());
            return value;
        }

        public ULID generate() {
            long callTime = systemTime.get();
            if (callTime == lastCallTime) {
                return next();
            } else if (callTime < 0) {
                throw new NegativeCallTimeException(String.format("Negative time value %d is not supported", callTime));
            }
            lastCallTime = callTime;
            return generate(callTime);
        }
    }

    private ULID(long timestamp, long msb, long lsb) {
        if (timestamp > MAX_TIMESTAMP) {
            throw new OverflowException("Timestamp exceeds maximum value of " + MAX_TIMESTAMP);
        }
        if (timestamp < 0) {
            throw new UnderflowException("Timestamp precedes minimum value of " + 0);
        }
        this.msb = msb & MSB_MASK | (timestamp << 16);
        this.lsb = lsb;
    }

    private ULID(long msb, long lsb) {
        this.msb = msb;
        this.lsb = lsb;
    }

    public byte[] toBytes() {
        byte[] bytes = new byte[16];
        System.arraycopy(Util.toBytes(msb), 0, bytes, 0, 8);
        System.arraycopy(Util.toBytes(lsb), 0, bytes, 8, 8);
        return bytes;
    }

    public ULID next() {
        boolean incrementMSB = lsb == ROLLOVER_VALUE;
        long nextMsb = msb;
        if (incrementMSB) {
            if ((msb & 0xFFFF) == 0xFFFF) {
                throw new OverflowException("Unable to increment random portion of MSB");
            }
            nextMsb += 1;
        }
        long nextLsb = lsb + 1;
        return new ULID(nextMsb, nextLsb);
    }

    public long timestamp() {
        return msb >>> 16;
    }

    public long msb() {
        return msb;
    }

    public long lsb() {
        return lsb;
    }

    @Override
    public int compareTo(ULID o) {
        Objects.requireNonNull(o);
        int cmp = Long.compare(msb, o.msb);
        return cmp == 0 ? Long.compare(lsb, o.lsb) : cmp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ULID ulid = (ULID) o;
        return msb == ulid.msb && lsb == ulid.lsb;
    }

    @Override
    public int hashCode() {
        return Objects.hash(msb, lsb);
    }

    @Override
    public String toString() {
        char[] data = new char[26];
        // extract timestamp from msb
        base32Encode(msb >>> 16, 10, data, 0);
        // remaining data is 80 random bits, 16 in the low part of the msb, and 64 in the lsb
        // shift these bits into one spot to make the encoding easier
        base32Encode((msb & 0xFFFFL) << 24 | (lsb >>> 40), 8, data, 10);
        // The remaining 40 random bits are in the lsb
        base32Encode(lsb, 8, data, 18);

        return new String(data);
    }

    private static void base32Encode(long l, int blocks, char[] buffer, int offset) {
        final int bitsToEncode = (blocks - 1) * BASE32_BITS;
        for (int i = 0; i <= bitsToEncode; i += BASE32_BITS) {
            int shift = bitsToEncode - i;
            int index = (int) (l >>> shift) & BASE32_MASK;
            buffer[offset + i / BASE32_BITS] = ENCODE_TABLE[index];
        }
    }

    private static long base32Decode(String s) {
        long v = 0;
        char[] chars = s.toCharArray();
        final int length = chars.length;
        for (int i = 0; i < length; i++) {
            switch (chars[i]) {
                case '0':
                case 'O':
                case 'o':
                    v |= (long) 0 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case '1':
                case 'I':
                case 'i':
                case 'L':
                case 'l':
                    v |= (long) 1 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case '2':
                    v |= (long) 2 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case '3':
                    v |= (long) 3 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case '4':
                    v |= (long) 4 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case '5':
                    v |= (long) 5 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case '6':
                    v |= (long) 6 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case '7':
                    v |= (long) 7 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case '8':
                    v |= (long) 8 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case '9':
                    v |= (long) 9 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case 'A':
                case 'a':
                    v |= (long) 10 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case 'B':
                case 'b':
                    v |= (long) 11 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case 'C':
                case 'c':
                    v |= (long) 12 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case 'D':
                case 'd':
                    v |= (long) 13 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case 'E':
                case 'e':
                    v |= (long) 14 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case 'F':
                case 'f':
                    v |= (long) 15 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case 'G':
                case 'g':
                    v |= (long) 16 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case 'H':
                case 'h':
                    v |= (long) 17 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case 'J':
                case 'j':
                    v |= (long) 18 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case 'K':
                case 'k':
                    v |= (long) 19 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case 'M':
                case 'm':
                    v |= (long) 20 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case 'N':
                case 'n':
                    v |= (long) 21 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case 'P':
                case 'p':
                    v |= (long) 22 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case 'Q':
                case 'q':
                    v |= (long) 23 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case 'R':
                case 'r':
                    v |= (long) 24 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case 'S':
                case 's':
                    v |= (long) 25 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case 'T':
                case 't':
                    v |= (long) 26 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case 'V':
                case 'v':
                    v |= (long) 27 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case 'W':
                case 'w':
                    v |= (long) 28 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case 'X':
                case 'x':
                    v |= (long) 29 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case 'Y':
                case 'y':
                    v |= (long) 30 << ((length - 1 - i) * BASE32_BITS);
                    break;
                case 'Z':
                case 'z':
                    v |= (long) 31 << ((length - 1 - i) * BASE32_BITS);
                    break;
            }
        }

        return v;
    }
    public static ULID parse(String ulid) {
        long ts = base32Decode(ulid.substring(0, 10));
        long hi = base32Decode(ulid.substring(10, 18));
        long lo = base32Decode(ulid.substring(18, 26));
        long msb = (ts << 16) | (hi >>> 24);
        long lsb = lo | (hi << 40);
        return new ULID(msb, lsb);
    }
    public static ULID fromBytes(byte[] bytes) {
        long msb = Util.fromBytes(bytes);
        long lsb = Util.fromBytes(bytes, 8);
        return new ULID(msb, lsb);
    }

    public static ULID minFor(Date date) {
        return minFor(date.toInstant());
    }

    public static ULID maxFor(Date date) {
        return maxFor(date.toInstant());
    }

    public static ULID minFor(Instant instant) {
        return minFor(instant.toEpochMilli());
    }


    public static ULID maxFor(Instant instant) {
        return maxFor(instant.toEpochMilli());
    }

    public static ULID minFor(long ts) {
        return new ULID(ts, 0, 0);
    }
    public static ULID maxFor(long ts) {
        return new ULID(ts, -1, -1);
    }

}