package form;

import enums.notificationEnums.NotificationLocation;
import enums.notificationEnums.NotificationType;
import storage.DTOs.UsersDTO;
import storage.Database;
import storage.Mapper;
import storage.PasswordCoder;
import storage.models.Users;
import utils.ConstantUtils;
import utils.DesignUtils;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.util.Objects;
import java.util.regex.Pattern;

public class AccountForm extends JFrame {
    private JPanel informationPanel;
    private JButton exitButton;
    private JTextField textFieldMail;
    private JTextField textFieldName;
    private JTextField textFieldLogin;
    private JButton updateButton;
    private JButton newPasswordButton;
    private JPanel adminTransitionPanel;
    private JButton clientsAButton;
    private JButton doctorsAButton;
    private JButton curesAButton;
    private JButton animalsAButton;
    private JButton freeVisitsAButton;
    private JButton occupiedVisitsAButton;
    private JButton timetableCreationAButton;
    private JPanel doctorTransitionPanel;
    private JButton curesDButton;
    private JButton animalsDButton;
    private JButton historyVisitsDButton;
    private JButton freeVisitsDButton;
    private JButton reportCreatingDButton;
    private JPanel clientTransitionPanel;
    private JButton doctorsCButton;
    private JButton curesCButton;
    private JButton animalsCButton;
    private JButton historyVisitsCButton;
    private JButton freeVisitsCButton;
    private JButton accountAButton;
    private JButton accountDButton;
    private JButton accountCButton;

    public AccountForm(JFrame parent) {
        setTitle("Аккаунт");
        setLocationRelativeTo(parent);
        setContentPane(informationPanel);
        setMinimumSize(DesignUtils.TABLES_FORM_MINIMUM_SIZE);
        if (parent != null) setSize(parent.getSize());
        setLocationRelativeTo(parent);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        accountAButton.setForeground(DesignUtils.SUB_MAIN_COLOR);
        accountDButton.setForeground(DesignUtils.SUB_MAIN_COLOR);
        accountCButton.setForeground(DesignUtils.SUB_MAIN_COLOR);
        transitionsLogic();
        defaultTextFields();

        updateButton.addActionListener(e -> {
            if (Objects.equals(ConstantUtils.authorizedUser.getName(), textFieldName.getText())
                    && Objects.equals(ConstantUtils.authorizedUser.getLogin(), textFieldLogin.getText())
                    && Objects.equals(ConstantUtils.authorizedUser.getMail(), textFieldMail.getText())) {
                new NotificationForm(this, NotificationType.INFO, NotificationLocation.TOP_CENTER, "Нужно поменять данные").showNotification();
                return;
            }

            if (textFieldName.getText().isEmpty() || textFieldLogin.getText().isEmpty() || textFieldMail.getText().isEmpty()) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Необходимо заполнить все поля!").showNotification();
                return;
            }

            if (!Mapper.mapToDTO((Database.getInstance().select(Users.builder().login(textFieldLogin.getText()).build())), UsersDTO.class, Users.class).isEmpty() && !Objects.equals(textFieldLogin.getText(), ConstantUtils.authorizedUser.getLogin())) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "В системе есть пользователь с таким логином").showNotification();
                return;
            }

