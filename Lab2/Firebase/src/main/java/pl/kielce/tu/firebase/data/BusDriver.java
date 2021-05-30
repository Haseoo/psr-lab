package pl.kielce.tu.firebase.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BusDriver {
    private Integer id;
    private String name;
    private String surname;
    private int age;
    private double salary;
    private List<BusCourse> courses = new ArrayList<>();
}
