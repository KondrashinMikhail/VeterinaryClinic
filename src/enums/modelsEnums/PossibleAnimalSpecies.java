package enums.modelsEnums;

public enum PossibleAnimalSpecies {
    Dog("Собака"),
    Cat("Кошка"),
    Bird("Птица"),
    Hamster("Хомяк"),
    Mouse("Мышь");

    public final String localizeString;

    private PossibleAnimalSpecies(String localizeString) {
        this.localizeString = localizeString;
    }
}
