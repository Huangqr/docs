package com.holderzone.saas.gateway.filter;

public class GatewayContext {

    public static final String CACHE_GATEWAY_CONTEXT = "cacheGatewayContext";


    private Object cacheBody;


    public static String getCacheGatewayContext() {
        return CACHE_GATEWAY_CONTEXT;
    }

    public Object getCacheBody() {
        return cacheBody;
    }

    public void setCacheBody(Object cacheBody) {
        this.cacheBody = cacheBody;
    }


}
