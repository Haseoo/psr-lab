package pl.kielce.tu.weaii.psr.elasticsearch.entities.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.RestHighLevelClient;
import pl.kielce.tu.weaii.psr.elasticsearch.entities.Animal;
import pl.kielce.tu.weaii.psr.elasticsearch.entities.Keeper;

public class KeeperService extends BaseService<Keeper> {
    public KeeperService(RestHighLevelClient client, ObjectMapper objectMapper) {
        super(client, objectMapper);
    }

    @Override
    Class<Keeper> getEntityType() {
        return Keeper.class;
    }
}
