package xingyu.lu.individual.svc.sofa.boot.consumer.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import io.seata.core.context.RootContext;
import io.seata.core.exception.TransactionException;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xingyu.lu.individual.svc.sofa.boot.facade.PayService;
import xingyu.lu.individual.svc.sofa.boot.facade.StockService;
import xingyu.lu.individual.svc.sofa.boot.facade.entity.Orders;
import xingyu.lu.individual.svc.sofa.boot.facade.mapper.OrdersMapper;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Slf4j
@Service
@DS("order")
public class BizServiceImpl implements BizService {

    @Resource
    private OrdersMapper ordersMapper;

    @GlobalTransactional
    @Transactional
    @Override
    public Orders createOrderBiz(PayService payService, StockService stockService) throws TransactionException {
        log.info("当前 XID: {}", RootContext.getXID());
        /* Product 单价 5 库存 10 */
        /* Account 余额 1 */
        /* Order 付 5 买 1 个 */

        Orders orders = Orders.builder().userId(1).productId(1).payAmount(new BigDecimal(5))
                .status("初始化订单").build();
        int saveOrderRecord = ordersMapper.insert(orders);

        log.info("保存订单{}", saveOrderRecord > 0 ? "成功" : "失败");

        Orders operationStockResult = stockService.stockBiz(orders);
        log.info("扣减库存 {} ", operationStockResult.toString());

        Orders operationAccountResult = payService.payBiz(orders);
        log.info("扣减余额 {} ", operationAccountResult.toString());

        orders.setStatus("下单成功");
        int updateOrderRecord = ordersMapper.updateById(orders);
        log.info("更新订单:{} {}", orders.getId(), updateOrderRecord > 0 ? "成功" : "失败");

        return orders;
    }
}
