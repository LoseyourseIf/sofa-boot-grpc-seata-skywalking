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
import xingyu.lu.individual.svc.sofa.boot.facade.entity.Account;
import xingyu.lu.individual.svc.sofa.boot.facade.entity.Orders;
import xingyu.lu.individual.svc.sofa.boot.facade.mapper.AccountMapper;
import xingyu.lu.individual.svc.sofa.boot.provider.service.SofaGrpcService;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author xingyu.lu
 * @create 2020-12-23 10:25
 **/
@Service
@Slf4j
@SofaService(uniqueId = "Grpc-Pay", interfaceType = SofaXServiceTriple.IXService.class,
        bindings = {@SofaServiceBinding(
                bindingType = RpcConstants.PROTOCOL_TYPE_TRIPLE,
                serializeType = RpcConstants.SERIALIZE_PROTOBUF)})
public class PaySofaGrpcServiceImpl extends SofaXServiceTriple.XServiceImplBase implements SofaGrpcService {

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
    private AccountMapper accountMapper;

    @Override
    @DS("pay")
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public String bizService(String param) {
        Orders orders = null;
        try {
            log.info("当前 XID: {}", RootContext.getXID());
            orders = JSON.parseObject(param, Orders.class);
            Integer userId = orders.getUserId();
            BigDecimal payAmount = orders.getPayAmount();

            Account account = accountMapper.selectById(userId);


            if (account.getBalance().compareTo(payAmount) < 0) {
                log.warn("用户 {} 余额不足，当前余额:{}", userId, account.getBalance());
                orders.setStatus("余额不足！");
                throw new Exception("余额不足");
            }

            account.setBalance(account.getBalance().subtract(payAmount));
            accountMapper.updateById(account);

            orders.setStatus("扣减余额成功！");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return JSON.toJSONString(orders);
    }
}
