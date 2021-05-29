package pl.kielce.tu.mongodb.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BusRoute {
    @JsonProperty("_id")
    private long id;
    private String from;
    private String to;
    private List<BusCourse> courses = new ArrayList<>();
}
