package storage.models;

import lombok.*;
import storage.DTOs.VaccinationDTO;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Vaccination implements Comparable<Vaccination> {
    Integer id;
    Timestamp date;
    Integer animal_id;
    Integer cure_id;

    @Override
    public int compareTo(@NonNull Vaccination vaccination) {
        return id.compareTo(vaccination.id);
    }

    public Vaccination map(VaccinationDTO dto) {
        Vaccination entity = new Vaccination();
        entity.id = dto.getId();
        entity.date = dto.getDate();
        entity.animal_id = dto.getAnimal_id();
        entity.cure_id = dto.getCure_id();
        return entity;
    }
}
