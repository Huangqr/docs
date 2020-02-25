**版本**

		Finchley.SR1

**Gateway的整体原理**

		gateway有Predicate，Filter
		多个predicate同时满足才会路由到对应路径
		可以通过filter对请求进行拦截,进行权限验证，和限流等等

**使用例子：动态路由，权限验证**

- 导包
		//这个包用于暴露动态修改路由的接口
		<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>

		//当需要调用其他服务接口时导入，不需要引入httpclient的包
		<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

		<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
            <exclusions>
                <exclusion>
                        <groupId>org.hdrhistogram</groupId>
                        <artifactId>HdrHistogram</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

- 启动类

		@SpringBootApplication
		@EnableFeignClients
		public class HolderSaasGatewayApplication {
		
		    public static void main(String[] args) {
		        SpringApplication.run(HolderSaasGatewayApplication.class, args);
		    }
		}

- 配置
		//用于开启动态路由的接口
		spring:
		  application:
		    name: holder-saas-gateway
		management:
		  endpoints:
		    web:
		      exposure:
		        include: "*"
		
		server:
		  port: 8562
		//注册到注册中心
		eureka:
		  instance:
		    prefer-ip-address: true
		    instance-id: 192.168.3.82:${server.port}
		  client:
		    serviceUrl:
		      defaultZone: http://192.168.100.135:8762/eureka/

- 动态路由

		GatewayControllerEndpoint中暴露了添加路由，删除路由，查询路由，刷新路由接口，直接调用即可

- 自定义全局filter：实现GlobalFilter,Ordered

		public class TokenFilter implements GlobalFilter, Ordered {
		    
		
		    @Override
		    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		       return null;
		    }
		
		   
		    @Override
		    public int getOrder() {
		        return Ordered.HIGHEST_PRECEDENCE+1;
		    }
		}

		/** 跳过前缀的filter，如/test1/a,经过拦截器后变为/a */
		public class StripPrefixFilter implements GlobalFilter, Ordered {
		    private static final Logger log = LoggerFactory.getLogger(StripPrefixFilter.class);

		    /** 跳过前缀的数量 */
		    private static final Integer STRIP_PREFIX_COUNT = 1;
		
		    /** filter的顺序 */
		    private static final Integer ORDER = 16;
		
		    @Override
		    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		        ServerHttpRequest request = exchange.getRequest();
		        addOriginalRequestUrl(exchange, request.getURI());
		        String path = request.getURI().getRawPath();
		        String newPath = "/" + Arrays.stream(StringUtils.tokenizeToStringArray(path, "/"))
		                .skip(STRIP_PREFIX_COUNT).collect(Collectors.joining("/"));
		        ServerHttpRequest newRequest = request.mutate()
		                .path(newPath)
		                .build();
		        exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, newRequest.getURI());
		        return chain.filter(exchange.mutate().request(newRequest).build());
		    }
		
		    @Override
		    public int getOrder() {
		        return Ordered.HIGHEST_PRECEDENCE + ORDER * 1000;
		    }
		}

- 重写requestBody

		参考ModifyRequestBodyGatewayFilterFactory
		例子：
		 ServerRequest serverRequest = new DefaultServerRequest(exchange);
		 return serverRequest.bodyToMono(Map.class)
			.flatMap(map -> {
				对map进行逻辑操作 ----> newMap
				return rewriteRequestBodyAndPassFilter(chain,exchange,newMap)
			})		

		private Mono<Void> rewriteRequestBodyAndPassFilter(GatewayFilterChain chain, ServerWebExchange exchange, Map map) {
	        BodyInserter bodyInserter = BodyInserters.fromPublisher(Mono.just(map), Map.class);
	        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, exchange.getRequest().getHeaders());
	        return bodyInserter.insert(outputMessage, new BodyInserterContext())
	                .then(Mono.defer(() -> {
	                    ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(
	                            exchange.getRequest()) {
	                        @Override
	                        public HttpHeaders getHeaders() {
	                            HttpHeaders httpHeaders = new HttpHeaders();
	                            httpHeaders.putAll(super.getHeaders());
	                            httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
	                            return httpHeaders;
	                        }
	
	                        @Override
	                        public Flux<DataBuffer> getBody() {
	                            return outputMessage.getBody();
	                        }
	                    };
	                    return chain.filter(exchange.mutate().request(decorator).build());
	                }));
	    }



- 重写responseBody

		参考ModifyResponseBodyGatewayFilterFactory

- 自定义responseBody

		public static Mono<Void> unAuthorizedResponse(ServerWebExchange exchange) {
	        DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
	        Result<Object> result = Result.buildFailResult(HttpStatus.UNAUTHORIZED.value(), "身份验证失败");
	        byte[] resultBytes = JacksonUtils.toJsonByte(result);
	        return exchange.getResponse().writeWith(Mono.just(resultBytes).map(dataBufferFactory::wrap));
	    }