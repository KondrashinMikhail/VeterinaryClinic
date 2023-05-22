package storage.DTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import storage.models.Animal;

@Getter
@NoArgsConstructor
public class AnimalDTO {
    Integer id;
    String name;
    Integer species;
    String breed;
    Integer age;
    Integer client_id;

    public AnimalDTO(Animal a) {
        this.id = a.getId();
        this.name = a.getName();
        this.species = a.getSpecies();
        this.breed = a.getBreed();
        this.age = a.getAge();
        this.client_id = a.getClient_id();
    }

    public AnimalDTO map(Animal entity) {
        AnimalDTO dto = new AnimalDTO();
        dto.id = entity.getId();
        dto.name = entity.getName();
        dto.species = entity.getSpecies();
        dto.breed = entity.getBreed();
        dto.age = entity.getAge();
        dto.client_id = entity.getClient_id();
        return dto;
    }
}
