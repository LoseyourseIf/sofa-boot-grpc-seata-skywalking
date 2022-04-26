package xingyu.lu.individual.svc.sofa.boot.provider.service.impl;

import io.seata.core.context.RootContext;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import xingyu.lu.individual.svc.sofa.boot.facade.entity.Account;
import xingyu.lu.individual.svc.sofa.boot.facade.entity.Orders;
import xingyu.lu.individual.svc.sofa.boot.facade.mapper.AccountMapper;

import java.math.BigDecimal;

public class PayBaseService {
    static Orders orderPay(Orders orders, Logger log, AccountMapper accountMapper) {
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
