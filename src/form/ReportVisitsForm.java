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
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReportVisitsForm extends JFrame{

    private List<CureDTO> tempCures;
    private List<CureDTO> attachedCures;

    private CureDTO selectedCure = null;
    private VisitDTO selectedVisit = null;


    private JScrollPane scrollPane;
    private JTable table;
    private JScrollPane scrollPaneCure;
    private JTable tableCure;
    private JPanel informationPanel;
    private JButton exitButton;
    private JTextField textFieldRecommendation;
    private JButton cancelButton;
    private JButton addButton;
    private JButton deleteButton;
    private JPanel cardPanel;
    private JComboBox<String> comboBoxCure;
    private JButton saveButton;
    private JButton clearButton;
    private JLabel attachedCuresLabel;
    private JPanel doctorTransitionPanel;
    private JButton curesDButton;
    private JButton animalsDButton;
    private JButton historyVisitsDButton;
    private JButton freeVisitsDButton;
    private JButton reportCreatingDButton;

    public ReportVisitsForm(JFrame parent) {
        setTitle("Создание отчетов");
        setLocationRelativeTo(parent);
        setContentPane(informationPanel);
        setMinimumSize(DesignUtils.TABLES_FORM_MINIMUM_SIZE);
        if (parent != null) setSize(parent.getSize());
        setLocationRelativeTo(parent);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        transitionsLogic();
        reportCreatingDButton.setForeground(DesignUtils.SUB_MAIN_COLOR);

        textFieldRecommendation.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        selectionCureVisibility(false);
        selectionVisitVisibility(false);
        comboBoxCure.setSelectedItem(null);
        cardPanel.setVisible(false);
        updateVisitTableUI(fillInNonRecommendedVisits());
        updateCureTableUI(fillInAttachedCures());

        addButton.addActionListener(e -> {

            if (comboBoxCure.getSelectedItem() == null) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, ("Выберите лекарство, прежде чем прикреплять!")).showNotification();
                return;
            }

            int id = comboBoxCure.getSelectedIndex();
            attachedCures.add(tempCures.get(id));
            //new NotificationForm(this, NotificationType.SUCCESS, NotificationLocation.TOP_CENTER, String.format("Лекарство %s прикреплено к приему", tempCures.get(id).getName())).showNotification();
            tempCures.remove(id);

            reloadComboBoxCures();
            updateCureTableUI(fillInAttachedCures());
        });

        deleteButton.addActionListener(e -> {
            tempCures = Mapper.mapToDTO(Database.getInstance().select(Cure.builder().build()), CureDTO.class, Cure.class);
            List<CureDTO> ids = new ArrayList<>();
            for (CureDTO tempCure : tempCures)
                for (CureDTO attachedCure : attachedCures)
                    if (Objects.equals(tempCure.getId(), attachedCure.getId())) ids.add(tempCure);

            if (!tempCures.isEmpty()) for (CureDTO id : ids) tempCures.remove(id);

            int idToDelete = -1;

            for (int i = 0; i < attachedCures.size(); i++) if (Objects.equals(attachedCures.get(i).getId(), selectedCure.getId())) idToDelete = i;
            attachedCures.remove(idToDelete);

            tempCures.add(selectedCure);

            reloadComboBoxCures();
            updateCureTableUI(fillInAttachedCures());
            tableCure.clearSelection();
            //new NotificationForm(this, NotificationType.SUCCESS, NotificationLocation.TOP_CENTER, String.format("Лекарство %s откреплено от приема", selectedCure.getName())).showNotification();
            clearButton.doClick();
        });

        saveButton.addActionListener(e -> {

            if (textFieldRecommendation.getText().isEmpty() || attachedCures.isEmpty()) {
                new NotificationForm(this, NotificationType.WARNING, NotificationLocation.TOP_CENTER, ("Необходимо выбрать лекарства и написать рекомендацию!")).showNotification();
                return;
            }

            Database.getInstance().insertOrUpdate(Mapper.mapFromDTO(VisitDTO.builder()
                            .id(selectedVisit.getId())
                            .recommendation(textFieldRecommendation.getText())
                            .build(),
                    VisitDTO.class, Visit.class));

            for (CureDTO attachedCure : attachedCures) {
                Database.getInstance().insertOrUpdate(Mapper.mapFromDTO(Visit_cureDTO.builder()
                                .visit_id(selectedVisit.getId())
                                .cure_id(attachedCure.getId())
                                .build(),
                        Visit_cureDTO.class, Visit_cure.class));
            }

            new NotificationForm(this, NotificationType.SUCCESS, NotificationLocation.TOP_CENTER, "Отчет успешно создан!").showNotification();

            UsersDTO doctor = Mapper.mapToDTO(Database.getInstance().select(Users.builder().id(selectedVisit.getDoctor_id()).role(UserRole.Doctor.ordinal()).build()), UsersDTO.class, Users.class).get(0);
            UsersDTO client = Mapper.mapToDTO(Database.getInstance().select(Users.builder().id(selectedVisit.getClient_id()).role(UserRole.Client.ordinal()).build()), UsersDTO.class, Users.class).get(0);
            AnimalDTO animal = Mapper.mapToDTO(Database.getInstance().select(Animal.builder().id(selectedVisit.getAnimal_id()).build()), AnimalDTO.class, Animal.class).get(0);

            StringBuilder cures = new StringBuilder();
            for (CureDTO c : attachedCures) {
                cures.append(c.getName()).append("; ");
            }

            MailSender.sendMail(
                    client.getMail(),
                    "Отчет по приему",
                    "Дата: " + selectedVisit.getDate() + "\n" +
                            "Животное: " + animal.getName() + "\n" +
                            "Врач: " + doctor.getName() + "(" + doctor.getMail() + ")" + "\n" +
                            "Рекомендации: " + textFieldRecommendation.getText() + "\n" +
                            "Назначенные лекарства: " + cures
            );

            MailSender.sendMail(
                    doctor.getMail(),
                    "Отчет по приему",
                    "Дата: " + selectedVisit.getDate() + "\n" +
                            "Животное: " + animal.getName() + "\n" +
                            "Клиент: " + client.getName() + "(" + client.getMail() + ")" + "\n" +
                            "Рекомендации: " + textFieldRecommendation.getText() + "\n" +
                            "Назначенные лекарства: " + cures
            );

            tempCures = Mapper.mapToDTO(Database.getInstance().select(Cure.builder().build()), CureDTO.class, Cure.class);
            attachedCures = new ArrayList<>();
            selectionVisitVisibility(false);
            reloadComboBoxCures();
            table.clearSelection();
            updateVisitTableUI(fillInNonRecommendedVisits());
        });

        cancelButton.addActionListener(e -> {
            tempCures = Mapper.mapToDTO(Database.getInstance().select(Cure.builder().build()), CureDTO.class, Cure.class);
            attachedCures = new ArrayList<>();
            selectionVisitVisibility(false);
            reloadComboBoxCures();
            table.clearSelection();
            textFieldRecommendation.setText("");
        });

        clearButton.addActionListener(e -> {
            comboBoxCure.setSelectedItem(null);
            selectionCureVisibility(false);
            tableCure.clearSelection();
        });

        exitButton.addActionListener(e -> exitFromAccount());
    }

    private void selectionCureVisibility(boolean isCureSelected) {
        if (!isCureSelected) selectedCure = null;
        comboBoxCure.setVisible(!isCureSelected);
        addButton.setVisible(!isCureSelected);
        deleteButton.setVisible(isCureSelected);
        clearButton.setVisible(isCureSelected);
    }

    private void selectionVisitVisibility(boolean isVisitSelected) {
        if (!isVisitSelected) selectedVisit = null;
        cardPanel.setVisible(isVisitSelected);
        scrollPaneCure.setVisible(isVisitSelected);
        attachedCuresLabel.setVisible(isVisitSelected);
    }

    private void reloadComboBoxCures() {
        comboBoxCure.setSelectedItem(null);
        comboBoxCure.setModel(new DefaultComboBoxModel<>(tempCures.stream().map(CureDTO::getName).toArray(String[]::new)));
        comboBoxCure.setSelectedItem(null);
    }

    private void createUIComponents() {
        tempCures = Mapper.mapToDTO(Database.getInstance().select(Cure.builder().build()), CureDTO.class, Cure.class);
        attachedCures = new ArrayList<>();

        comboBoxCure = new CustomComboBox(tempCures.stream().map(CureDTO::getName).toArray(String[]::new));

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
                selectedVisit = Mapper.mapToDTO(Database.getInstance().select(Visit.builder().id(Integer.parseInt(state.get(0))).build()), VisitDTO.class, Visit.class).get(0);
                attachedCures = new ArrayList<>();
                updateCureTableUI(fillInAttachedCures());
                selectionVisitVisibility(true);
            }
        });


        tableCure = new CustomTable();
        scrollPaneCure = new JScrollPane();
        ((CustomTable) tableCure).fixTable(scrollPaneCure);
        tableCure.getSelectionModel().addListSelectionListener(event -> {
            int viewRow = tableCure.getSelectedRow();
            if (!event.getValueIsAdjusting() && viewRow != -1) {
                int modelRow = tableCure.convertRowIndexToModel(viewRow);
                List<String> state = new ArrayList<>();
                for (int i = 0; i <= tableCure.getColumnCount(); i++) {
                    Object modelValue = tableCure.getModel().getValueAt(modelRow, i);
                    state.add(modelValue.toString());
                }
                selectedCure = Mapper.mapToDTO(Database.getInstance().select(Cure.builder().id(Integer.parseInt(state.get(0))).build()), CureDTO.class, Cure.class).get(0);
                selectionCureVisibility(true);
            }
        });

    }

    private void updateVisitTableUI(DefaultTableModel model) {
        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.removeColumn(table.getColumnModel().getColumn(0));
        table.setFont(DesignUtils.REGULAR_FONT);
    }

    private void updateCureTableUI(DefaultTableModel model) {
        tableCure.setModel(model);
        tableCure.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableCure.removeColumn(tableCure.getColumnModel().getColumn(0));
        tableCure.setFont(DesignUtils.REGULAR_FONT);
    }

    private DefaultTableModel fillInNonRecommendedVisits() {
        String[] header = new String[]{"Id", "Дата", "Клиент", "Животное"};
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, header) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        for (Visit visitDTO : Database.getInstance().select(Visit.builder().doctor_id(ConstantUtils.authorizedUser.getId()).build()).stream()
                .filter(vdto -> vdto.getClient_id() != 0 && vdto.getAnimal_id() != 0 && (vdto.getRecommendation() == null || vdto.getRecommendation().isEmpty()))
                .toList()) {

            model.addRow(new Object[] {
                    visitDTO.getId(),
                    visitDTO.getDate().toString().substring(0, visitDTO.getDate().toString().length() - 5),
                    Mapper.mapToDTO(Database.getInstance().select(Users.builder().id(visitDTO.getClient_id()).role(UserRole.Client.ordinal()).build()), UsersDTO.class, Users.class).get(0).getName(),
                    Mapper.mapToDTO(Database.getInstance().select(Animal.builder().id(visitDTO.getAnimal_id()).build()), AnimalDTO.class, Animal.class).get(0).getName(),
            });

        }
        return model;
    }

    private DefaultTableModel fillInAttachedCures() {
        String[] header = new String[]{"Id", "Название", "Категория", "Цена", "Производитель"};
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, header) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        for (CureDTO cure : attachedCures)
            model.addRow(new Object[]{ cure.getId(), cure.getName(), cure.getCategory(), cure.getPrice().toString(), cure.getManufacturer() });
        return model;
    }

    private void exitFromAccount() {
        ConstantUtils.authorizedUser = null;
        new LoginForm(null).showNotification(NotificationType.SUCCESS, "Выход произведен успешно!");
        dispose();
    }

    private void transitionsLogic() {
        switch (ConstantUtils.authorizedUser.getRole()) {
            case 0, 2 -> doctorTransitionPanel.setVisible(false);
            case 1 -> doctorTransitionPanel.setVisible(true);
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
    }
}
