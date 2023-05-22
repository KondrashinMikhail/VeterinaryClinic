package form;

import enums.notificationEnums.NotificationLocation;
import enums.notificationEnums.NotificationType;
import storage.Database;
import storage.Mapper;
import storage.PasswordCoder;
import storage.models.Users;
import storage.DTOs.UsersDTO;
import utils.ConstantUtils;
import utils.DesignUtils;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.util.List;
import java.util.Objects;

public class LoginForm extends JFrame {
    private JTextField textFieldLogin;
    private JPasswordField passwordFieldPassword;
    private JButton loginButton;
    private JButton registerButton;
    private JPanel loginPanel;
    private JLabel loginLabel;
    private JLabel passwordLabel;

    public LoginForm(JFrame parent) {

        setTitle("Авторизация");
        setLocationRelativeTo(parent);
        setContentPane(loginPanel);
        setMinimumSize(DesignUtils.AUTHORIZATION_FORM_MINIMUM_SIZE);
        if (parent != null) setSize(parent.getSize());
        setLocationRelativeTo(parent);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        defaultFieldsPaint();

        loginButton.addActionListener(e -> {
            defaultFieldsPaint();
            if (textFieldLogin.getText().isEmpty() || String.valueOf(passwordFieldPassword.getPassword()).isEmpty()) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Необходимо заполнить поля 'Логин' и 'Пароль'").showNotification();
                return;
            }

            boolean isUserExist = Mapper.mapToDTO(Database.getInstance().select(Users.builder().build()), UsersDTO.class, Users.class).stream().anyMatch(user ->
                            Objects.equals(user.getLogin(), textFieldLogin.getText())
                                    && PasswordCoder.confirmPassword(String.valueOf(passwordFieldPassword.getPassword()), user.getPassword()));
            if (!isUserExist) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Такого пользователя нет в системе").showNotification();
            }
            else {
                //TODO: Сделать вызов формы в зависимости от пользователя
                new InformationForm(this);
                dispose();
            }
        });

        registerButton.addActionListener(e -> {
            new RegistrationForm(this);
            dispose();
        });
    }

    private void defaultFieldsPaint() {
        loginLabel.setForeground(DesignUtils.MAIN_COLOR);
        passwordLabel.setForeground(DesignUtils.MAIN_COLOR);
        textFieldLogin.setBorder(new MatteBorder(0 ,0, 2, 0, DesignUtils.MAIN_COLOR));
        passwordFieldPassword.setBorder(new MatteBorder(0 ,0, 2, 0, DesignUtils.MAIN_COLOR));
    }

    public void showNotification(NotificationType type, String message) {
        new NotificationForm(this, type, NotificationLocation.TOP_CENTER, message).showNotification();
    }
}
