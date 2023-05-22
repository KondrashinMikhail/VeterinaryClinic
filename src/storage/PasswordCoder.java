package storage;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordCoder {
    public void crypt() {
        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw("mk", salt);

        if (BCrypt.checkpw("mk", hashedPassword)) {
            System.out.println("Hashed password: " + hashedPassword);
            System.out.println("Password: mk");
            System.out.println("OKAY");
        }
        else {
            System.out.println("ERROR");
        }
    }

    public static String generateHashedPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean confirmPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
