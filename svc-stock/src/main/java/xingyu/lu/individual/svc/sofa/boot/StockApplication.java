package xingyu.lu.individual.svc.sofa.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * @author xingyu.lu
 **/
@SpringBootApplication
@ImportResource("classpath*:sofa/sofa-rpc-seata-provider.xml")
public class StockApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockApplication.class, args);
    }
}
