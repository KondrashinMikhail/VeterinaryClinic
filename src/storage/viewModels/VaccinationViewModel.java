package storage.viewModels;

import storage.models.Vaccination;

import java.sql.Timestamp;

public class VaccinationViewModel {
    Integer id;
    Timestamp date;
    Integer animal_id;
    Integer cure_id;

    public VaccinationViewModel(Vaccination v) {
        this.id = v.getId();
        this.date = v.getDate();
        this.animal_id = v.getAnimal_id();
        this.cure_id = v.getCure_id();
    }
}
