package storage.models;

import lombok.*;
import java.sql.Timestamp;

@Value
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
}
