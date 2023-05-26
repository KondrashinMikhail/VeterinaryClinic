package form;

import enums.modelsEnums.PossibleAnimalSpecies;
import enums.notificationEnums.NotificationLocation;
import enums.notificationEnums.NotificationType;
import form.customGraphics.comboBox.CustomComboBox;
import form.customGraphics.table.CustomTable;
import storage.DTOs.AnimalDTO;
import storage.Database;
import storage.Mapper;
import storage.models.Animal;
import utils.ConstantUtils;
import utils.DesignUtils;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class AnimalsForClient extends JFrame {
    private AnimalDTO selectedAnimal;
    private JPanel informationPanel;
    private JScrollPane scrollPane;
    private JTable table;
    private JTextField textFieldName;
    private JTextField textFieldBreed;
    private JButton clearButton;
    private JTextField textFieldAge;
    private JButton exitButton;
    private JButton updateButton;
    private JButton addButton;
    private JComboBox<String> comboBoxSpeciesAnimal;
    private JButton deleteButton;
    private JButton vaccinationsButton;
    private JPanel clientTransitionPanel;
    private JButton doctorsCButton;
    private JButton curesCButton;
    private JButton animalsCButton;
    private JButton historyVisitsCButton;
    private JButton freeVisitsCButton;

    public AnimalsForClient(JFrame parent) {
        setTitle("Животные");
        setLocationRelativeTo(parent);
        setContentPane(informationPanel);
        setMinimumSize(DesignUtils.TABLES_FORM_MINIMUM_SIZE);
        if (parent != null) setSize(parent.getSize());
        setLocationRelativeTo(parent);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        transitionsLogic();
        animalsCButton.setForeground(DesignUtils.SUB_MAIN_COLOR);

        updateTableUI(fillInAnimals());
        defaultFieldsState();
        changeSelectedVisibility(false);
        comboBoxSpeciesAnimal.setSelectedItem(null);

        vaccinationsButton.addActionListener(e -> {
            new VaccinationForm(this, selectedAnimal, true);
            dispose();
        });

        addButton.addActionListener(e -> {
            if (isAnimalNotValid()) return;

            Database.getInstance().insertOrUpdate(Mapper.mapFromDTO(AnimalDTO.builder()
                            .name(textFieldName.getText())
                            .species(comboBoxSpeciesAnimal.getSelectedIndex())
                            .breed(textFieldBreed.getText())
                            .age(Integer.parseInt(textFieldAge.getText()))
                            .client_id(ConstantUtils.authorizedUser.getId())
                            .build(),
                    AnimalDTO.class, Animal.class));

            new NotificationForm(this, NotificationType.SUCCESS, NotificationLocation.TOP_CENTER, String.format("Животное %s добавлено", textFieldName.getText())).showNotification();
            updateTableUI(fillInAnimals());
            clearButton.doClick();
        });

        updateButton.addActionListener(e -> {
            if (isAnimalNotValid()) return;

            Database.getInstance().insertOrUpdate(Mapper.mapFromDTO(AnimalDTO.builder()
                            .id(selectedAnimal.getId())
                            .name(textFieldName.getText())
                            .species(comboBoxSpeciesAnimal.getSelectedIndex())
                            .breed(textFieldBreed.getText())
                            .age(Integer.parseInt(textFieldAge.getText()))
                            .build(),
                    AnimalDTO.class, Animal.class));

            new NotificationForm(this, NotificationType.SUCCESS, NotificationLocation.TOP_CENTER, String.format("Животное %s изменено", textFieldName.getText())).showNotification();
            updateTableUI(fillInAnimals());
            clearButton.doClick();
        });

        deleteButton.addActionListener(e -> {
            try {
                Database.getInstance().delete(Animal.class, selectedAnimal.getId());
            } catch (Exception ignored) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, ("Нельзя удалять данное животное!")).showNotification();
                return;
            }
            new NotificationForm(this, NotificationType.SUCCESS, NotificationLocation.TOP_CENTER, String.format("Животное %s удалено", textFieldName.getText())).showNotification();
            clearButton.doClick();
            updateTableUI(fillInAnimals());
        });

        clearButton.addActionListener(e -> {
            textFieldName.setText("");
            comboBoxSpeciesAnimal.setSelectedItem(null);
            textFieldBreed.setText("");
            textFieldAge.setText("");

            table.clearSelection();

            changeSelectedVisibility(false);
        });

        exitButton.addActionListener(e -> exitFromAccount());
    }

    private void createUIComponents() {
        comboBoxSpeciesAnimal = new CustomComboBox(Stream.of(PossibleAnimalSpecies.values()).map(species -> species.localizeString).toArray(String[]::new));
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

                String[] possibleSpecies = Stream.of(PossibleAnimalSpecies.values()).map(species -> species.localizeString).toArray(String[]::new);
                int selectedIndex = 0;
                for (int i = 0; i < possibleSpecies.length; i++) {
                    if (Objects.equals(possibleSpecies[i], state.get(2))) {
                        selectedIndex = i;
                        break;
                    }
                }
                comboBoxSpeciesAnimal.setSelectedIndex(selectedIndex);
                textFieldBreed.setText(state.get(3));
                textFieldAge.setText(state.get(4));

                selectedAnimal = Mapper.mapToDTO(Database.getInstance().select(Mapper.mapFromDTO(AnimalDTO.builder()
                                .id(Integer.valueOf(state.get(0)))
                                .build(),
                        AnimalDTO.class, Animal.class)), AnimalDTO.class, Animal.class).get(0);

                changeSelectedVisibility(true);
            }
        });
    }

    private DefaultTableModel fillInAnimals() {
        String[] header = new String[]{"Id", "Кличка", "Вид", "Порода", "Возраст"};
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, header) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        for (AnimalDTO animalDTO : Mapper.mapToDTO(Database.getInstance().select(Animal.builder().client_id(ConstantUtils.authorizedUser.getId()).build()), AnimalDTO.class, Animal.class))
            model.addRow(new Object[]{
                    animalDTO.getId(),
                    animalDTO.getName(),
                    PossibleAnimalSpecies.values()[animalDTO.getSpecies()].localizeString,
                    animalDTO.getBreed(),
                    animalDTO.getAge().toString(),
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
        textFieldBreed.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldAge.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
    }

    private void exitFromAccount() {
        ConstantUtils.authorizedUser = null;
        new LoginForm(null).showNotification(NotificationType.SUCCESS, "Выход произведен успешно!");
        dispose();
    }

    private void changeSelectedVisibility(boolean visibility) {
        clearButton.setVisible(visibility);
        updateButton.setVisible(visibility);
        deleteButton.setVisible(visibility);
        vaccinationsButton.setVisible(visibility);
        addButton.setVisible(!visibility);
        if (!visibility) selectedAnimal = null;
    }

    private boolean isAnimalNotValid() {
        if (textFieldName.getText().isEmpty()
                || comboBoxSpeciesAnimal.getSelectedItem() == null
                || textFieldBreed.getText().isEmpty()
                || textFieldAge.getText().isEmpty()) {
            new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Необходимо заполнить все поля!").showNotification();
            return true;
        }

        try {
            Integer.parseInt(textFieldAge.getText());
        } catch (Exception ignored) {
            new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Возраст введен некорректно!").showNotification();
            return true;
        }

        return false;
    }

    private void transitionsLogic() {
        switch (ConstantUtils.authorizedUser.getRole()) {
            case 0 -> clientTransitionPanel.setVisible(true);
            case 1, 2 -> clientTransitionPanel.setVisible(false);
        }

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
