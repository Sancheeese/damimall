import com.example.damimall.gateway.GatewayApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;

@SpringBootTest(classes = GatewayApplication.class)
public class GTest {
    @Autowired
    private DiscoveryClient discoveryClient;

    @Test
    public void test(){
        System.out.println("hello");

        List<String> services = discoveryClient.getServices();
        for (String s : services){
            System.out.println(s);
        }
    }
}
