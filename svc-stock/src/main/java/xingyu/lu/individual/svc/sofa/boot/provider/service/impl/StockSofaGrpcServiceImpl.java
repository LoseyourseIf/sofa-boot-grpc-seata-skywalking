package xingyu.lu.individual.svc.sofa.boot.provider.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.sofa.rpc.common.RpcConstants;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.baomidou.dynamic.datasource.annotation.DS;
import io.grpc.stub.StreamObserver;
import io.seata.core.context.RootContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import xingyu.lu.individual.svc.sofa.boot.facade.builder.GrpcXReply;
import xingyu.lu.individual.svc.sofa.boot.facade.builder.GrpcXRequest;
import xingyu.lu.individual.svc.sofa.boot.facade.builder.SofaXServiceTriple;
import xingyu.lu.individual.svc.sofa.boot.facade.entity.Orders;
import xingyu.lu.individual.svc.sofa.boot.facade.entity.Product;
import xingyu.lu.individual.svc.sofa.boot.facade.mapper.ProductMapper;
import xingyu.lu.individual.svc.sofa.boot.provider.service.SofaGrpcService;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author xingyu.lu
 * @create 2020-12-23 10:25
 **/

@Slf4j
@DS("stock")
@Service
@SofaService(uniqueId = "Grpc-Stock", interfaceType = SofaXServiceTriple.IXService.class,
        bindings = {@SofaServiceBinding(
                bindingType = RpcConstants.PROTOCOL_TYPE_TRIPLE,
                serializeType = RpcConstants.SERIALIZE_PROTOBUF)})
public class StockSofaGrpcServiceImpl extends SofaXServiceTriple.XServiceImplBase implements SofaGrpcService {

    @Override
    public void grpcXCall(GrpcXRequest request, StreamObserver<GrpcXReply> responseObserver) {
        String data = bizService(request.getBizContent());
        GrpcXReply reply = GrpcXReply.newBuilder()
                .setBizData(data)
                .build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Resource
    private ProductMapper productMapper;

    @Override
    public String bizService(String param) {
        log.info("当前 XID: {}", RootContext.getXID());
        Orders orders = null;
        try {
            orders = JSON.parseObject(param, Orders.class);

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
        return JSON.toJSONString(orders);
    }

}
