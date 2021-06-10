package pl.kielce.tu.weaii.psr.neo4j.entities;

import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@Data
@NodeEntity(label = "Animal")
public class Animal {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String locationCode;
}
