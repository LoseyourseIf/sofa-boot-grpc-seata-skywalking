package xingyu.lu.individual.svc.sofa.boot.facade;

import com.alipay.sofa.rpc.core.exception.SofaRpcException;
import com.alipay.sofa.rpc.core.request.SofaRequest;
import com.alipay.sofa.rpc.core.response.SofaResponse;
import com.alipay.sofa.rpc.ext.Extension;
import com.alipay.sofa.rpc.filter.AutoActive;
import com.alipay.sofa.rpc.filter.Filter;
import com.alipay.sofa.rpc.filter.FilterInvoker;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

@Extension("customer")
@AutoActive(providerSide = true, consumerSide = true)
@Component
public class SofaRpcCustomFilter extends Filter {
    @Override
    public boolean needToLoad(FilterInvoker invoker) {
        return true;
    }
    @Override
    public SofaResponse invoke(FilterInvoker invoker, SofaRequest request) throws SofaRpcException {
        SofaResponse response = new SofaResponse();
        try {
             response = invoker.invoke(request);
        }catch (Exception e){
            response.setErrorMsg(ExceptionUtils.getMessage(e));
        }
        return response;
    }
}
