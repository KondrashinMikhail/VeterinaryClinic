package storage.DTOs;

import lombok.*;
import storage.models.Visit;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class VisitDTO {
    Integer id;
    Timestamp date;
    String recommendation;
    Integer animal_id;
    Integer client_id;
    Integer doctor_id;

    public VisitDTO(Visit v) {
        this.id = v.getId();
        this.date = v.getDate();
        this.recommendation = v.getRecommendation();
        this.animal_id = v.getAnimal_id();
        this.client_id = v.getClient_id();
        this.doctor_id = v.getDoctor_id();
    }

    public VisitDTO map(Visit entity) {
        VisitDTO dto = new VisitDTO();
        dto.id = entity.getId();
        dto.date = entity.getDate();
        dto.recommendation = entity.getRecommendation();
        dto.animal_id = entity.getAnimal_id();
        dto.client_id = entity.getClient_id();
        dto.doctor_id = entity.getDoctor_id();
        return dto;
    }
}
