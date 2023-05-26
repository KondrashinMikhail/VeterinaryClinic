package storage.models;

import lombok.*;
import storage.DTOs.VisitDTO;
import storage.DTOs.Visit_cureDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Visit_cure implements Comparable<Visit_cure> {
    Integer id;
    Integer cure_id;
    Integer visit_id;

    @Override
    public int compareTo(@NonNull Visit_cure visit) {
        return id.compareTo(visit.id);
    }

    public Visit_cure map(Visit_cureDTO dto) {
        Visit_cure entity = new Visit_cure();
        entity.id = dto.getId();
        entity.cure_id = dto.getCure_id();
        entity.visit_id = dto.getVisit_id();
        return entity;
    }
}
