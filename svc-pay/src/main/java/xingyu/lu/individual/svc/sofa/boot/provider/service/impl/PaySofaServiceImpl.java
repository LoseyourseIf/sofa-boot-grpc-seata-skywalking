package xingyu.lu.individual.svc.sofa.boot.provider.service.impl;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.baomidou.dynamic.datasource.annotation.DS;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xingyu.lu.individual.svc.sofa.boot.facade.PayService;
import xingyu.lu.individual.svc.sofa.boot.facade.entity.Account;
import xingyu.lu.individual.svc.sofa.boot.facade.entity.Orders;
import xingyu.lu.individual.svc.sofa.boot.facade.mapper.AccountMapper;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author xingyu.lu
 * @create 2020-12-23 10:25
 **/
@Slf4j
@Service
@DS("pay")
@SofaService(uniqueId = "Pay",
        interfaceType = PayService.class,
        bindings = {@SofaServiceBinding(bindingType = "bolt")})
public class PaySofaServiceImpl implements PayService {

    @Resource
    private AccountMapper accountMapper;

    @Override
    public Orders payBiz(Orders orders) {
        try {
            log.info("当前 XID: {}", RootContext.getXID());
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
        return orders;
    }
}
