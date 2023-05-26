package form;

import enums.notificationEnums.NotificationLocation;
import enums.notificationEnums.NotificationType;
import form.customGraphics.comboBox.CustomComboBox;
import form.customGraphics.table.CustomTable;
import storage.DTOs.AnimalDTO;
import storage.DTOs.CureDTO;
import storage.DTOs.VaccinationDTO;
import storage.Database;
import storage.Mapper;
import storage.models.Cure;
import storage.models.Vaccination;
import utils.ConstantUtils;
import utils.DesignUtils;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class VaccinationForm extends JFrame {
    private final AnimalDTO currentAnimal;
    private final List<CureDTO> cures;
    private VaccinationDTO selectedVaccination;
    private JScrollPane scrollPane;
    private JTable table;
    private JTextField textFieldYear;
    private JTextField textFieldMonth;
    private JTextField textFieldDay;
    private JTextField textFieldHour;
    private JTextField textFieldMinute;
    private JButton exitButton;
    private JComboBox<String> comboBoxCures;
    private JTextField textFieldCure;
    private JButton addButton;
    private JButton clearButton;
    private JButton deleteButton;
    private JLabel labelState;
    private JPanel informationPanel;
    private JButton goBackButton;

    public VaccinationForm(JFrame parent, AnimalDTO currentAnimal, boolean isClientCalled) {
        this.currentAnimal = currentAnimal;
        cures = Mapper.mapToDTO(Database.getInstance().select(Mapper.mapFromDTO(CureDTO.builder().build(), CureDTO.class, Cure.class)), CureDTO.class, Cure.class);
        labelState.setText(labelState.getText() + " " + currentAnimal.getName());

        setTitle("Выписанные лекарства");
        setLocationRelativeTo(parent);
        setContentPane(informationPanel);
        setMinimumSize(DesignUtils.TABLES_FORM_MINIMUM_SIZE);
        if (parent != null) setSize(parent.getSize());
        setLocationRelativeTo(parent);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        comboBoxCures.setSelectedItem(null);
        changeSelectedVisibility(false);
        defaultFieldsState();
        updateTableUI(fillInVaccinations());

        goBackButton.addActionListener(e -> {
            if (isClientCalled) new AnimalsForClient(this);
            else new AnimalsForUsers(this);
            dispose();
        });

        clearButton.addActionListener(e -> {
            textFieldCure.setText("");
            textFieldYear.setText("");
            textFieldMonth.setText("");
            textFieldDay.setText("");
            textFieldHour.setText("");
            textFieldMinute.setText("");
            comboBoxCures.setSelectedItem(null);

            table.clearSelection();

            changeSelectedVisibility(false);

            selectedVaccination = null;
        });

        addButton.addActionListener(e -> {
            if (isVaccinationNotValid()) return;

            Database.getInstance().insertOrUpdate(Mapper.mapFromDTO(VaccinationDTO.builder()
                                    .date(new Timestamp(
                                                    Integer.parseInt(textFieldYear.getText()) - 1900,
                                                    Integer.parseInt(textFieldMonth.getText()) - 1,
                                                    Integer.parseInt(textFieldDay.getText()),
                                                    Integer.parseInt(textFieldHour.getText()),
                                                    Integer.parseInt(textFieldMinute.getText()),
                                                    0, 0))
                                    .animal_id(currentAnimal.getId())
                                    .cure_id(cures.get(comboBoxCures.getSelectedIndex()).getId())
                            .build(),
                    VaccinationDTO.class, Vaccination.class));

            new NotificationForm(this, NotificationType.SUCCESS, NotificationLocation.TOP_CENTER, "Запись добавлена").showNotification();
            updateTableUI(fillInVaccinations());
            clearButton.doClick();
        });

        deleteButton.addActionListener(e -> {
            try {
                Database.getInstance().delete(Vaccination.class, selectedVaccination.getId());
            } catch (Exception ignored) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, ("Нельзя удалять данную запись!")).showNotification();
                return;
            }
            new NotificationForm(this, NotificationType.SUCCESS, NotificationLocation.TOP_CENTER, "Запись удалена").showNotification();
            clearButton.doClick();
            updateTableUI(fillInVaccinations());
        });

        exitButton.addActionListener(e -> exitFromAccount());
    }

    private void createUIComponents() {
        comboBoxCures = new CustomComboBox(Mapper.mapToDTO(Database.getInstance().select(Mapper.mapFromDTO(CureDTO.builder().build(), CureDTO.class, Cure.class)), CureDTO.class, Cure.class)
                .stream().map(CureDTO::getName).toArray(String[]::new));
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

                String[] fullDate = state.get(1).split(" ");
                String[] date = fullDate[0].split("-");
                String[] time = fullDate[1].split(":");

                textFieldYear.setText(date[0]);
                textFieldMonth.setText(date[1]);
                textFieldDay.setText(date[2]);

                textFieldHour.setText(time[0]);
                textFieldMinute.setText(time[1]);

                textFieldCure.setText(state.get(2));

                selectedVaccination = Mapper.mapToDTO(Database.getInstance().select(Mapper.mapFromDTO(VaccinationDTO.builder()
                                .id(Integer.valueOf(state.get(0)))
                                .build(),
                        VaccinationDTO.class, Vaccination.class)), VaccinationDTO.class, Vaccination.class).get(0);

                changeSelectedVisibility(true);
            }
        });
    }

    private DefaultTableModel fillInVaccinations() {
        String[] header = new String[]{"Id", "Дата", "Лекарство"};
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, header) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        for (VaccinationDTO vaccinationDTO : Mapper.mapToDTO(Database.getInstance().select(Vaccination.builder().animal_id(currentAnimal.getId()).build()), VaccinationDTO.class, Vaccination.class))
            model.addRow(new Object[]{
                    vaccinationDTO.getId(),
                    vaccinationDTO.getDate().toString().substring(0, vaccinationDTO.getDate().toString().length() - 5),
                    Mapper.mapToDTO(Database.getInstance().select(Cure.builder().id(vaccinationDTO.getCure_id()).build()), CureDTO.class, Cure.class).get(0).getName(),
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
        textFieldCure.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldCure.setEditable(false);
        textFieldYear.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldMonth.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldDay.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldHour.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldMinute.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
    }

    private void changeSelectedVisibility(boolean visibility) {
        clearButton.setVisible(visibility);
        deleteButton.setVisible(visibility);
        textFieldCure.setVisible(visibility);
        comboBoxCures.setVisible(!visibility);
        addButton.setVisible(!visibility);
        textFieldYear.setEditable(!visibility);
        textFieldMonth.setEditable(!visibility);
        textFieldDay.setEditable(!visibility);
        textFieldHour.setEditable(!visibility);
        textFieldMinute.setEditable(!visibility);
    }

    private void exitFromAccount() {
        ConstantUtils.authorizedUser = null;
        new LoginForm(null).showNotification(NotificationType.SUCCESS, "Выход произведен успешно!");
        dispose();
    }

    private boolean isVaccinationNotValid() {
        if (textFieldYear.getText().isEmpty()
                || comboBoxCures.getSelectedItem() == null
                || textFieldMonth.getText().isEmpty()
                || textFieldDay.getText().isEmpty()
                || textFieldHour.getText().isEmpty()
                || textFieldMinute.getText().isEmpty()) {
            new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Необходимо заполнить все поля!").showNotification();
            return true;
        }

        try {
            Integer.parseInt(textFieldYear.getText());
            Integer.parseInt(textFieldMonth.getText());
            Integer.parseInt(textFieldDay.getText());
            Integer.parseInt(textFieldHour.getText());
            Integer.parseInt(textFieldMinute.getText());
        } catch (Exception ignored) {
            new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Дата введена некорректно!").showNotification();
            return true;
        }

        return false;
    }
}