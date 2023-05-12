package storage.models;

import lombok.*;

@Value
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
}
