package xingyu.lu.individual.svc.sofa.boot.filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath*:sofa/sofa-rpc-seata-filter.xml")
public class SofaRpcSeataFilterConfig {
}
