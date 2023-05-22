package form;

import enums.notificationEnums.NotificationLocation;
import enums.notificationEnums.NotificationType;
import enums.modelsEnums.UserRole;
import storage.Database;
import storage.models.Users;
import storage.DTOs.UsersDTO;
import utils.DesignUtils;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.util.regex.Pattern;

public class RegistrationForm extends JFrame{
    private JTextField textFieldLogin;
    private JPasswordField passwordFieldPassword;
    private JButton registerButton;
    private JButton cancelButton;
    private JPanel registrationPanel;
    private JTextField textFieldMail;
    private JTextField textFieldName;
    private JPasswordField passwordFieldPasswordConfirm;
    private JLabel loginLabel;
    private JLabel nameLabel;
    private JLabel mailLabel;
    private JLabel passwordLabel;
    private JLabel passwordConfirmLabel;

    public RegistrationForm(JFrame parent) {
        setTitle("Авторизация");
        setLocationRelativeTo(parent);
        setContentPane(registrationPanel);
        setMinimumSize(DesignUtils.AUTHORIZATION_FORM_MINIMUM_SIZE);
        if (parent != null) setSize(parent.getSize());
        setLocationRelativeTo(parent);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        defaultFieldsPaint();

        registerButton.addActionListener(e -> {
            defaultFieldsPaint();
            if (textFieldLogin.getText().isEmpty() || textFieldName.getText().isEmpty() || textFieldMail.getText().isEmpty()
                    || String.valueOf(passwordFieldPassword.getPassword()).isEmpty() || String.valueOf(passwordFieldPasswordConfirm.getPassword()).isEmpty()) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Необходимо заполнить все поля").showNotification();
                if (textFieldLogin.getText().isEmpty()) wrongLoginPaint();
                if (textFieldName.getText().isEmpty()) wrongNamePaint();
                if (textFieldMail.getText().isEmpty()) wrongMailPaint();
                if (String.valueOf(passwordFieldPassword.getPassword()).isEmpty()) wrongPasswordPaint();
                if (String.valueOf(passwordFieldPasswordConfirm.getPassword()).isEmpty()) wrongPasswordConfirmPaint();
                return;
            }

            if (!String.valueOf(passwordFieldPasswordConfirm.getPassword()).equals(String.valueOf(passwordFieldPassword.getPassword()))) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Пароли не совпадают").showNotification();
                wrongPasswordPaint();
                wrongPasswordConfirmPaint();
                return;
            }

            if (!Database.getInstance().select(Users.builder().login(textFieldLogin.getText()).build()).stream().map(UsersDTO::new).toList().isEmpty()) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "В системе есть пользователь с таким логином").showNotification();
                wrongLoginPaint();
                return;
            }


            if (!Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").matcher(textFieldMail.getText()).matches()) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Почта введена некорректно").showNotification();
                wrongMailPaint();
                return;
            }

            Database.getInstance().insertOrUpdate(Users.builder()
                    .name(textFieldName.getText())
                    .login(textFieldLogin.getText())
                    .mail(textFieldMail.getText())
                    .role(UserRole.Client.ordinal())
                    .password(String.valueOf(passwordFieldPassword.getPassword()))
                    .build());

            new LoginForm(this).showNotification(NotificationType.SUCCESS, "Регистрация выполнена, необходимо войти");
            dispose();
        });
        cancelButton.addActionListener(e -> {
            new LoginForm(this);
            dispose();
        });
    }

    private void defaultFieldsPaint() {
        loginLabel.setForeground(DesignUtils.SUB_MAIN_COLOR);
        nameLabel.setForeground(DesignUtils.SUB_MAIN_COLOR);
        mailLabel.setForeground(DesignUtils.SUB_MAIN_COLOR);
        passwordLabel.setForeground(DesignUtils.SUB_MAIN_COLOR);
        passwordConfirmLabel.setForeground(DesignUtils.SUB_MAIN_COLOR);
        textFieldLogin.setBorder(new MatteBorder(0 ,0, 2, 0, DesignUtils.SUB_MAIN_COLOR));
        textFieldName.setBorder(new MatteBorder(0 ,0, 2, 0, DesignUtils.SUB_MAIN_COLOR));
        textFieldMail.setBorder(new MatteBorder(0 ,0, 2, 0, DesignUtils.SUB_MAIN_COLOR));
        passwordFieldPassword.setBorder(new MatteBorder(0 ,0, 2, 0, DesignUtils.SUB_MAIN_COLOR));
        passwordFieldPasswordConfirm.setBorder(new MatteBorder(0 ,0, 2, 0, DesignUtils.SUB_MAIN_COLOR));
    }

    private void wrongLoginPaint() {
        textFieldLogin.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.ERROR_COLOR));
        loginLabel.setForeground(DesignUtils.ERROR_COLOR);
    }

    private void wrongNamePaint() {
        textFieldName.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.ERROR_COLOR));
        nameLabel.setForeground(DesignUtils.ERROR_COLOR);
    }
    private void wrongPasswordPaint() {
        passwordFieldPassword.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.ERROR_COLOR));
        passwordLabel.setForeground(DesignUtils.ERROR_COLOR);
    }
    private void wrongPasswordConfirmPaint() {
        passwordFieldPasswordConfirm.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.ERROR_COLOR));
        passwordConfirmLabel.setForeground(DesignUtils.ERROR_COLOR);
    }

    private void wrongMailPaint() {
        textFieldMail.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.ERROR_COLOR));
        mailLabel.setForeground(DesignUtils.ERROR_COLOR);
    }
}
