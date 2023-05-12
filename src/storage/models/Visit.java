package storage.models;

import lombok.*;
import java.sql.Timestamp;

@Value
@Builder(toBuilder = true)
public class Visit implements Comparable<Visit> {
    Integer id;
    Timestamp date;
    String comment;
    Boolean isClientCame;
    Integer animal_id;
    Integer cure_id;
    Integer client_id;
    Integer doctor_id;

    @Override
    public int compareTo(@NonNull Visit visit) {
        return id.compareTo(visit.id);
    }
}
