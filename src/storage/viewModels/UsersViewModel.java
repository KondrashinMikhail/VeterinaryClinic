package storage.viewModels;

import storage.models.Users;

public class UsersViewModel {
    Integer id;
    String name;
    String login;
    String password;
    String mail;
    Integer role;
    String specialization;

    public UsersViewModel(Users u) {
        this.id = u.getId();
        this.name = u.getName();
        this.login = u.getLogin();
        this.password = u.getPassword();
        this.mail = u.getMail();
        this.role = u.getRole();
        this.specialization = u.getSpecialization();
    }
}
