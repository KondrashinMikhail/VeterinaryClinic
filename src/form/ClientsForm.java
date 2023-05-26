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

import static utils.ConstantUtils.*;

public class ClientsForm extends JFrame {
    private JScrollPane scrollPane;
    private JTable table;
    private JPanel informationPanel;
    private JTextField textFieldNameUser;
    private JTextField textFieldMailUser;
    private JTextField textFieldLoginUser;
    private JTextField textFieldPasswordUser;
    private JButton clearButton;
    private JButton viewPasswordButton;
    private JButton exitButton;
    private JPanel cardPanel;
    private JPanel adminTransitionPanel;
    private JButton clientsAButton;
    private JButton doctorsAButton;
    private JButton curesAButton;
    private JButton animalsAButton;
    private JButton freeVisitsAButton;
    private JButton occupiedVisitsAButton;
    private JButton timetableCreationAButton;

    public ClientsForm(JFrame parent) {
        setTitle("Клиенты");
        setLocationRelativeTo(parent);
        setContentPane(informationPanel);
        setMinimumSize(DesignUtils.TABLES_FORM_MINIMUM_SIZE);
        if (parent != null) setSize(parent.getSize());
        setLocationRelativeTo(parent);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        transitionsLogic();
        clientsAButton.setForeground(DesignUtils.SUB_MAIN_COLOR);

        cardPanel.setVisible(false);
        updateTableUI(fillInClients());
        defaultFieldsState();

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
                            Mapper.mapToDTO(
                                            Database.getInstance()
                                                    .select(Users.builder().login(textFieldLoginUser.getText()).build()), UsersDTO.class, Users.class)
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
            table.clearSelection();
            cardPanel.setVisible(false);
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

                textFieldNameUser.setText(state.get(1));
                textFieldLoginUser.setText(state.get(2));
                textFieldMailUser.setText(state.get(3));

                cardPanel.setVisible(true);
            }
        });
    }

    private DefaultTableModel fillInClients() {
        String[] header = new String[]{"Id", "Имя", "Логин", "Почта"};
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, header) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        for (UsersDTO user : Mapper.mapToDTO(Database.getInstance().select(Users.builder().role(UserRole.Client.ordinal()).build()), UsersDTO.class, Users.class))
            model.addRow(new Object[]{ user.getId(), user.getName(), user.getLogin(), user.getMail() });
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

        textFieldLoginUser.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldLoginUser.setEditable(false);

        textFieldPasswordUser.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldPasswordUser.setVisible(false);
        textFieldPasswordUser.setEditable(false);
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
