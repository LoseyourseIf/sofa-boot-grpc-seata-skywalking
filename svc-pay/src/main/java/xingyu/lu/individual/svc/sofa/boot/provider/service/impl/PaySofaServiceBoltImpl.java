package xingyu.lu.individual.svc.sofa.boot.provider.service.impl;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xingyu.lu.individual.svc.sofa.boot.facade.PayService;
import xingyu.lu.individual.svc.sofa.boot.facade.entity.Orders;
import xingyu.lu.individual.svc.sofa.boot.facade.mapper.AccountMapper;

import javax.annotation.Resource;

/**
 * @author xingyu.lu
 * @create 2020-12-23 10:25
 **/
@Slf4j
@Service
@DS("pay")
@SofaService(uniqueId = "Pay-Bolt",
        interfaceType = PayService.class,
        bindings = {@SofaServiceBinding(
                bindingType = "bolt",
                filters = {"seataTxContextFilter"})})
public class PaySofaServiceBoltImpl extends PayBaseService implements PayService {

    @Resource
    private AccountMapper accountMapper;

    @Override
    public Orders payBiz(Orders orders) {
        return orderPay(orders, log, accountMapper);
    }
}
