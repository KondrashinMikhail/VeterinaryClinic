package storage.models;

import lombok.*;
import storage.DTOs.VisitDTO;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Visit implements Comparable<Visit> {
    Integer id;
    Timestamp date;
    String recommendation;
    Integer animal_id;
    Integer client_id;
    Integer doctor_id;

    @Override
    public int compareTo(@NonNull Visit visit) {
        return id.compareTo(visit.id);
    }

    public Visit map(VisitDTO dto) {
        Visit entity = new Visit();
        entity.id = dto.getId();
        entity.date = dto.getDate();
        entity.recommendation = dto.getRecommendation();
        entity.animal_id = dto.getAnimal_id();
        entity.client_id = dto.getClient_id();
        entity.doctor_id = dto.getDoctor_id();
        return entity;
    }
}
