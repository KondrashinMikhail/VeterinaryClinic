package storage;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordCoder {
    private String PASSWORD = "123";

    public void crypt() {
        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(PASSWORD, salt);

        if (BCrypt.checkpw("123", hashedPassword)) {
            System.out.println("Hashed password: " + hashedPassword);
            System.out.println("Password: 123");
            System.out.println("OKAY");
        }
        else {
            System.out.println("ERROR");
        }
    }

    public String generateHashedPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean confirmPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
