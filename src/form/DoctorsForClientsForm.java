package form;

import enums.modelsEnums.UserRole;
import enums.notificationEnums.NotificationType;
import form.customGraphics.table.CustomTable;
import storage.DTOs.UsersDTO;
import storage.Database;
import storage.Mapper;
import storage.models.Users;
import utils.ConstantUtils;
import utils.DesignUtils;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class DoctorsForClientsForm extends JFrame {
    private JScrollPane scrollPane;
    private JTable table;
    private JPanel cardPanel;
    private JTextField textFieldNameUser;
    private JTextField textFieldMailUser;
    private JTextField textFieldSpecializationUser;
    private JButton clearButton;
    private JButton exitButton;
    private JPanel informationPanel;
    private JPanel clientTransitionPanel;
    private JButton doctorsCButton;
    private JButton curesCButton;
    private JButton animalsCButton;
    private JButton historyVisitsCButton;
    private JButton freeVisitsCButton;
    private JButton accountCButton;

    public DoctorsForClientsForm(JFrame parent) {
        setTitle("Врачи");
        setLocationRelativeTo(parent);
        setContentPane(informationPanel);
        setMinimumSize(DesignUtils.TABLES_FORM_MINIMUM_SIZE);
        if (parent != null) setSize(parent.getSize());
        setLocationRelativeTo(parent);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        transitionsLogic();
        doctorsCButton.setForeground(DesignUtils.SUB_MAIN_COLOR);

        cardPanel.setVisible(false);
        updateTableUI(fillInDoctors());
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

                textFieldNameUser.setText(state.get(1));
                textFieldSpecializationUser.setText(state.get(2));
                textFieldMailUser.setText(state.get(3));

                cardPanel.setVisible(true);
            }
        });
    }

    private DefaultTableModel fillInDoctors() {
        String[] header = new String[]{"Id", "Имя", "Специализация", "Почта"};
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, header) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        for (UsersDTO user : Mapper.mapToDTO(Database.getInstance().select(Users.builder().role(UserRole.Doctor.ordinal()).build()), UsersDTO.class, Users.class))
            model.addRow(new Object[]{ user.getId(), user.getName(), user.getSpecialization(), user.getMail() });
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
        textFieldNameUser.setEditable(false);

        textFieldMailUser.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldMailUser.setEditable(false);

        textFieldSpecializationUser.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldSpecializationUser.setEditable(false);
    }

    private void exitFromAccount() {
        ConstantUtils.authorizedUser = null;
        new LoginForm(null).showNotification(NotificationType.SUCCESS, "Выход произведен успешно!");
        dispose();
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
        accountCButton.addActionListener(e -> {
            new AccountForm(this);
            dispose();
        });
    }
}
