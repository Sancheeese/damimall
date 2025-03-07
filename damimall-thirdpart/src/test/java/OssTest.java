import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.example.damimall.thirdpart.ThirdpartApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest(classes = ThirdpartApplication.class)
public class OssTest {
    @Value("${alibaba.cloud.oss.endpoint}")
    private String endpoint;

    @Value("${alibaba.cloud.oss.bucket}")
    private String bucket;

    @Value("${alibaba.cloud.access-key}")
    private String accessId;

    @Value("${alibaba.cloud.secret-key}")
    private String accessKey;

    @Test
    public void test1() throws FileNotFoundException {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);

//        InputStream is = new FileInputStream("E:\\cartoon.png");
//        ossClient.putObject(bucket, "cartoon.png", is);
//        ossClient.shutdown();
//
//        System.out.println("完成");

        String policy = "{\"expiration\":\"2025-02-17T15:22:20.071Z\",\"conditions\":[[\"content-length-range\",0,10485760],[\"starts-with\",\"$key\",\"2025-02-17\"]]}";
        String s = ossClient.calculatePostSignature(policy);
        System.out.println(s);
    }
}
