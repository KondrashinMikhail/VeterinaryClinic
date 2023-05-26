package storage.DTOs;

import lombok.*;
import storage.models.Visit;
import storage.models.Visit_cure;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Visit_cureDTO {
    Integer id;
    Integer cure_id;
    Integer visit_id;

    public Visit_cureDTO(Visit_cure v) {
        this.id = v.getId();
        this.cure_id = v.getCure_id();
        this.visit_id = v.getVisit_id();
    }

    public Visit_cureDTO map(Visit_cure entity) {
        Visit_cureDTO dto = new Visit_cureDTO();
        dto.id = entity.getId();
        dto.cure_id = entity.getCure_id();
        dto.visit_id = entity.getVisit_id();
        return dto;
    }
}
