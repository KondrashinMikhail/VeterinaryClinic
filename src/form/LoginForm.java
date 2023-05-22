package form;

import enums.notificationEnums.NotificationLocation;
import enums.notificationEnums.NotificationType;
import storage.Database;
import storage.models.Users;
import storage.DTOs.UsersDTO;
import utils.ConstantUtils;
import utils.DesignUtils;

import javax.swing.*;
import javax.swing.border.MatteBorder;

public class LoginForm extends JFrame {
    private JTextField textFieldLogin;
    private JPasswordField passwordFieldPassword;
    private JButton loginButton;
    private JButton registerButton;
    private JPanel loginPanel;
    private JLabel loginLabel;
    private JLabel passwordLabel;

    public LoginForm(JFrame parent) {
//        new NotificationForm(this, NotificationType.SUCCESS, NotificationLocation.CENTER, "Добро пожаловать, ").showNotification();

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

                if (textFieldLogin.getText().isEmpty() && String.valueOf(passwordFieldPassword.getPassword()).isEmpty()) {
                    wrongLoginPaint();
                    wrongPasswordPaint();
                }
                else if (!textFieldLogin.getText().isEmpty()) wrongPasswordPaint();
                else wrongLoginPaint();
                return;
            }
            if (Database.getInstance().select(Users.builder().login(textFieldLogin.getText()).build()).stream().map(UsersDTO::new).findAny().isEmpty()) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "В системе нет пользователя с таким логином").showNotification();
                wrongLoginPaint();
                return;
            }
            if (Database.getInstance().select(Users.builder().login(textFieldLogin.getText()).password(String.valueOf(passwordFieldPassword.getPassword())).build()).stream().map(UsersDTO::new).findAny().isEmpty()) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Неправильный пароль").showNotification();
                wrongPasswordPaint();
                return;
            }
            ConstantUtils.authorizedUser = Database.getInstance().select(Users.builder()
                    .login(textFieldLogin.getText())
                    .password(String.valueOf(passwordFieldPassword.getPassword())).build()).first();

            new InformationForm(this);
            dispose();

            //new NotificationForm(this, NotificationType.SUCCESS, NotificationLocation.TOP_CENTER, "Добро пожаловать, " + ConstantUtils.authorizedUser.getName()).showNotification();
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

    private void wrongLoginPaint() {
        textFieldLogin.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.ERROR_COLOR));
        loginLabel.setForeground(DesignUtils.ERROR_COLOR);
    }
    private void wrongPasswordPaint() {
        passwordFieldPassword.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.ERROR_COLOR));
        passwordLabel.setForeground(DesignUtils.ERROR_COLOR);
    }

    public void showNotification(NotificationType type, String message) {
        new NotificationForm(this, type, NotificationLocation.TOP_CENTER, message).showNotification();
    }
}
