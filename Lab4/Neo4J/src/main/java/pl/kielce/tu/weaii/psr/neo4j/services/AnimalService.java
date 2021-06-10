package pl.kielce.tu.weaii.psr.neo4j.services;

import org.neo4j.ogm.session.Session;
import pl.kielce.tu.weaii.psr.neo4j.entities.Animal;

public class AnimalService extends GenericService<Animal> {

    public AnimalService(Session session) {
        super(session);
    }

    @Override
    Class<Animal> getEntityType() {
        return Animal.class;
    }
}
