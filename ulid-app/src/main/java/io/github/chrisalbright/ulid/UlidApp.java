package io.github.chrisalbright.ulid;

import java.io.PrintStream;
import java.util.Date;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class UlidApp implements Consumer<String[]> {

    private final PrintStream out;
    private final Random random;
    private final Supplier<Long> systemTime;

    public UlidApp(PrintStream out, Random random, Supplier<Long> systemTime) {
        this.out = out;
        this.random = random;
        this.systemTime = systemTime;
    }

    @Override
    public void accept(String[] args) {
        if (args.length == 0) {
            ULID.Generator ulid = new ULID.Generator(random, systemTime);
            this.out.println(ulid.generate());
        } else {
            for (String arg : args) {
                ULID ulid = ULID.parse(arg);
                this.out.println(new Date(ulid.timestamp()));
            }
        }
    }
}
