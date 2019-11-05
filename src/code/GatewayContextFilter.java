package com.holderzone.saas.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author HuChiHui
 * @date 2019/06/03 下午 18:28
 * @description
 */
@Component
public class GatewayContextFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(GatewayContextFilter.class);

    /**
     * filter的顺序
     */
    private static final Integer ORDER = 1;


    /**
     * default HttpMessageReader
     */
    private static final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        /**
         * save request path and serviceId into gateway context
         */

        GatewayContext gatewayContext = new GatewayContext();
        /**
         * save gateway context into exchange
         */
        exchange.getAttributes().put(GatewayContext.CACHE_GATEWAY_CONTEXT,gatewayContext);

        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        MediaType contentType = headers.getContentType();
        long contentLength = headers.getContentLength();
        if(contentLength>0){
            if(MediaType.APPLICATION_JSON.equals(contentType) || MediaType.APPLICATION_JSON_UTF8.equals(contentType)){
                return readBody(exchange, chain,gatewayContext);
            }

        }
        return chain.filter(exchange);

    }


    @Override
    public int getOrder() {
        return  Ordered.HIGHEST_PRECEDENCE +  ORDER*1000;
    }



    /**
     * ReadJsonBody
     * @param exchange
     * @param chain
     * @return
     */
    private Mono<Void> readBody(ServerWebExchange exchange, GatewayFilterChain chain, GatewayContext gatewayContext){
        /**
         * join the body
         */
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .flatMap(dataBuffer -> {
                    /**
                     * read the body Flux<Databuffer>
                     */
                    DataBufferUtils.retain(dataBuffer);
                    Flux<DataBuffer> cachedFlux = Flux.defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
                    /**
                     * repackage ServerHttpRequest
                     */
                    ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return cachedFlux;
                        }
                    };
                    /**
                     * mutate exchage with new ServerHttpRequest
                     */
                    ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
                    /**
                     * read body string with default messageReaders
                     */
                    return ServerRequest.create(mutatedExchange, messageReaders)
                            .bodyToMono(Object.class)
                            .doOnNext(gatewayContext::setCacheBody).then(chain.filter(mutatedExchange));
                });
    }

}
