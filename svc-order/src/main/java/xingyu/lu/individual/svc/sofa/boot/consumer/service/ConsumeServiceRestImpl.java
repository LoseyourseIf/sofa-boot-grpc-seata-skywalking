package xingyu.lu.individual.svc.sofa.boot.consumer.service;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.baomidou.dynamic.datasource.annotation.DS;
import io.seata.core.exception.TransactionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xingyu.lu.individual.svc.sofa.boot.facade.PayService;
import xingyu.lu.individual.svc.sofa.boot.facade.StockService;
import xingyu.lu.individual.svc.sofa.boot.facade.entity.Orders;

import javax.annotation.Resource;

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
    @SofaReference(uniqueId = "Pay-Rest", jvmFirst = false,
            interfaceType = PayService.class,
            binding = @SofaReferenceBinding(bindingType = "rest",
                    filters = {"seataTxContextFilter"}))
    private PayService restPayService;

    @SofaReference(uniqueId = "Stock-Rest", jvmFirst = false,
            interfaceType = StockService.class,
            binding = @SofaReferenceBinding(bindingType = "rest",
                    filters = {"seataTxContextFilter"}))
    private StockService restStockService;

    /*BOLT*/
    @SofaReference(uniqueId = "Pay-Bolt", jvmFirst = false,
            interfaceType = PayService.class,
            binding = @SofaReferenceBinding(bindingType = "bolt",
                    filters = {"seataTxContextFilter"}))
    private PayService boltPayService;

    @SofaReference(uniqueId = "Stock-Bolt", jvmFirst = false,
            interfaceType = StockService.class,
            binding = @SofaReferenceBinding(bindingType = "bolt",
                    filters = {"seataTxContextFilter"}))
    private StockService boltStockService;

    @Resource
    private BizService bizService;

    @Override
    public Orders createOrder() throws TransactionException {
        return bizService.createOrderBiz(boltPayService, boltStockService);
    }
}
