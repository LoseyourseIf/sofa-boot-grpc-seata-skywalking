package xingyu.lu.individual.svc.sofa.boot.facade;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author xingyu.lu
 * @create 2021-04-22 15:49
 **/
@Slf4j
@Configuration
@MapperScan(value = {"xingyu.lu.individual.svc.sofa.boot.facade"})
public class MbpConfig {

}