            if (!Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").matcher(textFieldMail.getText()).matches()) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Почта введена некорректно!").showNotification();
                return;
            }

            Database.getInstance().insertOrUpdate(Users.builder()
                    .id(ConstantUtils.authorizedUser.getId())
                    .name(textFieldName.getText())
                    .login(textFieldLogin.getText())
                    .mail(textFieldMail.getText())
                    .build());

            ConstantUtils.authorizedUser = Database.getInstance().select(Users.builder().id(ConstantUtils.authorizedUser.getId()).build()).first();

            textFieldName.setText(ConstantUtils.authorizedUser.getName());
            textFieldLogin.setText(ConstantUtils.authorizedUser.getLogin());
            textFieldMail.setText(ConstantUtils.authorizedUser.getMail());

            new NotificationForm(this, NotificationType.SUCCESS, NotificationLocation.TOP_CENTER, "Данные изменены!").showNotification();
        });

        newPasswordButton.addActionListener(e -> {
            JPasswordField pf = new JPasswordField();
            int okCxl = JOptionPane.showConfirmDialog(null, pf, "Введите новый пароль", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (okCxl == JOptionPane.OK_OPTION) {
                String password = new String(pf.getPassword());

                JPasswordField pf1 = new JPasswordField();
                int okCxl1 = JOptionPane.showConfirmDialog(null, pf1, "Повторие пароль", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (okCxl1 == JOptionPane.OK_OPTION) {
                    String password1 = new String(pf1.getPassword());

                    if (password.equals(password1)) {
                        Database.getInstance().insertOrUpdate(Users.builder()
                                .id(ConstantUtils.authorizedUser.getId())
                                .password(PasswordCoder.encrypt(password))
                                .build());
                        new NotificationForm(this, NotificationType.SUCCESS, NotificationLocation.TOP_CENTER, "Пароль изменен!").showNotification();
                    }

                    else
                        new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Пароли не совпадают!").showNotification();

                }
            }
        });

        exitButton.addActionListener(e -> exitFromAccount());
    }

    public void defaultTextFields() {
        textFieldName.setText(ConstantUtils.authorizedUser.getName());
        textFieldLogin.setText(ConstantUtils.authorizedUser.getLogin());
        textFieldMail.setText(ConstantUtils.authorizedUser.getMail());

        textFieldName.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldLogin.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldMail.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
    }

    private void exitFromAccount() {
        ConstantUtils.authorizedUser = null;
        new LoginForm(null).showNotification(NotificationType.SUCCESS, "Выход произведен успешно!");
        dispose();
    }

    private void transitionsLogic() {
        switch (ConstantUtils.authorizedUser.getRole()) {
            case 0 -> {
                clientTransitionPanel.setVisible(true);
                doctorTransitionPanel.setVisible(false);
                adminTransitionPanel.setVisible(false);
            }
            case 1 -> {
                clientTransitionPanel.setVisible(false);
                doctorTransitionPanel.setVisible(true);
                adminTransitionPanel.setVisible(false);
            }
            case 2 -> {
                clientTransitionPanel.setVisible(false);
                doctorTransitionPanel.setVisible(false);
                adminTransitionPanel.setVisible(true);
            }
        }

        //Buttons for admin
        clientsAButton.addActionListener(e -> {
            new ClientsForm(this);
            dispose();
        });
        doctorsAButton.addActionListener(e -> {
            new DoctorsForAdminForm(this);
            dispose();
        });
        curesAButton.addActionListener(e -> {
            new CuresForAdminForm(this);
            dispose();
        });
        animalsAButton.addActionListener(e -> {
            new AnimalsForUsers(this);
            dispose();
        });
        freeVisitsAButton.addActionListener(e -> {
            new FreeVisitsForm(this);
            dispose();
        });
        occupiedVisitsAButton.addActionListener(e -> {
            new OccupiedVisitsForm(this);
            dispose();
        });
        timetableCreationAButton.addActionListener(e -> {
            new TimetableCreatingForm(this);
            dispose();
        });
        accountAButton.addActionListener(e -> {
            new AccountForm(this);
            dispose();
        });

        //Buttons for doctor
        curesDButton.addActionListener(e -> {
            new CuresForUsersForm(this);
            dispose();
        });
        animalsDButton.addActionListener(e -> {
            new AnimalsForUsers(this);
            dispose();
        });
        historyVisitsDButton.addActionListener(e -> {
            new HistoryVisitsForm(this);
            dispose();
        });
        freeVisitsDButton.addActionListener(e -> {
            new FreeVisitsForm(this);
            dispose();
        });
        reportCreatingDButton.addActionListener(e -> {
            new ReportVisitsForm(this);
            dispose();
        });
        accountDButton.addActionListener(e -> {
            new AccountForm(this);
            dispose();
        });

        //Buttons for client
        doctorsCButton.addActionListener(e -> {
            new DoctorsForClientsForm(this);
            dispose();
        });
        curesCButton.addActionListener(e -> {
            new CuresForUsersForm(this);
            dispose();
        });
        animalsCButton.addActionListener(e -> {
            new AnimalsForClient(this);
            dispose();
        });
        historyVisitsCButton.addActionListener(e -> {
            new HistoryVisitsForm(this);
            dispose();
        });
        freeVisitsCButton.addActionListener(e -> {
            new FreeVisitsForm(this);
            dispose();
        });
        accountCButton.addActionListener(e -> {
            new AccountForm(this);
            dispose();
        });
    }
}
