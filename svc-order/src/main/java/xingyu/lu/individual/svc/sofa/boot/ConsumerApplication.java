package xingyu.lu.individual.svc.sofa.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * @author xingyu.lu
 * @create 2020-12-23 13:26
 **/
@SpringBootApplication
@ImportResource("classpath*:sofa/sofa-rpc-seata-consumer.xml")
public class ConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}
