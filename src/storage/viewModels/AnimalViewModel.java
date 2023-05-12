package storage.viewModels;

import storage.models.Animal;

public class AnimalViewModel {
    Integer id;
    String name;
    Integer species;
    String breed;
    Integer age;
    Integer client_id;

    public AnimalViewModel(Animal a) {
        this.id = a.getId();
        this.name = a.getName();
        this.species = a.getSpecies();
        this.breed = a.getBreed();
        this.age = a.getAge();
        this.client_id = a.getClient_id();
    }
}
