package storage.DTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import storage.models.Users;

@Getter
@NoArgsConstructor
public class UsersDTO {
    Integer id;
    String name;
    String login;
    String password;
    String mail;
    Integer role;
    String specialization;

    public UsersDTO(Users u) {
        this.id = u.getId();
        this.name = u.getName();
        this.login = u.getLogin();
        this.password = u.getPassword();
        this.mail = u.getMail();
        this.role = u.getRole();
        this.specialization = u.getSpecialization();
    }

    public UsersDTO map(Users entity) {
        UsersDTO dto = new UsersDTO();
        dto.id = entity.getId();
        dto.name = entity.getName();
        dto.login = entity.getLogin();
        dto.password = entity.getPassword();
        dto.mail = entity.getMail();
        dto.role = entity.getRole();
        dto.specialization = entity.getSpecialization();
        return dto;
    }
}
