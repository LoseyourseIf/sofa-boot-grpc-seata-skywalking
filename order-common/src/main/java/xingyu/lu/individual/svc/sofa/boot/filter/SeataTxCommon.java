package xingyu.lu.individual.svc.sofa.boot.filter;

import com.alipay.sofa.rpc.core.request.SofaRequest;
import com.alipay.sofa.rpc.core.response.SofaResponse;
import com.alipay.sofa.rpc.filter.FilterInvoker;
import io.seata.core.context.RootContext;
import org.slf4j.Logger;

public class SeataTxCommon {
    static SofaResponse getSofaResponse(FilterInvoker filterInvoker, SofaRequest sofaRequest, String rpcXid, boolean bind, Logger log) {
        try {
            return filterInvoker.invoke(sofaRequest);
        } finally {
            if (bind) {
                String unbindXid = RootContext.unbind();
                log.info("unbind[" + unbindXid + "] from RootContext");
                if (!rpcXid.equalsIgnoreCase(unbindXid)) {
                    log.info("xid in change during RPC from " + rpcXid + " to " + unbindXid);
                    if (unbindXid != null) {
                        RootContext.bind(unbindXid);
                        log.info("bind [" + unbindXid + "] back to RootContext");
                    }
                }
            }
        }
    }
}
