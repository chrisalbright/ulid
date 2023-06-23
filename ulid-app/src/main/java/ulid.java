import io.github.chrisalbright.ulid.UlidApp;

import java.security.SecureRandom;

public class ulid {
    public static void main(String[] args) {
        new UlidApp(System.out, new SecureRandom(), System::currentTimeMillis).accept(args);
    }
}
