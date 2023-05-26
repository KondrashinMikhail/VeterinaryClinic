package form;

import enums.modelsEnums.PossibleAnimalSpecies;
import enums.notificationEnums.NotificationType;
import form.customGraphics.table.CustomTable;
import storage.DTOs.AnimalDTO;
import storage.DTOs.UsersDTO;
import storage.Database;
import storage.Mapper;
import storage.models.Animal;
import storage.models.Users;
import utils.ConstantUtils;
import utils.DesignUtils;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class AnimalsForUsers extends JFrame {
    private AnimalDTO selectedAnimal;
    private JPanel informationPanel;
    private JScrollPane scrollPane;
    private JTable table;
    private JPanel cardPanel;
    private JTextField textFieldName;
    private JTextField textFieldSpecies;
    private JTextField textFieldBreed;
    private JButton clearButton;
    private JTextField textFieldAge;
    private JButton exitButton;
    private JTextField textFieldOwner;
    private JButton vaccinationsButton;
    private JPanel doctorTransitionPanel;
    private JButton curesDButton;
    private JButton animalsDButton;
    private JButton historyVisitsDButton;
    private JButton freeVisitsDButton;
    private JButton reportCreatingDButton;
    private JPanel adminTransitionPanel;
    private JButton clientsAButton;
    private JButton doctorsAButton;
    private JButton curesAButton;
    private JButton animalsAButton;
    private JButton freeVisitsAButton;
    private JButton occupiedVisitsAButton;
    private JButton timetableCreationAButton;

    public AnimalsForUsers(JFrame parent) {
        setTitle("Животные");
        setLocationRelativeTo(parent);
        setContentPane(informationPanel);
        setMinimumSize(DesignUtils.TABLES_FORM_MINIMUM_SIZE);
        if (parent != null) setSize(parent.getSize());
        setLocationRelativeTo(parent);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        transitionsLogic();
        animalsAButton.setForeground(DesignUtils.SUB_MAIN_COLOR);
        animalsDButton.setForeground(DesignUtils.SUB_MAIN_COLOR);

        cardPanel.setVisible(false);
        updateTableUI(fillInAnimals());
        defaultFieldsState();

        vaccinationsButton.addActionListener(e -> {
            new VaccinationForm(this, selectedAnimal, false);
            dispose();
        });

        clearButton.addActionListener(e -> {
            table.clearSelection();
            cardPanel.setVisible(false);
        });

        exitButton.addActionListener(e -> exitFromAccount());
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

                textFieldName.setText(state.get(1));
                textFieldSpecies.setText(state.get(2));
                textFieldBreed.setText(state.get(3));
                textFieldAge.setText(state.get(4));
                textFieldOwner.setText(state.get(5));

                selectedAnimal = Mapper.mapToDTO(Database.getInstance().select(Mapper.mapFromDTO(AnimalDTO.builder()
                                .id(Integer.valueOf(state.get(0)))
                                .build(),
                        AnimalDTO.class, Animal.class)), AnimalDTO.class, Animal.class).get(0);

                cardPanel.setVisible(true);
            }
        });
    }


    private DefaultTableModel fillInAnimals() {
        String[] header = new String[]{"Id", "Кличка", "Вид", "Порода", "Возраст", "Владелец"};
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, header) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        for (AnimalDTO animalDTO : Mapper.mapToDTO(Database.getInstance().select(Animal.builder().build()), AnimalDTO.class, Animal.class))
            model.addRow(new Object[]{
                    animalDTO.getId(),
                    animalDTO.getName(),
                    PossibleAnimalSpecies.values()[animalDTO.getSpecies()].localizeString,
                    animalDTO.getBreed(),
                    animalDTO.getAge().toString(),
                    Mapper.mapToDTO(Database.getInstance().select(Users.builder().id(animalDTO.getClient_id()).build()), UsersDTO.class, Users.class).get(0).getName()
            });
        return model;
    }

    private void updateTableUI(DefaultTableModel model) {
        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.removeColumn(table.getColumnModel().getColumn(0));
        table.setFont(DesignUtils.REGULAR_FONT);
    }

    private void defaultFieldsState() {
        textFieldName.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldName.setEditable(false);

        textFieldSpecies.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldSpecies.setEditable(false);

        textFieldBreed.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldBreed.setEditable(false);

        textFieldAge.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldAge.setEditable(false);

        textFieldOwner.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldOwner.setEditable(false);
    }

    private void exitFromAccount() {
        ConstantUtils.authorizedUser = null;
        new LoginForm(null).showNotification(NotificationType.SUCCESS, "Выход произведен успешно!");
        dispose();
    }
    private void transitionsLogic() {
        switch (ConstantUtils.authorizedUser.getRole()) {
            case 0 -> {
                doctorTransitionPanel.setVisible(false);
                adminTransitionPanel.setVisible(false);
            }
            case 1 -> {
                doctorTransitionPanel.setVisible(true);
                adminTransitionPanel.setVisible(false);
            }
            case 2 -> {
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
    }
}
