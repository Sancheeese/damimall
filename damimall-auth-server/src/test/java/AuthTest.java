import com.example.common.utils.R;
import com.example.damimall.auth.AuthApplication;
import com.example.damimall.auth.feign.MemberFeign;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = AuthApplication.class)
public class AuthTest {
    @Autowired
    MemberFeign memberFeign;

    @Test
    public void test(){
        R r = memberFeign.hello();
        r.getCode();
    }
}
