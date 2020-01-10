package com.study.consul.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import springfox.documentation.spring.web.plugins.Docket;

@Component
@RefreshScope
public class ConsulRefreshConfig implements InitializingBean, ApplicationListener<RefreshEvent> {

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public void onApplicationEvent(RefreshEvent refreshEvent) {
    }
}
