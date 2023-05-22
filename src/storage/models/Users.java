package storage.models;

import lombok.*;
import storage.DTOs.UsersDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    public Users map(UsersDTO dto) {
        Users entity = new Users();
        entity.id = dto.getId();
        entity.name = dto.getName();
        entity.login = dto.getLogin();
        entity.password = dto.getPassword();
        entity.mail = dto.getMail();
        entity.role = dto.getRole();
        entity.specialization = dto.getSpecialization();
        return entity;
    }
}
