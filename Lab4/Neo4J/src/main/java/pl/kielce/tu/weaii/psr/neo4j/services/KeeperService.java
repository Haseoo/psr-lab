package pl.kielce.tu.weaii.psr.neo4j.services;

import org.neo4j.ogm.session.Session;
import pl.kielce.tu.weaii.psr.neo4j.entities.Keeper;

public class KeeperService extends GenericService<Keeper> {

    public KeeperService(Session session) {
        super(session);
    }

    @Override
    Class<Keeper> getEntityType() {
        return Keeper.class;
    }
}
