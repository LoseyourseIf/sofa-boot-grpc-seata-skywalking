package xingyu.lu.individual.svc.sofa.boot.consumer.service;

import io.seata.core.exception.TransactionException;
import xingyu.lu.individual.svc.sofa.boot.facade.entity.Orders;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author xingyu.lu
 * @create 2020-12-29 10:46
 **/
@Path("order")
@Consumes(MediaType.APPLICATION_JSON +";charset=UTF-8")
@Produces(MediaType.APPLICATION_JSON +";charset=UTF-8")
public interface ConsumeService {

    @GET
    @Path("create")
    public Orders createOrder() throws TransactionException;

}
