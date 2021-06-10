package pl.kielce.tu.weaii.psr.elasticsearch.entities;

import lombok.Data;

@Data
public class Animal {

    private Long id;

    private String name;

    private String locationCode;
}
