package form;

import enums.modelsEnums.UserRole;
import enums.notificationEnums.NotificationLocation;
import enums.notificationEnums.NotificationType;
import form.customGraphics.table.CustomTable;
import storage.DTOs.UsersDTO;
import storage.Database;
import storage.Mapper;
import storage.PasswordCoder;
import storage.models.Users;
import utils.ConstantUtils;
import utils.DesignUtils;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static utils.ConstantUtils.*;
import static utils.ConstantUtils.wrongAttemptsCounter;

public class DoctorsForAdminForm extends JFrame{
    private UsersDTO selectedDoctor;
    private JScrollPane scrollPane;
    private JTable table;
    private JTextField textFieldNameUser;
    private JTextField textFieldMailUser;
    private JTextField textFieldSpecializationUser;
    private JButton clearButton;
    private JButton exitButton;
    private JPanel informationPanel;
    private JTextField textFieldLoginUser;
    private JTextField textFieldPasswordUser;
    private JButton viewPasswordButton;
    private JButton updateButton;
    private JButton addButton;
    private JPasswordField passwordField;
    private JButton deleteButton;
    private JLabel changePasswordLabel;
    private JPanel adminTransitionPanel;
    private JButton clientsAButton;
    private JButton doctorsAButton;
    private JButton curesAButton;
    private JButton animalsAButton;
    private JButton freeVisitsAButton;
    private JButton occupiedVisitsAButton;
    private JButton timetableCreationAButton;

    public DoctorsForAdminForm(JFrame parent) {
        setTitle("Врачи");
        setLocationRelativeTo(parent);
        setContentPane(informationPanel);
        setMinimumSize(DesignUtils.TABLES_FORM_MINIMUM_SIZE);
        if (parent != null) setSize(parent.getSize());
        setLocationRelativeTo(parent);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        transitionsLogic();
        doctorsAButton.setForeground(DesignUtils.SUB_MAIN_COLOR);

        updateTableUI(fillInDoctors());
        defaultFieldsState();

        changePasswordLabel.setVisible(false);
        viewPasswordButton.setVisible(false);
        clearButton.setVisible(false);
        updateButton.setVisible(false);
        deleteButton.setVisible(false);
        selectedDoctor = null;

        viewPasswordButton.addActionListener(e -> {
            JPasswordField pf = new JPasswordField();
            int okCxl = JOptionPane.showConfirmDialog(null, pf, "Введите Ваш пароль", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (okCxl == JOptionPane.OK_OPTION) {
                String password = new String(pf.getPassword());
                if (password.equals(PasswordCoder.decrypt(ConstantUtils.authorizedUser.getPassword()))) {
                    Timer timer = new Timer(showPasswordTime, arg0 -> {
                        viewPasswordButton.setVisible(true);
                        textFieldPasswordUser.setText("");
                        textFieldPasswordUser.setVisible(false);
                    });
                    timer.setRepeats(false);
                    timer.start();

                    textFieldPasswordUser.setVisible(true);
                    textFieldPasswordUser.setText(PasswordCoder.decrypt(
                            Mapper.mapToDTO(Database.getInstance()
                                            .select(Users.builder().login(selectedDoctor.getLogin()).build()), UsersDTO.class, Users.class)
                                    .get(0).getPassword()
                    ));
                    viewPasswordButton.setVisible(false);
                } else {
                    wrongAttemptsCounter++;
                    if (possibleAttempts - wrongAttemptsCounter <= 0)
                        exitFromAccount(NotificationType.WARNING, "3 раза был введен неверный пароль");
                    else
                        new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Пароль неверен! Осталось попыток: " + (possibleAttempts - wrongAttemptsCounter)).showNotification();
                }
            }
        });

        clearButton.addActionListener(e -> {

            textFieldNameUser.setText("");
            textFieldSpecializationUser.setText("");
            textFieldMailUser.setText("");
            textFieldLoginUser.setText("");
            textFieldPasswordUser.setText("");

            textFieldPasswordUser.setVisible(false);
            viewPasswordButton.setVisible(false);

            table.clearSelection();

            changePasswordLabel.setVisible(false);
            passwordField.setVisible(true);

            clearButton.setVisible(false);
            deleteButton.setVisible(false);
            addButton.setVisible(true);
            updateButton.setVisible(false);

            selectedDoctor = null;
        });

        exitButton.addActionListener(e -> exitFromAccount(NotificationType.SUCCESS, "Выход произведен успешно!"));

        addButton.addActionListener(e -> {
            if (textFieldNameUser.getText().isEmpty()
                    || textFieldSpecializationUser.getText().isEmpty()
                    || textFieldMailUser.getText().isEmpty()
                    || textFieldLoginUser.getText().isEmpty()
                    || String.valueOf(passwordField.getPassword()).isEmpty()) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Необходимо заполнить все поля!").showNotification();
                return;
            }

            if (!Mapper.mapToDTO((Database.getInstance().select(Users.builder().login(textFieldLoginUser.getText()).build())), UsersDTO.class, Users.class).isEmpty()) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "В системе есть пользователь с таким логином").showNotification();
                return;
            }

            if (!Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").matcher(textFieldMailUser.getText()).matches()) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Почта введена некорректно").showNotification();
                return;
            }

