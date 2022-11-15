package com.chrisalbright.ulid;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;
import java.util.function.Supplier;

public class UlidAppTest {

    @Test
    public void ulidAppTest() {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outStream, true);
        Random r = new Random(0);
        Random r2 = new Random(0);
        Supplier<Long> sysTime = () -> 0L;

        String expectedUlid = new ULID.Generator(r, sysTime).generate().toString();

        new UlidApp(out, r2, sysTime).accept(new String[]{});

        Assertions.assertEquals(expectedUlid, outStream.toString().trim());

    }

}
