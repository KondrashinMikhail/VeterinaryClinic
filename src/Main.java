import form.*;
import lombok.SneakyThrows;

import javax.swing.*;

public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

        new LoginForm(null);
        //new RegistrationForm(null);

        //new ClientsForm(null);
        //new DoctorsForClientsForm(null);
        //new DoctorsForAdminForm(null);
        //new CuresForUsersForm(null);
        //new CuresForAdminForm(null);
        //new AnimalsForUsers(null);
        //new AnimalsForClient(null);
        //new VaccinationForm(null, Mapper.mapToDTO(Database.getInstance().select(Animal.builder().id(1).build()), AnimalDTO.class, Animal.class).get(0), false);
        //new HistoryVisitsForm(null);
        //new FreeVisitsForm(null);
        //new OccupiedVisitsForm(null);
        //new TimetableCreatingForm(null);
        //new ReportVisitsForm(null);
    }
}