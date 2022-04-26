package xingyu.lu.individual.svc.sofa.boot.provider.service.impl;

import io.seata.core.context.RootContext;
import org.slf4j.Logger;
import xingyu.lu.individual.svc.sofa.boot.facade.entity.Orders;
import xingyu.lu.individual.svc.sofa.boot.facade.entity.Product;
import xingyu.lu.individual.svc.sofa.boot.facade.mapper.ProductMapper;

import java.math.BigDecimal;

public class StockBaseService {
    static Orders orderStock(Orders orders, Logger log, ProductMapper productMapper) {
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
