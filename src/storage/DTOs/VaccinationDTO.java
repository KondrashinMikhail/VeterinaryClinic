package storage.DTOs;

import lombok.*;
import storage.models.Vaccination;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class VaccinationDTO {
    Integer id;
    Timestamp date;
    Integer animal_id;
    Integer cure_id;

    public VaccinationDTO(Vaccination v) {
        this.id = v.getId();
        this.date = v.getDate();
        this.animal_id = v.getAnimal_id();
        this.cure_id = v.getCure_id();
    }

    public VaccinationDTO map(Vaccination entity) {
        VaccinationDTO dto = new VaccinationDTO();
        dto.id = entity.getId();
        dto.date = entity.getDate();
        dto.animal_id = entity.getAnimal_id();
        dto.cure_id = entity.getCure_id();
        return dto;
    }
}
