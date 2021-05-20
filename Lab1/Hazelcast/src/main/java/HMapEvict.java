import java.net.UnknownHostException;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

public class HMapEvict {
    public static void main( String[] args ) throws UnknownHostException {
        ClientConfig clientConfig = HConfig.getClientConfig();
        HazelcastInstance client = HazelcastClient.newHazelcastClient( clientConfig );
        IMap<Long, Student> students = client.getMap( "students" );
        Student s = students.get(1457098882L);
        s.setName("xD");
        students.put(1457098882L, s);
        //students.evictAll();
    }
}