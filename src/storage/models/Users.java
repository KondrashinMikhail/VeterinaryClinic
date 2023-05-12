package storage.models;

import lombok.*;

@Value
@Builder(toBuilder = true)
public class Users implements Comparable<Users> {
    Integer id;
    String name;
    String login;
    String password;
    String mail;
    Integer role;
    String specialization;

    @Override
    public int compareTo(@NonNull Users users) {
        return id.compareTo(users.id);
    }

    @Override
    public String toString() {
        return name + ", " + specialization;
    }
}
