package pl.kielce.tu.weaii.psr.elasticsearch.entities.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.RestHighLevelClient;
import pl.kielce.tu.weaii.psr.elasticsearch.entities.Animal;

public class AnimalService extends BaseService<Animal> {
    public AnimalService(RestHighLevelClient client, ObjectMapper objectMapper) {
        super(client, objectMapper);
    }

    @Override
    Class<Animal> getEntityType() {
        return Animal.class;
    }
}
