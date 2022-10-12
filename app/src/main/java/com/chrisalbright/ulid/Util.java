package com.chrisalbright.ulid;

import java.util.Arrays;

public class Util {
    public static byte[] inc(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            int j = data.length - 1 - i;
            boolean incrementNextByte = (data[j] & 0xFF) == 0xFF;
            data[j]++;
            if (!incrementNextByte) {
                return data;
            }
        }
        byte[] newData = new byte[data.length + 1];
        newData[0] = 0;
        System.arraycopy(data, 0, newData, 1, data.length);
        return newData;
    }

    public static long fromBytes(byte[] b) {
        return fromBytes(b, 0);
    }

    public static long fromBytes(byte[] b, int offset) {
        assert (b.length - offset >= Long.BYTES);
        long l = 0;
        for (int i = 0; i < Long.BYTES; i++) {
            l = (l << 8) | (b[i + offset] & 0xFF);
        }
        return l;
    }

    public static byte[] toBytes(long l) {
        byte[] bytes = new byte[Long.BYTES];
        bytes[0] = (byte) ((l >> 56) & 0xFF);
        bytes[1] = (byte) ((l >> 48) & 0xFF);
        bytes[2] = (byte) ((l >> 40) & 0xFF);
        bytes[3] = (byte) ((l >> 32) & 0xFF);
        bytes[4] = (byte) ((l >> 24) & 0xFF);
        bytes[5] = (byte) ((l >> 16) & 0xFF);
        bytes[6] = (byte) ((l >> 8) & 0xFF);
        bytes[7] = (byte) (l & 0xFF);
        return bytes;
    }

    public static void p(long l, String... s) {
        for (String ss : s) {
            System.out.print(ss + " ");
        }
        System.out.println();
        System.out.printf("Value: %d%n", l);
        System.out.printf("Bytes: %s%n", Arrays.toString(toBytes(l)));
        System.out.printf("Binary: %s%n", toBinaryString(l));
    }

    public static String toBinaryString(long l) {
        final char[] tail = Long.toBinaryString(l).toCharArray();
        int tailBit = 0;
        final int offset = Long.SIZE - tail.length;
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Long.SIZE; i++) {
            if (i < offset) {
                sb.append('0');
            } else {
                sb.append(tail[tailBit++]);
            }
            if ((i + 1) % Byte.SIZE == 0) {
                sb.append(' ');
            }
        }
        return sb.toString();
    }

}
