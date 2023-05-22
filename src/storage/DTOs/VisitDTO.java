package storage.DTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import storage.models.Visit;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
public class VisitDTO {
    Integer id;
    Timestamp date;
    String comment;
    Boolean isClientCame;
    Integer animal_id;
    Integer cure_id;
    Integer client_id;
    Integer doctor_id;

    public VisitDTO(Visit v) {
        this.id = v.getId();
        this.date = v.getDate();
        this.comment = v.getComment();
        this.isClientCame = v.getIsClientCame();
        this.animal_id = v.getAnimal_id();
        this.cure_id = v.getCure_id();
        this.client_id = v.getClient_id();
        this.doctor_id = v.getDoctor_id();
    }

    public VisitDTO map(Visit entity) {
        VisitDTO dto = new VisitDTO();
        dto.id = entity.getId();
        dto.date = entity.getDate();
        dto.comment = entity.getComment();
        dto.isClientCame = entity.getIsClientCame();
        dto.animal_id = entity.getAnimal_id();
        dto.cure_id = entity.getCure_id();
        dto.client_id = entity.getClient_id();
        dto.doctor_id = entity.getDoctor_id();
        return dto;
    }
}
