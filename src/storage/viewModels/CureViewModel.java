package storage.viewModels;

import storage.models.Cure;

public class CureViewModel {
    Integer id;
    String name;
    String category;

    public CureViewModel(Cure c) {
        this.id = c.getId();
        this.name = c.getName();
        this.category = c.getCategory();
    }
}
