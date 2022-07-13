package xingyu.lu.individual.svc.sofa.boot.consumer.service;

import com.alipay.sofa.rpc.core.exception.SofaRpcException;
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
    public Orders createOrderBiz(PayService payService, StockService stockService) {
        log.info("当前 XID: {}", RootContext.getXID());

        /* Product 单价 5 库存 10 */
        /* Account 余额 1 */
        /* Order 付 5 买 1 个 */

        Orders orders = Orders.builder().userId(1).productId(1).payAmount(new BigDecimal(5))
                .status("初始化订单").build();
        int saveOrderRecord = ordersMapper.insert(orders);

        log.info("保存订单{}", saveOrderRecord > 0 ? "成功" : "失败");

        try {
            Orders operationStockResult = stockService.stockBiz(orders);
            log.info("扣减库存 {} ", operationStockResult.toString());
        } catch (SofaRpcException e) {
            log.error(e.getMessage());
            orders.setStatus("扣减库存失败！");
            throw new RuntimeException("扣减库存失败！");
        }

        try {
            Orders operationAccountResult = payService.payBiz(orders);
            log.info("扣减余额 {} ", operationAccountResult.toString());
        } catch (SofaRpcException e) {
            log.error(e.getMessage());
            orders.setStatus("扣减余额失败！");
            throw new RuntimeException("扣减余额失败！");
        }

        try {
            orders.setStatus("下单成功");
            int updateOrderRecord = ordersMapper.updateById(orders);
            log.info("保存订单:{} {}", orders.getId(), updateOrderRecord > 0 ? "成功" : "失败");
        } catch (SofaRpcException e) {
            log.error(e.getMessage());
            orders.setStatus("保存订单失败！");
            throw new RuntimeException("保存订单失败！");
        }

        return orders;
    }
}
