package xingyu.lu.individual.svc.sofa.boot.filter;

import com.alipay.sofa.rpc.context.RpcInternalContext;
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
@Extension(value = "seataTxContextConsumerFilter")
@AutoActive(consumerSide = true)
@Slf4j
public class SeataTxContextConsumerFilter extends Filter {

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

        log.info("xid in RootContext[" + xid + "] xid in RpcContext[" + rpcXid + "]");

        boolean bind = false;
        if (xid != null) {
            sofaRequest.addRequestProp(RootContext.KEY_XID, xid);
        } else {
            if (rpcXid != null) {
                RootContext.bind(rpcXid);
                bind = true;

                log.debug("bind[" + rpcXid + "] to RootContext");
            }
        }
        try {
            return filterInvoker.invoke(sofaRequest);
        } finally {
            if (bind) {
                String unbindXid = RootContext.unbind();

                log.debug("unbind[" + unbindXid + "] from RootContext");
                if (!rpcXid.equalsIgnoreCase(unbindXid)) {
                    if (log.isWarnEnabled()) {
                        log.warn("xid in change during RPC from " + rpcXid + " to " + unbindXid);
                    }
                    if (unbindXid != null) {
                        RootContext.bind(unbindXid);
                        if (log.isWarnEnabled()) {
                            log.warn("bind [" + unbindXid + "] back to RootContext");
                        }
                    }
                }
            }
        }
    }

    /**
     * get rpc xid
     *
     * @return
     */
    private String getRpcXid() {
        String rpcXid = (String) RpcInternalContext.getContext().getAttachment(RootContext.KEY_XID);
        if (rpcXid == null) {
            rpcXid = (String) RpcInternalContext.getContext().getAttachment(RootContext.KEY_XID.toLowerCase());
        }
        return rpcXid;
    }

}
