package xingyu.lu.individual.svc.sofa.boot.provider.service.impl;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xingyu.lu.individual.svc.sofa.boot.facade.StockService;
import xingyu.lu.individual.svc.sofa.boot.facade.entity.Orders;
import xingyu.lu.individual.svc.sofa.boot.facade.mapper.ProductMapper;

import javax.annotation.Resource;

/**
 * @author xingyu.lu
 * @create 2020-12-23 10:25
 **/

@Slf4j
@DS("stock")
@Service
@SofaService(uniqueId = "Stock",
        interfaceType = StockService.class,
        bindings = {@SofaServiceBinding(
                bindingType = "rest",
                filters = {"seataTxContextFilter"})})
public class StockSofaServiceRestImpl extends StockBaseService implements StockService {

    @Resource
    private ProductMapper productMapper;

    @Override
    public Orders stockBiz(Orders orders) {
        return orderStock(orders, log, productMapper);
    }
}
