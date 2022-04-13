package xingyu.lu.individual.svc.sofa.boot.facade;

import xingyu.lu.individual.svc.sofa.boot.facade.entity.Orders;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author xingyu.lu
 * @create 2020-12-23 10:25
 **/
@Path("stock")
@Consumes(MediaType.APPLICATION_JSON +";charset=UTF-8")
@Produces(MediaType.APPLICATION_JSON +";charset=UTF-8")
public interface StockService {

    @POST
    @Path("biz")
    Orders stockBiz(Orders orders);
}
