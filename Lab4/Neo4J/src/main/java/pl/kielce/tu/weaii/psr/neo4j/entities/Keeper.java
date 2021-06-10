package pl.kielce.tu.weaii.psr.neo4j.entities;

import lombok.Data;
import org.neo4j.ogm.annotation.*;

import java.util.HashSet;
import java.util.Set;

@Data
@NodeEntity(label = "AnimalKeeper")
public class Keeper {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String surname;

    private Integer age;

    private Double salary;

    @Relationship(type = "ANIMAL_KEEPER")
    private Set<Animal> animalTakenCareOf = new HashSet<>();
}
