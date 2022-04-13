package xingyu.lu.individual.svc.sofa.boot.facade;

import xingyu.lu.individual.svc.sofa.boot.facade.entity.Orders;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author xingyu.lu
 * @create 2020-12-23 10:25
 **/
@Path("pay")
@Consumes(MediaType.APPLICATION_JSON +";charset=UTF-8")
@Produces(MediaType.APPLICATION_JSON +";charset=UTF-8")
public interface PayService {

    @POST
    @Path("biz")
    Orders payBiz(Orders orders);
}
