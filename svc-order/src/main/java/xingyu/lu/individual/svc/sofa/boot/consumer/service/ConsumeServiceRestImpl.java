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
import xingyu.lu.individual.svc.sofa.boot.facade.PayService;
import xingyu.lu.individual.svc.sofa.boot.facade.StockService;
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
public class ConsumeServiceRestImpl implements ConsumeService {

    /*REST*/
    @SofaReference(uniqueId = "Pay", jvmFirst = false,
            interfaceType = PayService.class,
            binding = @SofaReferenceBinding(bindingType = "rest",
                    filters = {"seataTxContextFilter"}))
    private PayService restPayService;

    @SofaReference(uniqueId = "Stock", jvmFirst = false,
            interfaceType = StockService.class,
            binding = @SofaReferenceBinding(bindingType = "rest",
                    filters = {"seataTxContextFilter"}))
    private StockService restStockService;

    /*BOLT*/
    @SofaReference(uniqueId = "Pay", jvmFirst = false,
            interfaceType = PayService.class,
            binding = @SofaReferenceBinding(bindingType = "bolt",
                    filters = {"seataTxContextFilter"}))
    private PayService boltPayService;

    @SofaReference(uniqueId = "Stock", jvmFirst = false,
            interfaceType = StockService.class,
            binding = @SofaReferenceBinding(bindingType = "bolt",
                    filters = {"seataTxContextFilter"}))
    private StockService boltStockService;

    /*H2C*/
    @SofaReference(uniqueId = "Pay", jvmFirst = false,
            interfaceType = PayService.class,
            binding = @SofaReferenceBinding(bindingType = "h2c",
                    filters = {"seataTxContextFilter"}))
    private PayService h2cPayService;

    @SofaReference(uniqueId = "Stock", jvmFirst = false,
            interfaceType = StockService.class,
            binding = @SofaReferenceBinding(bindingType = "h2c",
                    filters = {"seataTxContextFilter"}))
    private StockService h2cStockService;

    /*GRPC*/
    @SofaReference(uniqueId = "Pay-Grpc", jvmFirst = false,
            interfaceType = SofaXServiceTriple.IXService.class,
            binding = @SofaReferenceBinding(
                    bindingType = RpcConstants.PROTOCOL_TYPE_TRIPLE,
                    serializeType = RpcConstants.SERIALIZE_PROTOBUF,
                    filters = {"seataTxContextFilter"}))
    private SofaXServiceTriple.IXService grpcPayService;

    @SofaReference(uniqueId = "Stock-Grpc", jvmFirst = false,
            interfaceType = SofaXServiceTriple.IXService.class,
            binding = @SofaReferenceBinding(
                    bindingType = RpcConstants.PROTOCOL_TYPE_TRIPLE,
                    serializeType = RpcConstants.SERIALIZE_PROTOBUF,
                    filters = {"seataTxContextFilter"}))
    private SofaXServiceTriple.IXService grpcStockService;


    @Resource
    private OrdersMapper ordersMapper;

    @Resource
    private BizService bizService;


//    @Override
//    public Orders createOrder() throws TransactionException {
//        return bizService.createOrderBiz(h2cPayService, h2cStockService);
//    }

    /**
     * Product 单价 5 库存 10
     * Account 余额 1
     * Order 付 5 买 1 个
     */
    @GlobalTransactional
    @Transactional
    @Override
    public Orders createOrder() {

        log.info("当前 XID: {}", RootContext.getXID());

        Orders orders = Orders.builder().userId(1).productId(1).payAmount(new BigDecimal(5))
                .status("初始化订单").build();
        int saveOrderRecord = ordersMapper.insert(orders);

        log.info("保存订单{}", saveOrderRecord > 0 ? "成功" : "失败");

        GrpcXRequest stockRequest = GrpcXRequest.newBuilder().setAppId("sofa")
                .setBizContent(JSON.toJSONString(orders))
                .build();
        Orders operationStockResult =
                JSON.parseObject(grpcStockService.grpcXCall(stockRequest).getBizData(), Orders.class);

        log.info("扣减库存 {} ", operationStockResult.toString());

        GrpcXRequest payRequest = GrpcXRequest.newBuilder().setAppId("sofa")
                .setBizContent(JSON.toJSONString(orders))
                .build();
        Orders operationAccountResult =
                JSON.parseObject(grpcPayService.grpcXCall(payRequest).getBizData(), Orders.class);

        log.info("扣减余额 {} ", operationAccountResult.toString());

        orders.setStatus("下单成功");
        int updateOrderRecord = ordersMapper.updateById(orders);
        log.info("更新订单:{} {}", orders.getId(), updateOrderRecord > 0 ? "成功" : "失败");

        return orders;
    }
}
