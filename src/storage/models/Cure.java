package storage.models;

import lombok.*;
import storage.DTOs.CureDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Cure implements Comparable<Cure> {
    Integer id;
    String name;
    String category;
    Double price;
    String manufacturer;

    @Override
    public int compareTo(@NonNull Cure cure) {
        return id.compareTo(cure.id);
    }

    public Cure map(CureDTO dto) {
        Cure entity = new Cure();
        entity.id = dto.getId();
        entity.name = dto.getName();
        entity.category = dto.getCategory();
        entity.price = dto.getPrice();
        entity.manufacturer = dto.getManufacturer();
        return entity;
    }
}
