package form;

import enums.modelsEnums.UserRole;
import enums.notificationEnums.NotificationType;
import form.customGraphics.table.CustomTable;
import storage.DTOs.*;
import storage.Database;
import storage.Mapper;
import storage.models.*;
import utils.ConstantUtils;
import utils.DesignUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class HistoryVisitsForm extends JFrame {
    private JScrollPane scrollPane;
    private JTable table;
    private JButton exitButton;
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

    public HistoryVisitsForm(JFrame parent) {
        setTitle("История приемов");
        setLocationRelativeTo(parent);
        setContentPane(informationPanel);
        setMinimumSize(DesignUtils.TABLES_FORM_MINIMUM_SIZE);
        if (parent != null) setSize(parent.getSize());
        setLocationRelativeTo(parent);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        transitionsLogic();
        historyVisitsDButton.setForeground(DesignUtils.SUB_MAIN_COLOR);
        historyVisitsCButton.setForeground(DesignUtils.SUB_MAIN_COLOR);

        switch (ConstantUtils.authorizedUser.getRole()) {
            case 0 -> updateTableUI(fillInVisitsForClients());
            case 1 -> updateTableUI(fillInVisitsForDoctors());
        }

        exitButton.addActionListener(e -> exitFromAccount());
    }

    private void createUIComponents() {
        table = new CustomTable();
        scrollPane = new JScrollPane();
        ((CustomTable) table).fixTable(scrollPane);
    }

    private DefaultTableModel fillInVisitsForClients() {
        String[] header = new String[]{"Id", "Дата", "Врач", "Животное", "Рекомендации", "Назначенные лекарства"};
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, header) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        for (VisitDTO visitDTO : Mapper.mapToDTO(Database.getInstance().select(Visit.builder().client_id(ConstantUtils.authorizedUser.getId()).build()), VisitDTO.class, Visit.class).stream()
                .filter(vdto -> vdto.getClient_id() != null && vdto.getAnimal_id() != null).toList()) {

            StringBuilder curesString = new StringBuilder();

            for (Visit_cureDTO vc :
                    Mapper.mapToDTO(Database.getInstance().select(Visit_cure.builder().visit_id(visitDTO.getId()).build()), Visit_cureDTO.class, Visit_cure.class))
                curesString.append(Mapper.mapToDTO(Database.getInstance().select(Cure.builder().id(vc.getCure_id()).build()), CureDTO.class, Cure.class).get(0).getName()).append("; ");

            model.addRow(new Object[] {
                    visitDTO.getId(),
                    visitDTO.getDate().toString().substring(0, visitDTO.getDate().toString().length() - 5),
                    Mapper.mapToDTO(Database.getInstance().select(Users.builder().id(visitDTO.getDoctor_id()).role(UserRole.Doctor.ordinal()).build()), UsersDTO.class, Users.class).get(0).getName(),
                    Mapper.mapToDTO(Database.getInstance().select(Animal.builder().id(visitDTO.getAnimal_id()).build()), AnimalDTO.class, Animal.class).get(0).getName(),
                    visitDTO.getRecommendation(),
                    curesString.toString()
            });

        }
        return model;
    }

    private DefaultTableModel fillInVisitsForDoctors() {
        String[] header = new String[]{"Id", "Дата", "Клиент", "Животное", "Рекомендации", "Назначенные лекарства"};
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, header) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        for(VisitDTO visitDTO : Mapper.mapToDTO(Database.getInstance().select(Visit.builder().doctor_id(ConstantUtils.authorizedUser.getId()).build()), VisitDTO.class, Visit.class).stream()
                .filter(vdto -> vdto.getClient_id() != 0 && vdto.getAnimal_id() != 0).toList()) {

            StringBuilder curesString = new StringBuilder();

            for (Visit_cureDTO vc :
                    Mapper.mapToDTO(Database.getInstance().select(Visit_cure.builder().visit_id(visitDTO.getId()).build()), Visit_cureDTO.class, Visit_cure.class))
                curesString.append(Mapper.mapToDTO(Database.getInstance().select(Cure.builder().id(vc.getCure_id()).build()), CureDTO.class, Cure.class).get(0).getName()).append("; ");

            model.addRow(new Object[] {
                    visitDTO.getId(),
                    visitDTO.getDate().toString().substring(0, visitDTO.getDate().toString().length() - 5),
                    Mapper.mapToDTO(Database.getInstance().select(Users.builder().id(visitDTO.getClient_id()).role(UserRole.Client.ordinal()).build()), UsersDTO.class, Users.class).get(0).getName(),
                    Mapper.mapToDTO(Database.getInstance().select(Animal.builder().id(visitDTO.getAnimal_id()).build()), AnimalDTO.class, Animal.class).get(0).getName(),
                    visitDTO.getRecommendation(),
                    curesString.toString()
            });

        }
        return model;
    }

    private void updateTableUI(DefaultTableModel model) {
        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.removeColumn(table.getColumnModel().getColumn(0));
        table.setFont(DesignUtils.REGULAR_FONT);
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
