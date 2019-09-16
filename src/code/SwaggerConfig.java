package com.think.thinkinjava.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${swagger.enable}")
    private Boolean swaggerEnable;

    @Bean
    public Docket createRestApi() {

        //在配置好的配置类中增加此段代码即可
        ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        //name表示名称，description表示描述
        ticketPar.name("Authorization").description("登录校验")
                .modelRef(new ModelRef("string")).parameterType("header")
                //required表示是否必填，DefaultValue表示默认值
                .required(false).defaultValue("Bearer ").build();

        //添加完此处一定要把下边的带***的也加上否则不生效
        pars.add(ticketPar.build());

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                //************把消息头添加
                .globalOperationParameters(pars)
                .select()
                //这里采用包含注解的方式来确定要显示的接口
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                // 这里采用包扫描的方式来确定要显示的接口
                //.apis(RequestHandlerSelectors.basePackage("com.stylefeng.guns.modular.system.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("travel-admin-ws Doc")
                .description("travel-admin-web Api文档")
                .termsOfServiceUrl("")
                .version("1.0")
                .build();
    }

}
