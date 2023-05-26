package form;

import enums.modelsEnums.UserRole;
import enums.notificationEnums.NotificationLocation;
import enums.notificationEnums.NotificationType;
import form.customGraphics.comboBox.CustomComboBox;
import form.customGraphics.table.CustomTable;
import mailLogic.MailSender;
import storage.DTOs.*;
import storage.Database;
import storage.Mapper;
import storage.models.*;
import utils.ConstantUtils;
import utils.DesignUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class FreeVisitsForm extends JFrame {
    private List<AnimalDTO> animals;
    private VisitDTO selectedVisit;
    private JPanel informationPanel;
    private JScrollPane scrollPane;
    private JTable table;
    private JButton exitButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton addButton;
    private JComboBox<String> comboBoxAnimals;
    private JPanel clientPanel;
    private JPanel adminTransitionPanel;
    private JPanel doctorTransitionPanel;
    private JPanel clientTransitionPanel;
    private JButton clientsAButton;
    private JButton doctorsAButton;
    private JButton curesAButton;
    private JButton animalsAButton;
    private JButton freeVisitsAButton;
    private JButton occupiedVisitsAButton;
    private JButton timetableCreationAButton;
    private JButton curesDButton;
    private JButton animalsDButton;
    private JButton historyVisitsDButton;
    private JButton freeVisitsDButton;
    private JButton reportCreatingDButton;
    private JButton doctorsCButton;
    private JButton curesCButton;
    private JButton animalsCButton;
    private JButton historyVisitsCButton;
    private JButton freeVisitsCButton;

    public FreeVisitsForm(JFrame parent) {
        setTitle("Свободные приемы");
        setLocationRelativeTo(parent);
        setContentPane(informationPanel);
        setMinimumSize(DesignUtils.TABLES_FORM_MINIMUM_SIZE);
        if (parent != null) setSize(parent.getSize());
        setLocationRelativeTo(parent);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        transitionsLogic();
        freeVisitsAButton.setForeground(DesignUtils.SUB_MAIN_COLOR);
        freeVisitsDButton.setForeground(DesignUtils.SUB_MAIN_COLOR);
        freeVisitsCButton.setForeground(DesignUtils.SUB_MAIN_COLOR);

        if (ConstantUtils.authorizedUser.getRole() == 1) updateTableUI(fillInVisitsDoctor());
        else updateTableUI(fillInVisits());

        deleteButton.setVisible(ConstantUtils.authorizedUser.getRole() == 2);
        clientPanel.setVisible(false);

        Mapper.mapToDTO(Database.getInstance().select(Animal.builder().client_id(ConstantUtils.authorizedUser.getId()).build()), AnimalDTO.class, Animal.class);

        addButton.addActionListener(e -> {
            if (comboBoxAnimals.getSelectedItem() == null) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Необходимо заполнить все поля!").showNotification();
                return;
            }
            Database.getInstance().insertOrUpdate(Visit.builder()
                    .id(selectedVisit.getId())
                    .animal_id(animals.get(comboBoxAnimals.getSelectedIndex()).getId())
                    .client_id(ConstantUtils.authorizedUser.getId())
                    .build());

            new NotificationForm(this, NotificationType.SUCCESS, NotificationLocation.TOP_CENTER, "Вы записаны на визит!").showNotification();

            MailSender.sendMail(ConstantUtils.authorizedUser.getMail(), "Запись на прием", "Здравствуйте, " + ConstantUtils.authorizedUser.getName() + "!\n" +
                    "Вы успешно записались на прием в нашу ветеринарную клинику.\n" +
                    Mapper.mapToDTO(Database.getInstance().select(Mapper.mapFromDTO(UsersDTO.builder()
                            .id(selectedVisit.getDoctor_id())
                            .role(UserRole.Doctor.ordinal())
                            .build(), UsersDTO.class, Users.class)), UsersDTO.class, Users.class).get(0).getName() + " будет ждать Вас и " + animals.get(comboBoxAnimals.getSelectedIndex()).getName() + " на приеме "
                    + selectedVisit.getDate().toString().substring(0, selectedVisit.getDate().toString().length() - 5) + "!");

            updateTableUI(fillInVisits());
            clearButton.doClick();
        });

        deleteButton.addActionListener(e -> {
            Database.getInstance().delete(Visit.class, selectedVisit.getId());
            new NotificationForm(this, NotificationType.SUCCESS, NotificationLocation.TOP_CENTER, String.format("Прием %s удален", selectedVisit.getId())).showNotification();

            MailSender.sendMail(Mapper.mapToDTO(Database.getInstance().select(Mapper.mapFromDTO(UsersDTO.builder()
                            .id(selectedVisit.getDoctor_id())
                            .role(UserRole.Doctor.ordinal())
                            .build(), UsersDTO.class, Users.class)), UsersDTO.class, Users.class).get(0).getMail(),
                    "Удаление приема", "Здравствуйте, " +
                    Mapper.mapToDTO(Database.getInstance().select(Mapper.mapFromDTO(UsersDTO.builder()
                            .id(selectedVisit.getDoctor_id())
                            .role(UserRole.Doctor.ordinal())
                            .build(), UsersDTO.class, Users.class)), UsersDTO.class, Users.class).get(0).getName() + "!\n" +
                    "Прием от " + selectedVisit.getDate().toString().substring(0, selectedVisit.getDate().toString().length() - 5) + ", на который Вы были назначены, удален.");

            updateTableUI(fillInVisits());
        });

        clearButton.addActionListener(e -> {
            clientPanel.setVisible(false);
            comboBoxAnimals.setSelectedItem(null);
            selectedVisit = null;
            table.clearSelection();
        });

        exitButton.addActionListener(e -> exitFromAccount());
    }

    private void createUIComponents() {
        animals = Mapper.mapToDTO(Database.getInstance().select(Animal.builder().client_id(ConstantUtils.authorizedUser.getId()).build()), AnimalDTO.class, Animal.class);
        comboBoxAnimals = new CustomComboBox(animals.stream().map(AnimalDTO::getName).toArray(String[]::new));
        comboBoxAnimals.setSelectedItem(null);
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
                if (ConstantUtils.authorizedUser.getRole() == 0) clientPanel.setVisible(true);
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
        String[] header = new String[]{"Id", "Дата", "Врач"};
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, header) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        for (VisitDTO visitDTO : Mapper.mapToDTO(Database.getInstance().select(Visit.builder().build()), VisitDTO.class, Visit.class).stream()
                .filter(vdto -> vdto.getClient_id() == 0 && vdto.getAnimal_id() == 0).toList()) {
            model.addRow(new Object[] {
                    visitDTO.getId(),
                    visitDTO.getDate().toString().substring(0, visitDTO.getDate().toString().length() - 5),
                    Mapper.mapToDTO(Database.getInstance().select(Users.builder().id(visitDTO.getDoctor_id()).role(UserRole.Doctor.ordinal()).build()), UsersDTO.class, Users.class).get(0).getName(),
            });
        }
        return model;
    }

    private DefaultTableModel fillInVisitsDoctor() {
        String[] header = new String[]{"Id", "Дата"};
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, header) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        for (VisitDTO visitDTO : Mapper.mapToDTO(Database.getInstance().select(Visit.builder().build()), VisitDTO.class, Visit.class).stream()
                .filter(vdto -> vdto.getClient_id() == 0 && vdto.getAnimal_id() == 0).toList()) {
            model.addRow(new Object[] {
                    visitDTO.getId(),
                    visitDTO.getDate().toString().substring(0, visitDTO.getDate().toString().length() - 5)
            });
        }
        return model;
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
    }
}
