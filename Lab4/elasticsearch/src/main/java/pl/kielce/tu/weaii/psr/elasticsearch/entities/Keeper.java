package pl.kielce.tu.weaii.psr.elasticsearch.entities;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class Keeper {
    private Long id;

    private String name;

    private String surname;

    private Integer age;

    private Double salary;

    private Set<Animal> animalTakenCareOf = new HashSet<>();
}
