package storage.models;

import lombok.*;

@Value
@Builder(toBuilder = true)
public class Cure implements Comparable<Cure> {
    Integer id;
    String name;
    String category;

    @Override
    public int compareTo(@NonNull Cure cure) {
        return id.compareTo(cure.id);
    }
}
