package com.holderzone.saas.store.trading.center.interceptor;

import com.holderzone.saas.store.trading.center.utils.ThreadLocalCache;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import static com.holderzone.saas.store.dto.common.CommonConstant.USER_INFO;

public class FeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        template.header(USER_INFO, ThreadLocalCache.getJsonStr());
    }

}
