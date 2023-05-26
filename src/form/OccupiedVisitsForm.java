package form;

import enums.modelsEnums.UserRole;
import enums.notificationEnums.NotificationLocation;
import enums.notificationEnums.NotificationType;
import form.customGraphics.table.CustomTable;
import mailLogic.MailSender;
import storage.DTOs.AnimalDTO;
import storage.DTOs.UsersDTO;
import storage.DTOs.VisitDTO;
import storage.Database;
import storage.Mapper;
import storage.PasswordCoder;
import storage.models.Animal;
import storage.models.Users;
import storage.models.Visit;
import utils.ConstantUtils;
import utils.DesignUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

import static utils.ConstantUtils.*;
import static utils.ConstantUtils.wrongAttemptsCounter;

public class OccupiedVisitsForm extends JFrame {
    private VisitDTO selectedVisit;
    private JPanel informationPanel;
    private JButton exitButton;
    private JScrollPane scrollPane;
    private JTable table;
    private JButton deleteButton;
    private JPanel adminTransitionPanel;
    private JButton clientsAButton;
    private JButton doctorsAButton;
    private JButton curesAButton;
    private JButton animalsAButton;
    private JButton freeVisitsAButton;
    private JButton occupiedVisitsAButton;
    private JButton timetableCreationAButton;
    private JButton accountAButton;

    public OccupiedVisitsForm(JFrame parent) {
        setTitle("Непосещенные приемы");
        setLocationRelativeTo(parent);
        setContentPane(informationPanel);
        setMinimumSize(DesignUtils.TABLES_FORM_MINIMUM_SIZE);
        if (parent != null) setSize(parent.getSize());
        setLocationRelativeTo(parent);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        transitionsLogic();
        occupiedVisitsAButton.setForeground(DesignUtils.SUB_MAIN_COLOR);

        updateTableUI(fillInVisits());

        deleteButton.addActionListener(e -> {
            if (selectedVisit == null) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, ("Необходимо выбрать прием!")).showNotification();
                return;
            }
            JPasswordField pf = new JPasswordField();
            int okCxl = JOptionPane.showConfirmDialog(null, pf, "Введите Ваш пароль", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (okCxl == JOptionPane.OK_OPTION) {
                String password = new String(pf.getPassword());
                if (password.equals(PasswordCoder.decrypt(ConstantUtils.authorizedUser.getPassword()))) {

                    UsersDTO user = Mapper.mapToDTO(Database.getInstance().select(Mapper.mapFromDTO(UsersDTO.builder()
                            .id(selectedVisit.getClient_id())
                            .role(UserRole.Client.ordinal())
                            .build(), UsersDTO.class, Users.class)), UsersDTO.class, Users.class).get(0);

                    UsersDTO doctor = Mapper.mapToDTO(Database.getInstance().select(Mapper.mapFromDTO(UsersDTO.builder()
                            .id(selectedVisit.getDoctor_id())
                            .role(UserRole.Doctor.ordinal())
                            .build(), UsersDTO.class, Users.class)), UsersDTO.class, Users.class).get(0);

                    MailSender.sendMail(doctor.getMail(),
                            "Открпление от приема", "Здравствуйте, " + doctor.getName() + "!\n" +
                            "От записи на Ваш прием от " + selectedVisit.getDate().toString().substring(0, selectedVisit.getDate().toString().length() - 5) + " был откреплен пользователь " + user.getName() + ".");

                    MailSender.sendMail(user.getMail(),
                            "Открпление от приема", "Здравствуйте, " + user.getName() + "!\n" +
                                    "Ваша запись на прием от " + selectedVisit.getDate().toString().substring(0, selectedVisit.getDate().toString().length() - 5) + " была отменена.");


                    Database.getInstance().insertOrUpdate(Visit.builder()
                            .id(selectedVisit.getId())
                            .client_id(0)
                            .animal_id(0)
                            .build());

                    updateTableUI(fillInVisits());

                    new NotificationForm(this, NotificationType.SUCCESS, NotificationLocation.TOP_CENTER, user.getName() + " успешно откреплен").showNotification();

                } else {
                    wrongAttemptsCounter++;
                    if (possibleAttempts - wrongAttemptsCounter <= 0)
                        exitFromAccount(NotificationType.WARNING, "3 раза был введен неверный пароль");
                    else
                        new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Пароль неверен! Осталось попыток: " + (possibleAttempts - wrongAttemptsCounter)).showNotification();
                }
            }
        });

        exitButton.addActionListener(e -> exitFromAccount(NotificationType.SUCCESS, "Выход произведен успешно!"));
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

                selectedVisit = Mapper.mapToDTO(Database.getInstance().select(Mapper.mapFromDTO(VisitDTO.builder()
                                .id(Integer.valueOf(state.get(0)))
                                .build(),
                        VisitDTO.class, Visit.class)), VisitDTO.class, Visit.class).get(0);
            }
        });
    }

    private void updateTableUI(DefaultTableModel model) {
        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.removeColumn(table.getColumnModel().getColumn(0));
        table.setFont(DesignUtils.REGULAR_FONT);
    }

    private DefaultTableModel fillInVisits() {
        String[] header = new String[]{"Id", "Дата", "Врач", "Клиент", "Животное"};
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, header) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        for (VisitDTO visitDTO : Mapper.mapToDTO(Database.getInstance().select(Visit.builder().build()), VisitDTO.class, Visit.class).stream()
                .filter(vdto -> vdto.getClient_id() != 0 && vdto.getAnimal_id() != 0 && (vdto.getRecommendation() == null || vdto.getRecommendation().isEmpty())).toList()) {
            model.addRow(new Object[] {
                    visitDTO.getId(),
                    visitDTO.getDate().toString().substring(0, visitDTO.getDate().toString().length() - 5),
                    Mapper.mapToDTO(Database.getInstance().select(Users.builder().id(visitDTO.getDoctor_id()).role(UserRole.Doctor.ordinal()).build()), UsersDTO.class, Users.class).get(0).getName(),
                    Mapper.mapToDTO(Database.getInstance().select(Users.builder().id(visitDTO.getClient_id()).role(UserRole.Client.ordinal()).build()), UsersDTO.class, Users.class).get(0).getName(),
                    Mapper.mapToDTO(Database.getInstance().select(Animal.builder().id(visitDTO.getAnimal_id()).build()), AnimalDTO.class, Animal.class).get(0).getName()
            });
        }
        return model;
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
        accountAButton.addActionListener(e -> {
            new AccountForm(this);
            dispose();
        });
    }
}
