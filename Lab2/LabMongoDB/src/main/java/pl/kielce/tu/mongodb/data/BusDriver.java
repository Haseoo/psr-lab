package pl.kielce.tu.mongodb.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BusDriver {
    @JsonProperty("_id")
    private long id;
    private String name;
    private String surname;
    private int age;
    private double salary;
    private List<BusCourse> courses = new ArrayList<>();
}
