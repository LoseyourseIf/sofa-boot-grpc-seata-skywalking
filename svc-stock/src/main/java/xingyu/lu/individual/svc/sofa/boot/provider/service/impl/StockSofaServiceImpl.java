package xingyu.lu.individual.svc.sofa.boot.provider.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.baomidou.dynamic.datasource.annotation.DS;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xingyu.lu.individual.svc.sofa.boot.facade.StockService;
import xingyu.lu.individual.svc.sofa.boot.facade.entity.Orders;
import xingyu.lu.individual.svc.sofa.boot.facade.entity.Product;
import xingyu.lu.individual.svc.sofa.boot.facade.mapper.ProductMapper;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author xingyu.lu
 * @create 2020-12-23 10:25
 **/

@Slf4j
@DS("stock")
@Service
@SofaService(uniqueId = "Stock",
        interfaceType = StockService.class,
        bindings = {@SofaServiceBinding(bindingType = "bolt")})
public class StockSofaServiceImpl implements StockService {

    @Resource
    private ProductMapper productMapper;

    @Override
    public Orders bizService(Orders orders) {
        log.info("当前 XID: {}", RootContext.getXID());
        try {
            Integer productId = orders.getProductId();
            BigDecimal payAmount = orders.getPayAmount();

            Product p = productMapper.selectById(productId);

            BigDecimal count = payAmount.divide(p.getPrice(), BigDecimal.ROUND_HALF_DOWN);

            /*判断库存*/
            if (p.getStock() < count.intValue()) {
                log.warn("{} 库存不足，当前库存:{}", productId, p.getStock());
                orders.setStatus("库存不足");
                throw new Exception("库存不足");
            }

            /*扣库存*/
            p.setStock(p.getStock() - count.intValue());
            productMapper.updateById(p);

            orders.setStatus("扣减库存成功！");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return orders;
    }

}
