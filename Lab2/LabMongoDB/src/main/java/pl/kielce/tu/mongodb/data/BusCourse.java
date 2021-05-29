package pl.kielce.tu.mongodb.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BusCourse {
    @JsonProperty("_id")
    private long id;
    private String departureTime;
}
