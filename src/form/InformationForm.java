package form;

import enums.modelsEnums.Models;
import enums.modelsEnums.PossibleAnimalSpecies;
import enums.modelsEnums.UserRole;
import form.customGraphics.comboBox.CustomComboBox;
import form.customGraphics.table.CustomTable;
import storage.Database;
import storage.models.*;
import utils.ConstantUtils;
import utils.DesignUtils;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Stream;

public class InformationForm extends JFrame {
    private JPanel informationPanel;
    private JTextField textFieldNameUser;
    private JTextField textFieldMailUser;
    private JTextField textFieldLoginUser;
    private JTextField textFieldPasswordUser;
    private JTextField textFieldSpecializationDoctor;
    private JButton addButton;
    private JButton clearButton;
    private JTable table;
    private JScrollPane scrollPane;
    private JButton animalsButton;
    private JButton curesButton;
    private JButton clientsButton;
    private JButton doctorsButton;
    private JButton visitsButton;
    private JButton deleteButton;
    private JLabel labelNameUser;
    private JLabel labelMailUser;
    private JLabel labelLoginUser;
    private JLabel labelPasswordUser;
    private JLabel labelSpecializationDoctor;
    private JLabel labelNameCure;
    private JTextField textFieldNameCure;
    private JLabel labelCategoryCure;
    private JTextField textFieldCategoryCure;
    private JLabel labelNameAnimal;
    private JTextField textFieldNameAnimal;
    private JComboBox<String> comboBoxSpeciesAnimal;
    private JLabel labelSpeciesAnimal;
    private JLabel labelBreedAnimal;
    private JTextField textFieldBreedAnimal;
    private JLabel labelAgeAnimal;
    private JTextField textFieldAgeAnimal;
    private JLabel labelDate;
    private JTextField textFieldYear;
    private JTextField textFieldMonth;
    private JTextField textFieldDay;
    private JTextField textFieldHour;
    private JTextField textFieldMinute;
    private JLabel labelYear;
    private JLabel labelMonth;
    private JLabel labelDay;
    private JLabel labelHour;
    private JLabel labelMinute;
    private JLabel labelSeparator1;
    private JLabel labelSeparator2;
    private JLabel labelSeparator3;
    private JLabel labelSeparator4;
    private JLabel labelComment;
    private JLabel labelIsClientCame;
    private JComboBox<String> comboBoxIsClientCame;
    private JTextField textFieldCommentVisit;
    private JPanel cardPanel;
    private JButton updateButton;
    private JLabel labelDoctorVisit;
    private JComboBox comboBoxDoctorVisit;
    private JComboBox comboBoxAnimalVisit;
    private JLabel labelAnimalVisit;
    private JButton buttonAppointment;

    private Models selectedModel = null;

