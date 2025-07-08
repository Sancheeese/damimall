import com.example.damimall.order.OrderApplication;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = OrderApplication.class)
public class OrderTest {
    @Autowired
    AmqpAdmin amqpAdmin;

    @Test
    public void test(){
        DirectExchange directExchange = new DirectExchange("test.direct");
        amqpAdmin.declareExchange(directExchange);
    }
}
