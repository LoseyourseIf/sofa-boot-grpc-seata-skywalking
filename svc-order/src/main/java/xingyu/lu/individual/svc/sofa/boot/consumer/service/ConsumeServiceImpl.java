package xingyu.lu.individual.svc.sofa.boot.consumer.service;

import com.alibaba.fastjson.JSON;
import com.alipay.sofa.rpc.common.RpcConstants;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.baomidou.dynamic.datasource.annotation.DS;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xingyu.lu.individual.svc.sofa.boot.facade.builder.GrpcXReply;
import xingyu.lu.individual.svc.sofa.boot.facade.builder.GrpcXRequest;
import xingyu.lu.individual.svc.sofa.boot.facade.builder.SofaXServiceTriple;
import xingyu.lu.individual.svc.sofa.boot.facade.entity.Orders;
import xingyu.lu.individual.svc.sofa.boot.facade.mapper.OrdersMapper;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author xingyu.lu
 * @create 2020-12-29 10:46
 **/
@Slf4j
@Service
@DS("order")
@SofaService(uniqueId = "Rest-Order", interfaceType = ConsumeService.class, bindings =
@SofaServiceBinding(bindingType = "rest", timeout = 50000))
public class ConsumeServiceImpl implements ConsumeService {

    @SofaReference(
            uniqueId = "Grpc-Pay",
            interfaceType = SofaXServiceTriple.IXService.class,
            binding = @SofaReferenceBinding(
                    bindingType = RpcConstants.PROTOCOL_TYPE_TRIPLE,
                    serializeType = RpcConstants.SERIALIZE_PROTOBUF))
    private SofaXServiceTriple.IXService payService;

    @SofaReference(
            uniqueId = "Grpc-Stock",
            interfaceType = SofaXServiceTriple.IXService.class,
            binding = @SofaReferenceBinding(
                    bindingType = RpcConstants.PROTOCOL_TYPE_TRIPLE,
                    serializeType = RpcConstants.SERIALIZE_PROTOBUF))
    private SofaXServiceTriple.IXService stockService;

    @Resource
    private OrdersMapper ordersMapper;

    @GlobalTransactional
    @Transactional
    public Orders createOrder() {
        log.info("当前 XID: {}", RootContext.getXID());
        /* Product 单价 5 库存 10 */
        /* Account 余额 1 */
        /* Order 付 5 买 1 个 */

        Orders orders = Orders.builder().userId(1).productId(1).payAmount(new BigDecimal(5))
                .status("初始化订单").build();
        int saveOrderRecord = ordersMapper.insert(orders);

        log.info("保存订单{}", saveOrderRecord > 0 ? "成功" : "失败");

        String operationStockResult = callStockService(orders);
        log.info("扣减库存 {} ", operationStockResult);

        String operationAccountResult = callAccountService(orders);
        log.info("扣减余额 {} ", operationAccountResult);

        orders.setStatus("下单成功");
        int updateOrderRecord = ordersMapper.updateById(orders);
        log.info("更新订单:{} {}", orders.getId(), updateOrderRecord > 0 ? "成功" : "失败");

        return orders;
    }

    /*调用库存服务*/
    public String callStockService(Orders orders) {
        try {
            GrpcXRequest request = GrpcXRequest.newBuilder()
                    .setAppId("ORDER")
                    .setBizContent(JSON.toJSONString(orders))
                    .build();
            GrpcXReply reply = stockService.grpcXCall(request);
            return reply.getBizData();
        } catch (Exception e) {
            orders.setStatus("调用库存服务失效！");
            throw new RuntimeException("调用库存服务失效!");
        }
    }

    /*调用账户付款服务*/
    public String callAccountService(Orders orders) {
        try {
            GrpcXRequest request = GrpcXRequest.newBuilder()
                    .setAppId("ORDER")
                    .setBizContent(JSON.toJSONString(orders))
                    .build();
            GrpcXReply reply = payService.grpcXCall(request);
            return reply.getBizData();
        } catch (Exception e) {
            orders.setStatus("调用账户服务失效！");
            throw new RuntimeException("调用账户服务失效!");
        }
    }
}