    public InformationForm(JFrame parent) {
        setTitle("Информация");
        setLocationRelativeTo(parent);
        setContentPane(informationPanel);
        setMinimumSize(DesignUtils.TABLES_FORM_MINIMUM_SIZE);
        if (parent != null) setSize(parent.getSize());
        setLocationRelativeTo(parent);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        defaultFieldsPaint();
        cardPanel.setVisible(false);
        scrollPane.setVisible(false);
        updateButton.setVisible(false);

        curesButton.addActionListener(e -> updateTableUI(fillInCures()));
        switch (UserRole.values()[ConstantUtils.authorizedUser.getRole()]) {
            case Client -> {
                animalsButton.addActionListener(e -> updateTableUI(fillInAnimals(ConstantUtils.authorizedUser.getId())));
                clientsButton.setVisible(false);
                doctorsButton.setVisible(false);
                visitsButton.addActionListener(e -> updateTableUI(fillInVisits()));
            }
            case Doctor -> {
                animalsButton.addActionListener(e -> updateTableUI(fillInAnimals(null)));
                clientsButton.setVisible(false);
                doctorsButton.setVisible(false);
                visitsButton.addActionListener(e -> updateTableUI(fillInVisits()));
            }
            case Administrator -> {
                animalsButton.addActionListener(e -> updateTableUI(fillInAnimals(null)));
                clientsButton.addActionListener(e -> updateTableUI(fillInClients()));
                doctorsButton.addActionListener(e -> updateTableUI(fillInDoctors()));
                visitsButton.addActionListener(e -> updateTableUI(fillInVisits()));
            }
        }

        animalsButton.addActionListener(e -> {
            selectedModel = Models.Animal;
            defaultVisibility();
            defaultText();

            textFieldNameAnimal.setVisible(true);
            textFieldBreedAnimal.setVisible(true);
            textFieldAgeAnimal.setVisible(true);
            comboBoxSpeciesAnimal.setVisible(true);

            labelNameAnimal.setVisible(true);
            labelBreedAnimal.setVisible(true);
            labelSpeciesAnimal.setVisible(true);
            labelAgeAnimal.setVisible(true);

        });
        clientsButton.addActionListener(e -> {
            selectedModel = Models.Client;
            defaultVisibility();
            defaultText();

            textFieldNameUser.setVisible(true);
            textFieldMailUser.setVisible(true);
            textFieldLoginUser.setVisible(true);
            textFieldPasswordUser.setVisible(true);

            labelNameUser.setVisible(true);
            labelMailUser.setVisible(true);
            labelLoginUser.setVisible(true);
            labelPasswordUser.setVisible(true);
        });
        doctorsButton.addActionListener(e -> {
            selectedModel = Models.Doctor;
            defaultVisibility();
            defaultText();

            textFieldNameUser.setVisible(true);
            textFieldMailUser.setVisible(true);
            textFieldLoginUser.setVisible(true);
            textFieldPasswordUser.setVisible(true);
            textFieldSpecializationDoctor.setVisible(true);

            labelNameUser.setVisible(true);
            labelMailUser.setVisible(true);
            labelLoginUser.setVisible(true);
            labelPasswordUser.setVisible(true);
            labelSpecializationDoctor.setVisible(true);
        });
        curesButton.addActionListener(e -> {
            selectedModel = Models.Cure;
            defaultVisibility();
            defaultText();

            textFieldNameCure.setVisible(true);
            textFieldCategoryCure.setVisible(true);

            labelNameCure.setVisible(true);
            labelCategoryCure.setVisible(true);
        });
        visitsButton.addActionListener(e -> {
            selectedModel = Models.Visit;
            defaultVisibility();
            defaultText();

            textFieldYear.setVisible(true);
            textFieldMonth.setVisible(true);
            textFieldDay.setVisible(true);
            textFieldHour.setVisible(true);
            textFieldMinute.setVisible(true);
            textFieldCommentVisit.setVisible(true);
            comboBoxIsClientCame.setVisible(true);

            labelDate.setVisible(true);
            labelYear.setVisible(true);
            labelMonth.setVisible(true);
            labelDay.setVisible(true);
            labelHour.setVisible(true);
            labelMinute.setVisible(true);
            labelSeparator1.setVisible(true);
            labelSeparator2.setVisible(true);
            labelSeparator3.setVisible(true);
            labelSeparator4.setVisible(true);
            labelComment.setVisible(true);
            labelIsClientCame.setVisible(true);

//            labelDoctorVisit.setVisible(true);
//            comboBoxDoctorVisit.setVisible(true);



            if (ConstantUtils.authorizedUser.getRole() == UserRole.Administrator.ordinal()) {
                //Сделать поле для заполнения врача видимым
                labelDoctorVisit.setVisible(true);
                comboBoxDoctorVisit.setVisible(true);

            }

            else if (ConstantUtils.authorizedUser.getRole() == UserRole.Client.ordinal()) {
                //Сделать видимой кнопку "Записаться на прием"
                //Сделать поле для заполнения животного видимым
                labelAnimalVisit.setVisible(true);
                buttonAppointment.setVisible(true);
                comboBoxAnimalVisit.setVisible(true);
            }

        });

        createListeners(animalsButton);
        createListeners(curesButton);
        createListeners(clientsButton);
        createListeners(doctorsButton);
        createListeners(visitsButton);

        buttonAppointment.addActionListener(e-> Database.getInstance().insertOrUpdate(Visit.builder().id(ConstantUtils.selectedVisit.getId()).client_id(ConstantUtils.authorizedUser.getId()).animal_id(
                Database.getInstance().select(Animal.builder().client_id(ConstantUtils.authorizedUser.getId()).name(Objects.requireNonNull(comboBoxAnimalVisit.getSelectedItem()).toString()).build()).first().getId()
        ).build()));

        addButton.addActionListener(e -> {
            switch(selectedModel) {
                case Animal -> {
                    Database.getInstance().insertOrUpdate(Animal.builder()
                            .name(textFieldNameAnimal.getText())
                            .species(comboBoxSpeciesAnimal.getSelectedIndex())
                            .breed(textFieldBreedAnimal.getText())
                            .age(Integer.valueOf(textFieldAgeAnimal.getText()))
                            .client_id(ConstantUtils.authorizedUser.getId())
                            .build());

                    Integer clientId = null;
                    if (ConstantUtils.authorizedUser.getRole() == UserRole.Client.ordinal())
                        clientId = ConstantUtils.authorizedUser.getId();

                    updateTableUI(fillInAnimals(clientId));
                }
                case Cure -> Database.getInstance().insertOrUpdate(Cure.builder()
                        .name(textFieldNameCure.getText())
                        .category(textFieldCategoryCure.getText())
                        .build());
                case Client -> Database.getInstance().insertOrUpdate(Users.builder()
                        .name(textFieldNameUser.getText())
                        .login(textFieldLoginUser.getText())
                        .password(textFieldPasswordUser.getText())
                        .mail(textFieldMailUser.getText())
                        .role(UserRole.Client.ordinal())
                        .build());
                case Doctor -> Database.getInstance().insertOrUpdate(Users.builder()
                        .name(textFieldNameUser.getText())
                        .login(textFieldLoginUser.getText())
                        .password(textFieldPasswordUser.getText())
                        .mail(textFieldMailUser.getText())
                        .role(UserRole.Client.ordinal())
                        .specialization(textFieldSpecializationDoctor.getText())
                        .build());
                case Visit -> {
                    Timestamp date = new Timestamp(
                            Integer.parseInt(textFieldYear.getText()) - 1900,
                            Integer.parseInt(textFieldMonth.getText()) - 1,
                            Integer.parseInt(textFieldDay.getText()),
                            Integer.parseInt(textFieldHour.getText()),
                            Integer.parseInt(textFieldMinute.getText()),
                            0, 0
                    );

                    boolean isCame = comboBoxIsClientCame.getSelectedIndex() == 0;
                    Integer doctorId = null;


                    TreeSet<Users> doctors = Database.getInstance().select(Users.builder().role(UserRole.Doctor.ordinal()).build());
                    for (Users doctor : doctors) {
                        if (Objects.equals(Objects.requireNonNull(comboBoxDoctorVisit.getSelectedItem()).toString(), doctor.toString())) {
                            doctorId = doctor.getId();
                        }
                    }

                    Database.getInstance().insertOrUpdate(Visit.builder()
                            .date(date)
                            .recommendation(textFieldCommentVisit.getText())
                            .doctor_id(doctorId)
                            .build());
                }
                case Vaccination -> {

                }
            }
        });

        updateButton.addActionListener(e -> {
            switch(selectedModel) {
                case Animal -> Database.getInstance().insertOrUpdate(Animal.builder()
                        .id(ConstantUtils.selectedAnimal.getId())
                        .name(textFieldNameAnimal.getText())
                        .species(comboBoxSpeciesAnimal.getSelectedIndex())
                        .breed(textFieldBreedAnimal.getText())
                        .age(Integer.valueOf(textFieldAgeAnimal.getText()))
                        .build());
                case Cure -> Database.getInstance().insertOrUpdate(Cure.builder()
                        .id(ConstantUtils.selectedCure.getId())
                        .name(textFieldNameCure.getText())
                        .category(textFieldCategoryCure.getText())
                        .build());
                case Client -> Database.getInstance().insertOrUpdate(Users.builder()
                        .id(ConstantUtils.selectedUser.getId())
                        .name(textFieldNameUser.getText())
                        .login(textFieldLoginUser.getText())
                        .password(textFieldPasswordUser.getText())
                        .mail(textFieldMailUser.getText())
                        .role(UserRole.Client.ordinal())
                        .build());
                case Doctor -> Database.getInstance().insertOrUpdate(Users.builder()
                        .id(ConstantUtils.selectedUser.getId())
                        .name(textFieldNameUser.getText())
                        .login(textFieldLoginUser.getText())
                        .password(textFieldPasswordUser.getText())
                        .mail(textFieldMailUser.getText())
                        .role(UserRole.Doctor.ordinal())
                        .specialization(textFieldSpecializationDoctor.getText())
                        .build());
                case Visit -> {
                    Timestamp date = new Timestamp(
                            Integer.parseInt(textFieldYear.getText()) - 1900,
                            Integer.parseInt(textFieldMonth.getText()) - 1,
                            Integer.parseInt(textFieldDay.getText()),
                            Integer.parseInt(textFieldHour.getText()),
                            Integer.parseInt(textFieldMinute.getText()),
                            0, 0
                    );

                    boolean isCame = comboBoxIsClientCame.getSelectedIndex() == 0;

                    Database.getInstance().insertOrUpdate(Visit.builder()
                            .id(ConstantUtils.selectedVisit.getId())
                            .date(date)
                            .recommendation(textFieldCommentVisit.getText())
                            .animal_id(ConstantUtils.selectedVisit.getAnimal_id())
                            .client_id(ConstantUtils.selectedVisit.getClient_id())
                            .doctor_id(ConstantUtils.selectedVisit.getDoctor_id())
                            .build());
                }
                case Vaccination -> {
                    //ConstantUtils.selectedVaccination;
                }
            }
        });
        clearButton.addActionListener(e -> {
            defaultText();
            switch (selectedModel) {
                case Animal -> addButton.setVisible(ConstantUtils.authorizedUser.getRole() == UserRole.Client.ordinal());
                case Cure, Doctor, Client, Visit -> addButton.setVisible(ConstantUtils.authorizedUser.getRole() == UserRole.Administrator.ordinal());
                case Vaccination -> addButton.setVisible(true);
            }
        });

        deleteButton.addActionListener(e -> {
            switch(selectedModel) {
                case Animal -> Database.getInstance().delete(Animal.class, ConstantUtils.selectedAnimal.getId());
                case Cure -> Database.getInstance().delete(Cure.class, ConstantUtils.selectedCure.getId());
                case Client, Doctor -> Database.getInstance().delete(Users.class, ConstantUtils.selectedUser.getId());
                case Visit -> Database.getInstance().delete(Visit.class, ConstantUtils.selectedVisit.getId());
                case Vaccination -> Database.getInstance().delete(Vaccination.class, ConstantUtils.selectedVaccination.getId());
            }
        });

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

                switch (selectedModel) {
                    case Animal -> {
                        textFieldNameAnimal.setText(state.get(1));
                        comboBoxSpeciesAnimal.setSelectedItem(PossibleAnimalSpecies.valueOf(state.get(2)).name());
                        textFieldBreedAnimal.setText(state.get(3));
                        textFieldAgeAnimal.setText(state.get(4));
                        ConstantUtils.selectedAnimal = Database.getInstance().select(Animal.builder().id(Integer.parseInt(state.get(0))).build()).first();
                    }
                    case Client -> {
                        textFieldNameUser.setText(state.get(1));
                        textFieldLoginUser.setText(state.get(2));
                        textFieldPasswordUser.setText(state.get(3));
                        textFieldMailUser.setText(state.get(4));
                        ConstantUtils.selectedUser = Database.getInstance().select(Users.builder().id(Integer.parseInt(state.get(0))).build()).first();
                    }
                    case Doctor -> {
                        textFieldNameUser.setText(state.get(1));
                        textFieldLoginUser.setText(state.get(2));
                        textFieldPasswordUser.setText(state.get(3));
                        textFieldMailUser.setText(state.get(4));
                        textFieldSpecializationDoctor.setText(state.get(5));
                        ConstantUtils.selectedUser = Database.getInstance().select(Users.builder().id(Integer.parseInt(state.get(0))).build()).first();
                    }
                    case Cure -> {
                        textFieldNameCure.setText(state.get(1));
                        textFieldCategoryCure.setText(state.get(2));
                        ConstantUtils.selectedCure = Database.getInstance().select(Cure.builder().id(Integer.parseInt(state.get(0))).build()).first();
                    }
                    case Visit -> {
                        String date = state.get(1);
                        Timestamp timestamp = Timestamp.valueOf(date + ":00.0");

                        textFieldYear.setText(String.valueOf(timestamp.getYear() + 1900));
                        textFieldMonth.setText(String.valueOf(timestamp.getMonth() + 1));
                        textFieldDay.setText(String.valueOf(timestamp.getDate()));
                        textFieldHour.setText(String.valueOf(timestamp.getHours()));
                        textFieldMinute.setText(String.valueOf(timestamp.getMinutes()));

                        textFieldCommentVisit.setText(state.get(2));
                        comboBoxIsClientCame.setSelectedItem(state.get(3));

                        //comboBoxDoctorVisit.setSelectedItem(state.get());

                        ConstantUtils.selectedVisit = Database.getInstance().select(Visit.builder().id(Integer.parseInt(state.get(0))).build()).first();
                    }
                }

                if (selectedModel != null) {
                    switch (selectedModel) {
                        case Animal -> {
                            if (ConstantUtils.authorizedUser.getRole() == UserRole.Client.ordinal())
                                updateButton.setVisible(true);
                        }
                        case Cure, Doctor, Client, Visit -> {
                            if (ConstantUtils.authorizedUser.getRole() == UserRole.Administrator.ordinal())
                                updateButton.setVisible(true);
                        }
                        case Vaccination -> updateButton.setVisible(true);
                    }
                }

                addButton.setVisible(false);
            }
        });

        comboBoxSpeciesAnimal = new CustomComboBox(Stream.of(PossibleAnimalSpecies.values()).map(PossibleAnimalSpecies::name).toArray(String[]::new));
        comboBoxIsClientCame = new CustomComboBox(new String[] {"Пришел", "Не пришел"});
        comboBoxDoctorVisit = new CustomComboBox(Database.getInstance().select(Users.builder().role(UserRole.Doctor.ordinal()).build()).stream().map(Users::toString).toArray(String[]::new));
        comboBoxAnimalVisit = new CustomComboBox(Database.getInstance().select(Animal.builder().client_id(ConstantUtils.authorizedUser.getId()).build()).stream().map(Animal::getName).toArray(String[]::new));
    }

    private void defaultFieldsPaint() {
        textFieldNameUser.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldMailUser.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldLoginUser.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldPasswordUser.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldSpecializationDoctor.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldNameCure.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldCategoryCure.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldNameAnimal.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldBreedAnimal.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldAgeAnimal.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldYear.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldMonth.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldDay.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldHour.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldMinute.setBorder(new MatteBorder(0, 0, 2, 0, DesignUtils.MAIN_COLOR));
        textFieldCommentVisit.setBorder(new MatteBorder(0 ,0, 2, 0, DesignUtils.MAIN_COLOR));
    }

    private void defaultButtonsPaint() {
        animalsButton.setForeground(DesignUtils.MAIN_COLOR);
        curesButton.setForeground(DesignUtils.MAIN_COLOR);
        clientsButton.setForeground(DesignUtils.MAIN_COLOR);
        doctorsButton.setForeground(DesignUtils.MAIN_COLOR);
        visitsButton.setForeground(DesignUtils.MAIN_COLOR);
    }

    private void defaultText() {
        textFieldNameUser.setText("");
        textFieldMailUser.setText("");
        textFieldLoginUser.setText("");
        textFieldPasswordUser.setText("");
        textFieldSpecializationDoctor.setText("");

        textFieldNameCure.setText("");
        textFieldCategoryCure.setText("");

        textFieldNameAnimal.setText("");
        textFieldBreedAnimal.setText("");
        textFieldAgeAnimal.setText("");

        textFieldYear.setText("");
        textFieldMonth.setText("");
        textFieldDay.setText("");
        textFieldHour.setText("");
        textFieldMinute.setText("");
        textFieldCommentVisit.setText("");


        updateButton.setVisible(false);
        //addButton.setVisible(false);

        ConstantUtils.selectedAnimal = null;
        ConstantUtils.selectedUser = null;
        ConstantUtils.selectedCure = null;
        ConstantUtils.selectedVisit = null;
        ConstantUtils.selectedVaccination = null;

        table.clearSelection();
    }

    private void defaultVisibility() {
        textFieldNameUser.setVisible(false);
        textFieldMailUser.setVisible(false);
        textFieldLoginUser.setVisible(false);
        textFieldPasswordUser.setVisible(false);
        textFieldSpecializationDoctor.setVisible(false);

        textFieldNameCure.setVisible(false);
        textFieldCategoryCure.setVisible(false);

        textFieldNameAnimal.setVisible(false);
        textFieldBreedAnimal.setVisible(false);
        textFieldAgeAnimal.setVisible(false);
        comboBoxSpeciesAnimal.setVisible(false);

        textFieldYear.setVisible(false);
        textFieldMonth.setVisible(false);
        textFieldDay.setVisible(false);
        textFieldHour.setVisible(false);
        textFieldMinute.setVisible(false);
        textFieldCommentVisit.setVisible(false);
        comboBoxIsClientCame.setVisible(false);


        labelNameUser.setVisible(false);
        labelMailUser.setVisible(false);
        labelLoginUser.setVisible(false);
        labelPasswordUser.setVisible(false);
        labelSpecializationDoctor.setVisible(false);

        labelNameCure.setVisible(false);
        labelCategoryCure.setVisible(false);

        labelNameAnimal.setVisible(false);
        labelBreedAnimal.setVisible(false);
        labelSpeciesAnimal.setVisible(false);
        labelAgeAnimal.setVisible(false);

        labelDate.setVisible(false);
        labelYear.setVisible(false);
        labelMonth.setVisible(false);
        labelDay.setVisible(false);
        labelHour.setVisible(false);
        labelMinute.setVisible(false);
        labelSeparator1.setVisible(false);
        labelSeparator2.setVisible(false);
        labelSeparator3.setVisible(false);
        labelSeparator4.setVisible(false);
        labelComment.setVisible(false);
        labelIsClientCame.setVisible(false);

        labelDoctorVisit.setVisible(false);
        comboBoxDoctorVisit.setVisible(false);

        labelAnimalVisit.setVisible(false);
        comboBoxAnimalVisit.setVisible(false);
        buttonAppointment.setVisible(false);
    }

    private void createListeners(JButton button) {
        button.addActionListener(e -> {
            defaultButtonsPaint();
            button.setForeground(DesignUtils.SUB_MAIN_COLOR);
            cardPanel.setVisible(true);
            scrollPane.setVisible(true);


            clearButton.setVisible(true);
            deleteButton.setVisible(true);

        });
        button.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.getForeground() != DesignUtils.SUB_MAIN_COLOR)
                    button.setForeground(DesignUtils.SELECTION_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.getForeground() != DesignUtils.SUB_MAIN_COLOR)
                    button.setForeground(DesignUtils.MAIN_COLOR);
            }
        });
    }

    private DefaultTableModel fillInAnimals(Integer clientId) {
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, new String[]{"Id", "Кличка", "Вид", "Порода", "Возраст"}) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        if (clientId != null)
            for (Animal animal : Database.getInstance().select(Animal.builder().client_id(clientId).build()))
                model.addRow(new Object[]{animal.getId(), animal.getName(), PossibleAnimalSpecies.values()[animal.getSpecies()], animal.getBreed(), animal.getAge()});
        else
            for (Animal animal : Database.getInstance().select(Animal.builder().build()))
                model.addRow(new Object[]{animal.getId(), animal.getName(), PossibleAnimalSpecies.values()[animal.getSpecies()], animal.getBreed(), animal.getAge()});
        return model;
    }

    private DefaultTableModel fillInCures() {
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, new String[]{"Id", "Название", "Категория"}) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        for (Cure cure : Database.getInstance().select(Cure.builder().build()))
            model.addRow(new Object[]{cure.getId(), cure.getName(), cure.getCategory()});
        return model;
    }

    private DefaultTableModel fillInDoctors() {
        String[] header = new String[]{"Id", "Имя", "Логин", "Пароль", "Почта", "Специализация"};
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, header) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        for (Users user : Database.getInstance().select(Users.builder().role(UserRole.Doctor.ordinal()).build()))
            model.addRow(new Object[]{user.getId(), user.getName(), user.getLogin(), user.getPassword(), user.getMail(), user.getSpecialization()});
        return model;
    }

    private DefaultTableModel fillInClients() {
        String[] header = new String[]{"Id", "Имя", "Логин", "Пароль", "Почта"};
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, header) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        for (Users user : Database.getInstance().select(Users.builder().role(UserRole.Client.ordinal()).build()))
            model.addRow(new Object[]{user.getId(), user.getName(), user.getLogin(), user.getPassword(), user.getMail()});
        return model;
    }

    private DefaultTableModel fillInVisits() {

        String[] header;
        if (ConstantUtils.authorizedUser.getRole() == UserRole.Client.ordinal()) header = new String[]{"Id", "Дата", "Комментарий", "Врач"};
        else if (ConstantUtils.authorizedUser.getRole() == UserRole.Doctor.ordinal()) header = new String[]{"Id", "Дата", "Комментарий", "Посещение", "Клиент", "Животное"};
        else header = new String[]{"Id", "Дата", "Комментарий", "Посещение", "Врач", "Клиент", "Животное"};
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, header) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        if (ConstantUtils.authorizedUser.getRole() != UserRole.Administrator.ordinal()) {
            if (ConstantUtils.authorizedUser.getRole() == UserRole.Client.ordinal()) {

                defaultVisibility();
                addButton.setVisible(false);
                clearButton.setVisible(false);
                deleteButton.setVisible(false);

                labelAnimalVisit.setVisible(true);
                comboBoxAnimalVisit.setVisible(true);
                buttonAppointment.setVisible(true);

//                for (Visit visit : Database.getInstance().select(Visit.builder().client_id(ConstantUtils.authorizedUser.getId()).build())) {
//                    if (visit.getIsClientCame())
//                        model.addRow(new Object[]{visit.getId(),
//                                String.valueOf(visit.getDate()).substring(0, String.valueOf(visit.getDate()).length() - 5),
//                                visit.getComment(),
//                                "Пришел",
//                                Database.getInstance().select(Users.builder().id(visit.getDoctor_id()).build()).first().getName(),
//                                Database.getInstance().select(Animal.builder().id(visit.getAnimal_id()).build()).first().getName()
//                        });
//                    else
//                        model.addRow(new Object[]{visit.getId(),
//                                String.valueOf(visit.getDate()).substring(0, String.valueOf(visit.getDate()).length() - 5),
//                                visit.getComment(),
//                                "Не пришел",
//                                Database.getInstance().select(Users.builder().id(visit.getDoctor_id()).build()).first().getName(),
//                                Database.getInstance().select(Animal.builder().id(visit.getAnimal_id()).build()).first().getName()
//                        });
//                }
                for (Visit visit : Database.getInstance().select(Visit.builder().build())){
                    if (visit.getAnimal_id() == 0 && visit.getClient_id() == 0)
                        model.addRow(new Object[]{visit.getId(),
                                String.valueOf(visit.getDate()).substring(0, String.valueOf(visit.getDate()).length() - 5),
                                visit.getRecommendation(),
                                Database.getInstance().select(Users.builder().id(visit.getDoctor_id()).build()).first().getName(),
                        });
                }
            }

            else if (ConstantUtils.authorizedUser.getRole() == UserRole.Doctor.ordinal()) {
                String clientName;
                String animalName;

                for (Visit visit : Database.getInstance().select(Visit.builder().doctor_id(ConstantUtils.authorizedUser.getId()).build())) {
                    if (visit.getClient_id() == 0) {
                        clientName = "---";
                        animalName = "---";
                    }
                    else {
                        clientName = Database.getInstance().select(Users.builder().id(visit.getClient_id()).build()).first().getName();
                        animalName = Database.getInstance().select(Animal.builder().id(visit.getAnimal_id()).build()).first().getName();
                    }

//                    if (visit.getIsClientCame())
//                        model.addRow(new Object[]{visit.getId(), String.valueOf(visit.getDate()).substring(0, String.valueOf(visit.getDate()).length() - 5), visit.getRecommendation(), "Пришел", clientName, animalName});
//                    else
//                        model.addRow(new Object[]{visit.getId(), String.valueOf(visit.getDate()).substring(0, String.valueOf(visit.getDate()).length() - 5), visit.getRecommendation(), "Не пришел", clientName, animalName});
                }
            }
        } else {
            String clientName;
            String animalName;

            for (Visit visit : Database.getInstance().select(Visit.builder().build())) {
                if (visit.getClient_id() == 0) {
                    clientName = "---";
                    animalName = "---";
                }
                else {
                    clientName = Database.getInstance().select(Users.builder().id(visit.getClient_id()).build()).first().getName();
                    animalName = Database.getInstance().select(Animal.builder().id(visit.getAnimal_id()).build()).first().getName();
                }

//                if (visit.getIsClientCame())
//                    model.addRow(new Object[]{visit.getId(), String.valueOf(visit.getDate()).substring(0, String.valueOf(visit.getDate()).length() - 5), visit.getRecommendation(), "Пришел",
//                            Database.getInstance().select(Users.builder().id(visit.getDoctor_id()).build()).first().getName(), clientName, animalName});
//                else
//                    model.addRow(new Object[]{visit.getId(), String.valueOf(visit.getDate()).substring(0, String.valueOf(visit.getDate()).length() - 5), visit.getRecommendation(), "Не пришел",
//                            Database.getInstance().select(Users.builder().id(visit.getDoctor_id()).build()).first().getName(), clientName, animalName});
            }
        }
        return model;
    }

    private void updateTableUI(DefaultTableModel model) {
        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.removeColumn(table.getColumnModel().getColumn(0));
        table.setFont(DesignUtils.REGULAR_FONT);

        switch (selectedModel) {
            case Animal -> addButton.setVisible(ConstantUtils.authorizedUser.getRole() == UserRole.Client.ordinal());
            case Cure, Doctor, Client, Visit -> addButton.setVisible(ConstantUtils.authorizedUser.getRole() == UserRole.Administrator.ordinal());
            case Vaccination -> addButton.setVisible(true);
        }
    }
}