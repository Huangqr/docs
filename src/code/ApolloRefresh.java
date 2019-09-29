package com.holderzone.resource.msp.config;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author HuChiHui
 * @date 2019/09/16 下午 14:02
 * @description
 */
@Component
public class ApolloRefresh implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(ApolloRefresh.class);

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @ApolloConfigChangeListener(value = {"application-test.yml" , "application-release.yml" , "application-ctyun.yml" , "application-hw.yml"})
    public void onChange(ConfigChangeEvent changeEvent){
        if (!changeEvent.changedKeys().isEmpty()){
            log.info("刷新配置......");
            this.applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));
        }
    }
}
