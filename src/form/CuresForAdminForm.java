package form;

import enums.notificationEnums.NotificationLocation;
import enums.notificationEnums.NotificationType;
import form.customGraphics.table.CustomTable;
import storage.DTOs.CureDTO;
import storage.Database;
import storage.Mapper;
import storage.models.Cure;
import utils.ConstantUtils;
import utils.DesignUtils;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class CuresForAdminForm extends JFrame {
    private CureDTO selectedCure;
    private JScrollPane scrollPane;
    private JTable table;
    private JPanel informationPanel;
    private JTextField textFieldName;
    private JTextField textFieldCategory;
    private JTextField textFieldPrice;
    private JButton clearButton;
    private JTextField textFieldManufacturer;
    private JButton exitButton;
    private JButton updateButton;
    private JButton addButton;
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

    public CuresForAdminForm(JFrame parent) {
        setTitle("Лекарства");
        setLocationRelativeTo(parent);
        setContentPane(informationPanel);
        setMinimumSize(DesignUtils.TABLES_FORM_MINIMUM_SIZE);
        if (parent != null) setSize(parent.getSize());
        setLocationRelativeTo(parent);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        transitionsLogic();
        curesAButton.setForeground(DesignUtils.SUB_MAIN_COLOR);

        updateTableUI(fillInCures());
        defaultFieldsState();
        changeSelectedVisibility(false);

        addButton.addActionListener(e -> {
            if (isCureNotValid()) return;

            Database.getInstance().insertOrUpdate(Mapper.mapFromDTO(CureDTO.builder()
                            .name(textFieldName.getText())
                            .category(textFieldCategory.getText())
                            .price(Double.parseDouble(textFieldPrice.getText()))
                            .manufacturer(textFieldManufacturer.getText())
                            .build(),
                    CureDTO.class, Cure.class));

            new NotificationForm(this, NotificationType.SUCCESS, NotificationLocation.TOP_CENTER, String.format("Лекарство %s добавлено", textFieldName.getText())).showNotification();
            updateTableUI(fillInCures());
            clearButton.doClick();
        });
        updateButton.addActionListener(e -> {
            if (isCureNotValid()) return;

            Database.getInstance().insertOrUpdate(Mapper.mapFromDTO(CureDTO.builder()
                            .id(selectedCure.getId())
                            .name(textFieldName.getText())
                            .category(textFieldCategory.getText())
                            .price(Double.parseDouble(textFieldPrice.getText()))
                            .manufacturer(textFieldManufacturer.getText())
                            .build(),
                    CureDTO.class, Cure.class));

            new NotificationForm(this, NotificationType.SUCCESS, NotificationLocation.TOP_CENTER, String.format("Лекарство %s изменено", selectedCure.getName())).showNotification();
            updateTableUI(fillInCures());
            clearButton.doClick();
        });

        clearButton.addActionListener(e -> {
            textFieldName.setText("");
            textFieldCategory.setText("");
            textFieldPrice.setText("");
            textFieldManufacturer.setText("");

            table.clearSelection();

            changeSelectedVisibility(false);
        });

        deleteButton.addActionListener(e -> {
            try {
                Database.getInstance().delete(Cure.class, selectedCure.getId());
            } catch (Exception ignored) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, ("Нельзя удалять данное лекарство!")).showNotification();
                return;
            }
            new NotificationForm(this, NotificationType.SUCCESS, NotificationLocation.TOP_CENTER, String.format("Лекарство %s удалено", textFieldName.getText())).showNotification();
            clearButton.doClick();
            updateTableUI(fillInCures());
        });

        exitButton.addActionListener(e -> exitFromAccount());
    }

    private boolean isCureNotValid() {
        if (textFieldName.getText().isEmpty()
                || textFieldCategory.getText().isEmpty()
                || textFieldPrice.getText().isEmpty()
                || textFieldManufacturer.getText().isEmpty()) {
            new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Необходимо заполнить все поля!").showNotification();
            return true;
        }

        try {
            Double.parseDouble(textFieldPrice.getText());
        } catch (Exception ignored) {
            new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, "Цена введена некорректно!").showNotification();
            return true;
        }

        return false;
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
                textFieldCategory.setText(state.get(2));
                textFieldPrice.setText(state.get(3));
                textFieldManufacturer.setText(state.get(4));

                selectedCure = Mapper.mapToDTO(Database.getInstance().select(Mapper.mapFromDTO(CureDTO.builder()
                                .id(Integer.valueOf(state.get(0)))
                                .build(),
                        CureDTO.class, Cure.class)), CureDTO.class, Cure.class).get(0);

                changeSelectedVisibility(true);
            }
        });
    }

    private DefaultTableModel fillInCures() {
        String[] header = new String[]{"Id", "Название", "Категория", "Цена", "Производитель"};
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, header) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        for (CureDTO cure : Mapper.mapToDTO(Database.getInstance().select(Cure.builder().build()), CureDTO.class, Cure.class))
            model.addRow(new Object[]{ cure.getId(), cure.getName(), cure.getCategory(), cure.getPrice().toString(), cure.getManufacturer() });
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
        textFieldCategory.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldPrice.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldManufacturer.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
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
        addButton.setVisible(!visibility);
        if (!visibility) selectedCure = null;
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
