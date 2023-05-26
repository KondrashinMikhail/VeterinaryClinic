package form;

import enums.modelsEnums.UserRole;
import enums.notificationEnums.NotificationLocation;
import enums.notificationEnums.NotificationType;
import form.customGraphics.comboBox.CustomComboBox;
import mailLogic.MailSender;
import storage.DTOs.UsersDTO;
import storage.Database;
import storage.Mapper;
import storage.models.Users;
import storage.models.Visit;
import utils.ConstantUtils;
import utils.DesignUtils;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.sql.Timestamp;
import java.util.List;

public class TimetableCreatingForm extends JFrame {
    private List<UsersDTO> doctors;
    private JPanel informationPanel;
    private JTextField textFieldTime;
    private JComboBox<String> comboBoxDoctors;
    private JButton addButton;
    private JButton exitButton;
    private JPanel adminTransitionPanel;
    private JButton clientsAButton;
    private JButton doctorsAButton;
    private JButton curesAButton;
    private JButton animalsAButton;
    private JButton freeVisitsAButton;
    private JButton occupiedVisitsAButton;
    private JButton timetableCreationAButton;

    public TimetableCreatingForm(JFrame parent) {
        setTitle("Создание расписания");
        setLocationRelativeTo(parent);
        setContentPane(informationPanel);
        setMinimumSize(DesignUtils.TABLES_FORM_MINIMUM_SIZE);
        if (parent != null) setSize(parent.getSize());
        setLocationRelativeTo(parent);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        transitionsLogic();
        timetableCreationAButton.setForeground(DesignUtils.SUB_MAIN_COLOR);

        comboBoxDoctors.setSelectedItem(null);
        textFieldTime.setText(new Timestamp(System.currentTimeMillis()).toString().substring(0, new Timestamp(System.currentTimeMillis()).toString().length() - 12));
        textFieldTime.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));

        addButton.addActionListener(e -> {

            if (comboBoxDoctors.getSelectedItem() == null || textFieldTime.getText().isEmpty()) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Необходимо заполнить все поля!").showNotification();
                return;
            }

            if (textFieldTime.getText().length() != 16) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Неполная дата!").showNotification();
                return;
            }

            try {
                String[] dateFull = textFieldTime.getText().split(" ");
                String[] date = dateFull[0].split("-");
                String[] time = dateFull[1].split(":");
                Timestamp timestamp = new Timestamp(0);
                timestamp.setYear(Integer.parseInt(date[0]) - 1900);
                timestamp.setMonth(Integer.parseInt(date[1]) - 1);
                timestamp.setDate(Integer.parseInt(date[2]));
                timestamp.setHours(Integer.parseInt(time[0]));
                timestamp.setMinutes(Integer.parseInt(time[1]));
            } catch (Exception ignored) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Некорректная дата!").showNotification();
                return;
            }

            UsersDTO doctor = doctors.get(comboBoxDoctors.getSelectedIndex());
            String[] dateFull = textFieldTime.getText().split(" ");
            String[] date = dateFull[0].split("-");
            String[] time = dateFull[1].split(":");
            Timestamp timestamp = new Timestamp(0);
            timestamp.setYear(Integer.parseInt(date[0]) - 1900);
            timestamp.setMonth(Integer.parseInt(date[1]) - 1);
            timestamp.setDate(Integer.parseInt(date[2]));
            timestamp.setHours(Integer.parseInt(time[0]));
            timestamp.setMinutes(Integer.parseInt(time[1]));

            Database.getInstance().insertOrUpdate(Visit.builder()
                    .doctor_id(doctor.getId())
                    .date(timestamp)
                    .build());

            new NotificationForm(this, NotificationType.SUCCESS, NotificationLocation.TOP_CENTER, "Новый прием создан!").showNotification();

            MailSender.sendMail(doctor.getMail(), "Создание приема", "Здравствуйте, " + doctor.getName() + "!\n" +
                    "Вы назначены на прием на " + textFieldTime.getText());

            comboBoxDoctors.setSelectedItem(null);
            textFieldTime.setText(new Timestamp(System.currentTimeMillis()).toString().substring(0, new Timestamp(System.currentTimeMillis()).toString().length() - 12));
        });

        exitButton.addActionListener(e -> exitFromAccount());
    }

    private void createUIComponents() {
        doctors = Mapper.mapToDTO(Database.getInstance().select(Users.builder().role(UserRole.Doctor.ordinal()).build()), UsersDTO.class, Users.class);
        comboBoxDoctors = new CustomComboBox(doctors.stream().map(UsersDTO::getName).toArray(String[]::new));
    }

    private void exitFromAccount() {
        ConstantUtils.authorizedUser = null;
        new LoginForm(null).showNotification(NotificationType.SUCCESS, "Выход произведен успешно!");
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
