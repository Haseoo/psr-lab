package pl.kielce.tu.firebase.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BusRoute {
    private Integer id;
    private String from;
    private String to;
    private List<BusCourse> courses = new ArrayList<>();
}
