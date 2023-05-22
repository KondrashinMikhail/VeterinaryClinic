package storage.models;

import lombok.*;
import storage.DTOs.AnimalDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Animal implements Comparable<Animal> {
    Integer id;
    String name;
    Integer species;
    String breed;
    Integer age;
    Integer client_id;

    @Override
    public int compareTo(@NonNull Animal animal) {
        return id.compareTo(animal.id);
    }

    public Animal map(AnimalDTO dto) {
        Animal entity = new Animal();
        entity.id = dto.getId();
        entity.name = dto.getName();
        entity.species = dto.getSpecies();
        entity.breed = dto.getBreed();
        entity.age = dto.getAge();
        entity.client_id = dto.getClient_id();
        return entity;
    }
}