            JPasswordField pf = new JPasswordField();
            int okCxl = JOptionPane.showConfirmDialog(null, pf, "Введите Ваш пароль", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (okCxl == JOptionPane.OK_OPTION) {
                String password = new String(pf.getPassword());
                if (password.equals(PasswordCoder.decrypt(ConstantUtils.authorizedUser.getPassword()))) {

                    Database.getInstance().insertOrUpdate(Mapper.mapFromDTO(UsersDTO.builder()
                                    .name(textFieldNameUser.getText())
                                    .specialization(textFieldSpecializationUser.getText())
                                    .login(textFieldLoginUser.getText())
                                    .mail(textFieldMailUser.getText())
                                    .role(UserRole.Doctor.ordinal())
                                    .password(PasswordCoder.encrypt(String.valueOf(passwordField.getPassword())))
                                    .build(),
                            UsersDTO.class, Users.class));

                    textFieldNameUser.setText("");
                    textFieldSpecializationUser.setText("");
                    textFieldMailUser.setText("");
                    textFieldLoginUser.setText("");
                    passwordField.setText("");

                    clearButton.doClick();

                    updateTableUI(fillInDoctors());

                    new NotificationForm(this, NotificationType.SUCCESS, NotificationLocation.TOP_CENTER, String.format("Врач %s создан", textFieldLoginUser.getText())).showNotification();
                } else {
                    wrongAttemptsCounter++;
                    if (possibleAttempts - wrongAttemptsCounter <= 0)
                        exitFromAccount(NotificationType.WARNING, "3 раза был введен неверный пароль");
                    else
                        new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Пароль неверен! Осталось попыток: " + (possibleAttempts - wrongAttemptsCounter)).showNotification();
                }
            }
        });

        updateButton.addActionListener(e -> {
            if (textFieldNameUser.getText().isEmpty()
                    || textFieldSpecializationUser.getText().isEmpty()
                    || textFieldMailUser.getText().isEmpty()
                    || textFieldLoginUser.getText().isEmpty()) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Необходимо заполнить все поля!").showNotification();
                return;
            }

            if (!Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").matcher(textFieldMailUser.getText()).matches()) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Почта введена некорректно").showNotification();
                return;
            }

            JPasswordField pf = new JPasswordField();
            int okCxl = JOptionPane.showConfirmDialog(null, pf, "Введите Ваш пароль", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (okCxl == JOptionPane.OK_OPTION) {
                String password = new String(pf.getPassword());
                if (password.equals(PasswordCoder.decrypt(ConstantUtils.authorizedUser.getPassword()))) {
                    if (String.valueOf(passwordField.getPassword()).isEmpty()) {
                        Database.getInstance().insertOrUpdate(Mapper.mapFromDTO(UsersDTO.builder()
                                        .id(selectedDoctor.getId())
                                        .name(textFieldNameUser.getText())
                                        .specialization(textFieldSpecializationUser.getText())
                                        .login(textFieldLoginUser.getText())
                                        .mail(textFieldMailUser.getText())
                                        .role(UserRole.Doctor.ordinal())
                                        .build(),
                                UsersDTO.class, Users.class));
                    } else {
                        Database.getInstance().insertOrUpdate(Mapper.mapFromDTO(UsersDTO.builder()
                                        .id(selectedDoctor.getId())
                                        .name(textFieldNameUser.getText())
                                        .specialization(textFieldSpecializationUser.getText())
                                        .login(textFieldLoginUser.getText())
                                        .mail(textFieldMailUser.getText())
                                        .role(UserRole.Doctor.ordinal())
                                        .password(PasswordCoder.encrypt(String.valueOf(passwordField.getPassword())))
                                        .build(),
                                UsersDTO.class, Users.class));
                    }

                    textFieldNameUser.setText("");
                    textFieldSpecializationUser.setText("");
                    textFieldMailUser.setText("");
                    textFieldLoginUser.setText("");
                    passwordField.setText("");

                    clearButton.doClick();

                    updateTableUI(fillInDoctors());

                    new NotificationForm(this, NotificationType.SUCCESS, NotificationLocation.TOP_CENTER, String.format("Врач %s изменен", textFieldLoginUser.getText())).showNotification();
                } else {
                    wrongAttemptsCounter++;
                    if (possibleAttempts - wrongAttemptsCounter <= 0)
                        exitFromAccount(NotificationType.WARNING, "3 раза был введен неверный пароль");
                    else
                        new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Пароль неверен! Осталось попыток: " + (possibleAttempts - wrongAttemptsCounter)).showNotification();
                }
            }
        });

        deleteButton.addActionListener(e -> {
            JPasswordField pf = new JPasswordField();
            int okCxl = JOptionPane.showConfirmDialog(null, pf, "Введите Ваш пароль", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (okCxl == JOptionPane.OK_OPTION) {
                String password = new String(pf.getPassword());
                if (password.equals(PasswordCoder.decrypt(ConstantUtils.authorizedUser.getPassword()))) {

                    try {
                        Database.getInstance().delete(Users.class, selectedDoctor.getId());
                    } catch (Exception ignored) {
                        new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, ("Нельзя удалять данного врача!")).showNotification();
                        return;
                    }

                    textFieldNameUser.setText("");
                    textFieldSpecializationUser.setText("");
                    textFieldMailUser.setText("");
                    textFieldLoginUser.setText("");
                    passwordField.setText("");

                    clearButton.doClick();

                    updateTableUI(fillInDoctors());

                    new NotificationForm(this, NotificationType.SUCCESS, NotificationLocation.TOP_CENTER, String.format("Врач %s удален", textFieldLoginUser.getText())).showNotification();
                } else {
                    wrongAttemptsCounter++;
                    if (possibleAttempts - wrongAttemptsCounter <= 0)
                        exitFromAccount(NotificationType.WARNING, "3 раза был введен неверный пароль");
                    else
                        new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Пароль неверен! Осталось попыток: " + (possibleAttempts - wrongAttemptsCounter)).showNotification();
                }
            }
        });
    }

    private void createUIComponents() {
        table = new CustomTable();
        scrollPane = new JScrollPane();
        ((CustomTable) table).fixTable(scrollPane);
        table.getSelectionModel().addListSelectionListener(event -> {
            int viewRow = table.getSelectedRow();
            if (!event.getValueIsAdjusting() && viewRow != -1) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                List<String> state = new ArrayList<>();
                for (int i = 0; i <= table.getColumnCount(); i++) {
                    Object modelValue = table.getModel().getValueAt(modelRow, i);
                    state.add(modelValue.toString());
                }

                textFieldNameUser.setText(state.get(1));
                textFieldSpecializationUser.setText(state.get(2));
                textFieldMailUser.setText(state.get(3));
                textFieldLoginUser.setText(state.get(4));

                textFieldPasswordUser.setVisible(false);



                viewPasswordButton.setVisible(true);

                //passwordField.setVisible(false);
                passwordField.setText("");

                clearButton.setVisible(true);
                deleteButton.setVisible(true);

                addButton.setVisible(false);
                updateButton.setVisible(true);

                changePasswordLabel.setVisible(true);
                passwordField.setText("");
                passwordField.setVisible(true);




                selectedDoctor = Mapper.mapToDTO(Database.getInstance().select(Mapper.mapFromDTO(UsersDTO.builder()
                                .login(state.get(4))
                                .role(UserRole.Doctor.ordinal())
                                .build(),
                        UsersDTO.class, Users.class)), UsersDTO.class, Users.class).get(0);
            }
        });
    }

    private DefaultTableModel fillInDoctors() {
        String[] header = new String[]{"Id", "Имя", "Специализация", "Почта", "Логин"};
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, header) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        for (UsersDTO user : Mapper.mapToDTO(Database.getInstance().select(Users.builder().role(UserRole.Doctor.ordinal()).build()), UsersDTO.class, Users.class))
            model.addRow(new Object[]{ user.getId(), user.getName(), user.getSpecialization(), user.getMail(), user.getLogin() });
        return model;
    }

    private void updateTableUI(DefaultTableModel model) {
        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.removeColumn(table.getColumnModel().getColumn(0));
        table.setFont(DesignUtils.REGULAR_FONT);
    }

    private void defaultFieldsState() {
        textFieldNameUser.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldMailUser.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldSpecializationUser.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldLoginUser.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldPasswordUser.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldPasswordUser.setVisible(false);
        textFieldPasswordUser.setEditable(false);
        passwordField.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
    }

    private void exitFromAccount(NotificationType type, String message) {
        ConstantUtils.authorizedUser = null;
        new LoginForm(null).showNotification(type, message);
        dispose();
    }

    private void transitionsLogic() {
        switch (ConstantUtils.authorizedUser.getRole()) {
            case 0, 1 -> adminTransitionPanel.setVisible(false);
            case 2 -> adminTransitionPanel.setVisible(true);
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
    }
}
