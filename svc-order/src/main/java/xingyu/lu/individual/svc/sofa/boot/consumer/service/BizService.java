package xingyu.lu.individual.svc.sofa.boot.consumer.service;


import io.seata.core.exception.TransactionException;
import xingyu.lu.individual.svc.sofa.boot.facade.PayService;
import xingyu.lu.individual.svc.sofa.boot.facade.StockService;
import xingyu.lu.individual.svc.sofa.boot.facade.entity.Orders;

public interface BizService {
    Orders createOrderBiz(PayService payService, StockService stockService) throws TransactionException;
}
