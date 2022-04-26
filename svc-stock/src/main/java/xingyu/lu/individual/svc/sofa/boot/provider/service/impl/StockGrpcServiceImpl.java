package xingyu.lu.individual.svc.sofa.boot.provider.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.sofa.rpc.common.RpcConstants;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.baomidou.dynamic.datasource.annotation.DS;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xingyu.lu.individual.svc.sofa.boot.facade.SofaGrpcService;
import xingyu.lu.individual.svc.sofa.boot.facade.builder.GrpcXReply;
import xingyu.lu.individual.svc.sofa.boot.facade.builder.GrpcXRequest;
import xingyu.lu.individual.svc.sofa.boot.facade.builder.SofaXServiceTriple;
import xingyu.lu.individual.svc.sofa.boot.facade.entity.Orders;
import xingyu.lu.individual.svc.sofa.boot.facade.mapper.ProductMapper;

import javax.annotation.Resource;

/**
 * @author xingyu.lu
 * @create 2020-12-23 10:25
 **/
@Slf4j
@Service
@DS("stock")
@SofaService(uniqueId = "Stock-Grpc", interfaceType = SofaXServiceTriple.IXService.class,
        bindings = {@SofaServiceBinding(
                bindingType = RpcConstants.PROTOCOL_TYPE_TRIPLE,
                serializeType = RpcConstants.SERIALIZE_PROTOBUF,
                filters = {"seataTxContextFilter"})})
public class StockGrpcServiceImpl extends SofaXServiceTriple.XServiceImplBase implements SofaGrpcService {

    @Resource
    private ProductMapper productMapper;

    @Override
    public void grpcXCall(GrpcXRequest request, StreamObserver<GrpcXReply> responseObserver) {

        log.info("Executing Thread Is = {}", Thread.currentThread().getName());

        String data = biz(request.getBizContent());

        GrpcXReply reply = GrpcXReply.newBuilder()
                .setBizData(data)
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public String biz(String param) {
        Orders orders = JSON.parseObject(param, Orders.class);
        StockBaseService.orderStock(orders, log, productMapper);
        return JSON.toJSONString(orders);
    }
}
