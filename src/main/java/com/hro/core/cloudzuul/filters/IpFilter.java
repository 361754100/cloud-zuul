package com.hro.core.cloudzuul.filters;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * (1) filterType:
 * .pre：可以在请求被路由之前调用。适用于身份认证的场景，认证通过后再继续执行下面的流程。
 * ·route：在路由请求时被调用。适用于灰度发布场景，在将要路由的时候可以做一些自定义的逻辑。
 * ·post：在route和error过滤器之后被调用。这种过滤器将请求路由到达具体的服务之后执行。适用于需要添加响应头，记录响应日志等应用场景。
 * ·error：处理请求时发生错误时被调用。在执行过程中发送错误时会进入error过滤器，可以用来统一记录错误信息
 *
 */
public class IpFilter extends ZuulFilter {

    private List<String> blackIpList = new ArrayList<>();

    public IpFilter(){
        super();
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
//        return true;
        // 有可能设置了多个过滤器，根据全局参数决定是否执行过滤器
        RequestContext cxt = RequestContext.getCurrentContext();
        Object isSuccess = cxt.get("isSuccess");
        return isSuccess == null? true: Boolean.parseBoolean(isSuccess.toString());
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        String reqIp = ctx.getRequest().getRemoteAddr();
        if(StringUtils.isEmpty(reqIp) || !blackIpList.contains(reqIp)) {
            return null;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("code", 400);
        data.put("msg", "request not allow...");

        

        ctx.setResponseBody(JSON.toJSONString(data));
        ctx.setSendZuulResponse(false); // 告诉Zuul，不要把请求转发到后端服务
        ctx.set("sendForwardFilter.ran", true); // 是用来拦截本地转发请求的
        ctx.set("isSuccess", false); // 把是否执行的标记写入上下文中，接下来的过滤器获取到此标记，判断是否执行
        ctx.getResponse().setContentType("application/json; charset=utf-8");
        return null;
    }
}
