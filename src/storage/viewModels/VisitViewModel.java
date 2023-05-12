package storage.viewModels;

import storage.models.Visit;

import java.sql.Timestamp;

public class VisitViewModel {
    Integer id;
    Timestamp date;
    String comment;
    Boolean isClientCame;
    Integer animal_id;
    Integer cure_id;
    Integer client_id;
    Integer doctor_id;

    public VisitViewModel(Visit v) {
        this.id = v.getId();
        this.date = v.getDate();
        this.comment = v.getComment();
        this.isClientCame = v.getIsClientCame();
        this.animal_id = v.getAnimal_id();
        this.cure_id = v.getCure_id();
        this.client_id = v.getClient_id();
        this.doctor_id = v.getDoctor_id();
    }
}
