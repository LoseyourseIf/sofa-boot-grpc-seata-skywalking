package xingyu.lu.individual.svc.sofa.boot.filter;

import com.alipay.sofa.rpc.context.RpcInvokeContext;
import com.alipay.sofa.rpc.core.exception.SofaRpcException;
import com.alipay.sofa.rpc.core.request.SofaRequest;
import com.alipay.sofa.rpc.core.response.SofaResponse;
import com.alipay.sofa.rpc.ext.Extension;
import com.alipay.sofa.rpc.filter.AutoActive;
import com.alipay.sofa.rpc.filter.Filter;
import com.alipay.sofa.rpc.filter.FilterInvoker;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Extension(value = "seataTxContextFilter")
@AutoActive(consumerSide = true,providerSide = true)
@Slf4j
public class SeataTxContextFilter extends Filter {

    /**
     * log for this class
     */

    @Override
    public boolean needToLoad(FilterInvoker invoker) {
        return true;
    }

    @Override
    public SofaResponse invoke(FilterInvoker filterInvoker, SofaRequest sofaRequest) throws SofaRpcException {
        String xid = RootContext.getXID();
        String rpcXid = getRpcXid();
        log.info("XID in RootContext[" + xid + "] XID in RpcInvokeContext[" + rpcXid + "]");
        boolean bind = false;
        if (xid != null) {
            RpcInvokeContext.getContext().putRequestBaggage(RootContext.KEY_XID, xid);
            log.info("RpcInvokeContext.PUT_REQUEST_BAGGAGE XID = {}", xid);
        } else {
            if (rpcXid != null) {
                RootContext.bind(rpcXid);
                bind = true;
                log.info("bind[" + rpcXid + "] to RootContext");
            }
        }
        return SeataTxCommon.getSofaResponse(filterInvoker, sofaRequest, rpcXid, bind, log);
    }


    /**
     * get rpc xid
     *
     * @return
     */
    private String getRpcXid() {
        String rpcXid = RpcInvokeContext.getContext().getRequestBaggage(RootContext.KEY_XID);
        if (rpcXid == null) {
            rpcXid = RpcInvokeContext.getContext().getRequestBaggage(RootContext.KEY_XID.toLowerCase());
        }
        return rpcXid;
    }

}
