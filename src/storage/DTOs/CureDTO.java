package storage.DTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import storage.models.Cure;

@Getter
@NoArgsConstructor
public class CureDTO {
    Integer id;
    String name;
    String category;
    Double price;
    String manufacturer;

    public CureDTO(Cure c) {
        this.id = c.getId();
        this.name = c.getName();
        this.category = c.getCategory();
        this.price = c.getPrice();
        this.manufacturer = c.getManufacturer();
    }

    public CureDTO map(Cure entity) {
        CureDTO dto = new CureDTO();
        dto.id = entity.getId();
        dto.name = entity.getName();
        dto.category = entity.getCategory();
        dto.price = entity.getPrice();
        dto.manufacturer = entity.getManufacturer();
        return dto;
    }
}
