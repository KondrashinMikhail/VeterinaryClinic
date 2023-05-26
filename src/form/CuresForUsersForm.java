package form;

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

public class CuresForUsersForm extends JFrame {
    private JScrollPane scrollPane;
    private JTable table;
    private JPanel cardPanel;
    private JTextField textFieldName;
    private JTextField textFieldCategory;
    private JTextField textFieldPrice;
    private JButton clearButton;
    private JButton exitButton;
    private JTextField textFieldManufacturer;
    private JPanel informationPanel;
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
    private JButton accountDButton;
    private JButton accountCButton;

    public CuresForUsersForm(JFrame parent) {
        setTitle("Лекарства");
        setLocationRelativeTo(parent);
        setContentPane(informationPanel);
        setMinimumSize(DesignUtils.TABLES_FORM_MINIMUM_SIZE);
        if (parent != null) setSize(parent.getSize());
        setLocationRelativeTo(parent);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        transitionsLogic();
        curesDButton.setForeground(DesignUtils.SUB_MAIN_COLOR);
        curesCButton.setForeground(DesignUtils.SUB_MAIN_COLOR);

        cardPanel.setVisible(false);
        updateTableUI(fillInCures());
        defaultFieldsState();

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
                textFieldCategory.setText(state.get(2));
                textFieldPrice.setText(state.get(3));
                textFieldManufacturer.setText(state.get(4));

                cardPanel.setVisible(true);
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
        textFieldName.setEditable(false);

        textFieldCategory.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldCategory.setEditable(false);

        textFieldPrice.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldPrice.setEditable(false);

        textFieldManufacturer.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldManufacturer.setEditable(false);

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
            }
            case 1 -> {
                clientTransitionPanel.setVisible(false);
                doctorTransitionPanel.setVisible(true);
            }
            case 2 -> {
                clientTransitionPanel.setVisible(false);
                doctorTransitionPanel.setVisible(false);
            }
        }

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
