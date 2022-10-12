import com.chrisalbright.ulid.ULID;

import java.security.SecureRandom;
import java.util.Date;

public class ulid {
    public static void main(String[] args) {
        if (args.length == 0) {
            final SecureRandom r = new SecureRandom();
            ULID.Generator ulid = new ULID.Generator(r, System::currentTimeMillis);
            System.out.println(ulid.generate());
        } else {
            for (String arg : args) {
                ULID ulid = ULID.parse(arg);
                System.out.println(new Date(ulid.timestamp()));
            }
        }
    }
}
