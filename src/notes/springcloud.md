
		
**学习路线**  

	服务注册与发现 eureka和fegin
	断路器（调用服务发生异常处理）
	网关路由(根据路由请求不同服务)
	配置中心 spring-cloud-config(统一管理配置)
	
	消息总线()
	服务追踪(调用服务过程追踪)
	断路器监控()
	
**版本**

	Edgware.SR3

**注册中心**

- 手动剔除服务

		http://{注册中心地址}:{端口}/eureka/apps/{服务名称}/{要剔除的服务}  （delete请求）

		如：http://192.168.100.100:8141/eureka/apps/BASE-SERVICE/192.168.102.4:8171

		Eureka强制下线
		
		可以通过调用stateUpdate接口，更改实例的状态为OUT_OF_SERVICE
		实现方式
		调用接口：/eureka/apps/appID/instanceID/status?value=OUT_OF_SERVICE
		调用示例：http://101.37.33.252:8083/eureka/apps/EUREKA-1/10.28.144.127:17101/status?value=OUT_OF_SERVICE
		调用方式：PUT

		手动上线

		删除实例的覆盖状态，同时修改实例的状态为UP ， 当客户端的缓存刷新之后，获取到EUREKA-1的状态为UP
		实现方式
		请求接口：/eureka/apps/appID/instanceID/status?value=UP
		调用示例：http://101.37.33.252:8083/eureka/apps/EUREKA-1/10.28.144.127:17101/status?value=UP
		调用方式：DELETE


- 配置  
		jar包
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
		</dependency>
		
		启动类
		@SpringBootApplication
		@EnableEurekaServer
		public class SpringcloudEurekaServerApplication {
		
			public static void main(String[] args) {
				SpringApplication.run(SpringcloudEurekaServerApplication.class, args);
			}
		}
		
		配置文件
		server:
		  port: 8001
		eureka:
		  instance:
		    hostname: localhost
			prefer-ip-address: true
		  client:
		    registerWithEureka: false
		    fetchRegistry: false
		    serviceUrl:
		      defaultZone: http://${eureka.instance.hostname}:8761/eureka/
		  server:
		    #关闭自我保护
		    enable-self-preservation: false
		    #剔除失效服务间隔
		    eviction-interval-timer-in-ms: 5000	

**服务注册**

- 配置
		jar包
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		配置文件
		server:
		  port: 8002
		eureka:
		  instance:
		    lease-renewal-interval-in-seconds: 5
		    lease-expiration-duration-in-seconds: 10
		    instance-id: ${spring.cloud.client.ip-address}:${server.port}
		    prefer-ip-address: true
		  client:
		    service-url:
		      defaultZone: http://localhost:8001/eureka/
		spring:
		  application:
		    name: springcloud-eureka-provider1

		启动类
		@SpringBootApplication
		@EnableEurekaClient
		public class SpringcloudEurekaProvider1Application {
		
			public static void main(String[] args) {
				SpringApplication.run(SpringcloudEurekaProvider1Application.class, args);
			}
		}

		服务注册类
		@RestController
		public class Provider1Controller {
		
		    @GetMapping("/hello")
		    public String hello(){
		        return "hello";
		    }
		}


**服务消费**

- 配置

		jar包
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-openfeign</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		配置文件
		eureka:
		  instance:
		    lease-renewal-interval-in-seconds: 5
		    lease-expiration-duration-in-seconds: 10
		    instance-id: ${spring.cloud.client.ip-address}:${server.port}
		    prefer-ip-address: true
		  client:
		    service-url:
		      defaultZone: http://localhost:8001/eureka/
		server:
		  port: 8003
		spring:
		  application:
		    name: first-consumer

		启动类
		@SpringBootApplication
		@EnableFeignClients
		public class SpringcloudEurekaClient1Application {
		
			public static void main(String[] args) {
				SpringApplication.run(SpringcloudEurekaClient1Application.class, args);
			}
		}

		服务发现类
		@FeignClient(name = "springcloud-eureka-provider1")
		public interface Provider1Service {
		
		    @GetMapping("/hello")
		    String hello();
		}
		
		使用apache的httpclient替换feign的默认httpclient
		jar包：
			<dependency>
				<groupId>io.github.openfeign</groupId>
				<artifactId>feign-httpclient</artifactId>
			</dependency>
		配置文件：
			feign:
			  httpclient:
			    enabled: true
				connection-timeout: 300000
				max-connections: 500
				time-to-live: 30
				time-to-live-unit: seconds
		提高请求并发配置：在使用apache的httpclient的基础上
		ribbon:
		  ConnectionTimeout: 10000
		  ReadTimeout: 10000
		hystrix:
		  command:
		    default:
		      fallback:
		        isolation:
		          semaphore:
		            maxConcurrentRequests: 100
		      execution:
		        isolation:
					strategy: SEMAPHORE(默认是线程thread.)
      				semaphore:
        				maxConcurrentRequests: 2000
		            thread:
		            	timeoutInMilliseconds: 15000
		        timeout:
		          enable: true
		  threadpool:
		    default:
		      coreSize: 50
		      maximumSize: 500
		      maxQueueSize: 500
		      queueSizeRejectionThreshold: 300
		      keepAliveTimeMinutes: 10
		      allowMaximumSizeToDivergeFromCoreSize: true
		      metrics:
		        rollingStats:
		          numBuckets: 500

		注意事项：
			使用@RequestParam注解时，需要在括号里指明参数名,例：void hello(@RequestParam("id") String id)

**服务注册（consul）**

		<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-consul-discovery</artifactId>
            <version>2.1.1.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
		
		@SpringBootApplication
		@EnableFeignClients
		@EnableSwagger2
		@EnableDiscoveryClient //（引入注解）
		@EnableApolloConfig
		public class AggregationApplication {
			public static void main(String[] args) {
				ConfigurableApplicationContext app = SpringApplication.run(AggregationApplication.class, args);
				SpringContextUtils.getInstance().setCfgContext(app);
			}
		}

		spring:
		  cloud:
		    consul:
		      host: 192.168.17.132
		      port: 8500
		      discovery:
		        health-check-path: /actuator/health   # 健康健康路径，也可以自己写
		        health-check-interval: 10s            # 检测轮训时间 1m 代码1分钟
		        instance-id: ${spring.application.name}:${vcap.application.instance_id:${spring.application.instance_id:${random.value}}}
				// 可替换为 ${spring.application.name}:${spring.cloud.client.ipAddress}


**断路器**
	
- 配置

		feign:
		  hystrix:
		    enabled: true
		
		@FeignClient(name = "springcloud-eureka-provider1", fallbackFactory = Provider1ServiceFallback.class)
		public interface Provider1Service {
		
		    @GetMapping("/hello")
		    String hello() throws Throwable;
		}
		
		@Component
		public class Provider1ServiceFallback implements FallbackFactory<Provider1Service> {
		
		    @Override
		    public Provider1Service create(Throwable throwable) {
		        return new Provider1Service() {
		            @Override
		            public String hello() throws Throwable {
		                throw throwable;
		            }
		        };
		    }
		}

**熔断监控**

- 监控module配置

		jar包：
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
			</dependency>

			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
			</dependency>
		配置文件：
			无
		启动类：
			@SpringBootApplication
			@EnableHystrixDashboard
			public class SpringcloudHystrixDashboardApplication {
			
				public static void main(String[] args) {
					SpringApplication.run(SpringcloudHystrixDashboardApplication.class, args);
				}
			}

- 被监控客户端配置

		jar包：
			<dependency>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-starter-actuator</artifactId>
			</dependency>
	
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
			</dependency>
		配置文件:
			management:
			  endpoints:
			    web:
			      exposure:
			        include: hystrix.stream
		启动类:
			@SpringBootApplication
			@EnableFeignClients
			@EnableHystrix
			@EnableCircuitBreaker
			public class SpringcloudEurekaClient1Application {
			
				public static void main(String[] args) {
					SpringApplication.run(SpringcloudEurekaClient1Application.class, args);
				}
			}
- 查看网页；
	
		首先打开：监控端http://localhost:8006/hystrix,在监控地址栏填写被监控端的地址http://localhost:8003/hystrix.stream

- 以上是监控单个应用的，通过整合turbine可以监控多个

		- 新增turbine项目
			
			jar包:
				<dependency>
				    <groupId>org.springframework.cloud</groupId>
				    <artifactId>spring-cloud-starter-netflix-turbine</artifactId>
				</dependency>
			配置文件:
				spring:
				  application:
				    name: turbine
				server:
				  port: 8007
				eureka:
				  client:
				    service-url:
				      defaultZone: http://localhost:8001/eureka/
				turbine:
				  app-config: first-consumer,second-consumer
				  cluster-name-expression: new String("default")
				  combine-host-port: true

				turbine.app-config参数指定了需要收集监控信息的服务名；
				turbine.cluster-name-expression 参数指定了集群名称为 default，当我们服务数量非常多的时候，可以启动多个 Turbine 服务来构建不同的聚合集群，而该参数可以用来区分这些不同的聚合集群，同时该参数值可以在 Hystrix 仪表盘中用来定位不同的聚合集群，只需要在 Hystrix Stream 的 URL 中通过 cluster 参数来指定；
				turbine.combine-host-port参数设置为true，可以让同一主机上的服务通过主机名与端口号的组合来进行区分，默认情况下会以 host 来区分不同的服务，这会使得在本地调试的时候，本机上的不同服务聚合成一个服务来统计	
			启动类:
				@SpringBootApplication
				@EnableTurbine
				public class SpringcloudHystrixTurbineApplication {
				
					public static void main(String[] args) {
						SpringApplication.run(SpringcloudHystrixTurbineApplication.class, args);
					}
				}
 			网页查看
			首先打开：监控端http://localhost:8006/hystrix,在监控地址栏填写turbine项目的地址http://localhost:8007/turbine.stream



**配置中心**：

- 配置
		git仓库配置文件:
			provider2-dev.properties:dao.name=dao1
		1.不注入到注册中心
					
			服务端：
				jar包：
					<dependency>
						<groupId>org.springframework.cloud</groupId>
						<artifactId>spring-cloud-config-server</artifactId>
					</dependency>
				启动类：
					@SpringBootApplication
					@EnableConfigServer
					public class SpringcloudEurekaConfApplication {
					
						public static void main(String[] args) {
							SpringApplication.run(SpringcloudEurekaConfApplication.class, args);
						}
					}
				配置文件：application.yml
					spring:
					  application:
					      name: springcloud-eureka-conf
					  cloud:
					    config:
					      server:
					        git:
					          uri: https://github.com/SuperDaoOffice/springcloud-global-conf.git
					          username: dao_office
					          password: 121994hu
					server:
					  port: 8005
			客户端：
				jar包：
					<dependency>
						<groupId>org.springframework.cloud</groupId>
						<artifactId>spring-cloud-starter-config</artifactId>
					</dependency>
				配置文件：bootstrap.yml
					spring: 
					 cloud: 
					  config: 
					   label: master 
					   profile: dev
					   name: provider2
					   uri: http://localhost:8005/
				启动类：
					无额外注解
				调用：
					@RestController
					public class Provider1Controller {
					
					    @Value("${dao.name}")
					    private String name;
					
					    @GetMapping("/hello")
					    public String hello(){
					        System.out.println(name);
					        return name;
					    }
					}
		2.配置到注册中心
			
			服务端：
				jar包
					添加<dependency>
							<groupId>org.springframework.cloud</groupId>
							<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
						</dependency>
				启动类：
					添加@EnableEurekaClient
				配置文件：
					添加：
						eureka:
						  client:
						    service-url:
						      defaultZone: http://localhost:8001/eureka/
			客户端：
				jar包:
					<dependency>
						<groupId>org.springframework.cloud</groupId>
						<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
					</dependency>
					<dependency>
						<groupId>org.springframework.cloud</groupId>
						<artifactId>spring-cloud-starter-config</artifactId>
					</dependency>
				启动类
					@SpringBootApplication
					@EnableEurekaClient
					@EnableFeignClients
				配置文件：
					需要使用bootstrap.yml，而不是application.yml
					server:
					  port: 8002
					eureka:
					  client:
					    service-url:
					      defaultZone: http://localhost:8001/eureka/
					spring:
					  application:
					    name: springcloud-eureka-provider1
					  cloud:
					    config:
					      discovery:
					        enabled: true
					        serviceId : springcloud-eureka-conf
					      label: master
					      profile: dev
					      name: provider2
**消息总线+配置中心**

- 配置中心端：
	
		没有变化，可以注册到注册中心或者不注册

- 调用配置的客户端

		jar包：每一个客户端都得配置这个jar包
			加入
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-starter-bus-amqp</artifactId>
			</dependency>

		配置文件：每一个客户端都得配置rabbit配置
			spring:
			  rabbitmq:
			    host: 47.106.95.183
			    port: 5672
			    username: dao
			    password: 121994hu

		发送更新请求/bus/refresh的客户端需要关闭安全的配置,未关闭会显示没有认证
			management:
			    security:
			      enabled: false
		
		调用配置的类：需要加入@RefreshScope，不然无法更新
			@Service
			@RefreshScope
			public class BusService {
			
			    @Value("${dao.name}")
			    private String name;
			
			    public String hello(){
			        return name;
			    }
			}

**网关**
	
	- 导包：
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-starter-netflix-zuul</artifactId>
			</dependency>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
			</dependency>
	- 启动类：
			@SpringBootApplication
			@EnableZuulProxy
			@EnableEurekaClient
			public class SpringcloudEurekaZuulApplication {
			
				public static void main(String[] args) {
					SpringApplication.run(SpringcloudEurekaZuulApplication.class, args);
				}
			}
	- 配置文件：
			
			zuul:
			  ignored-services: '*'
			  routes:
			    first-provider:
			      path: /first-provider/**
			      serviceId: springcloud-eureka-provider1
			server:
			  port: 8004
			spring:
			  application:
			    name: springcloud-eureka-zuul
			eureka:
			  client:
			    service-url:
			      defaultZone: http://localhost:8001/eureka/

			提升并发：
				zuul:
				  host:
				    max-total-connections: 2000
				    max-per-route-connections: 2000
				    connect-timeout-millis: 10000
				    socket-timeout-millis: 60000
				  ribbon-isolation-strategy: semaphore
				  semaphore:
				    max-semaphores: 2000
				hystrix:
				    command:
				        default:
				            execution:
				                isolation:
				                    thread:
				                        timeoutInMilliseconds: 121000
				#ribbonTimeout = (ReadTimeout+ConnectTimeout)*(maxAutoRetries+1)*(maxAutoRetriesNextServer+1)
				#timeoutInMilliseconds值必须大于ribbonTimeout
				ribbon:
				  ReadTimeout: 10000
				  ConnectTimeout: 50000
				  maxAutoRetries: 0
				  maxAutoRetriesNextServer: 1

	
				
**服务追踪**

- 服务端：
	
		http方式
			执行命令curl -sSL https://zipkin.io/quickstart.sh | bash -s下载jar包
			java -jar zipkin.jar
		
		rabbit方式
			RABBIT_ADDRESSES=localhost java -jar zipkin.jar
			其他环境变量：
				RABBIT_CONCURRENCY	并发消费者数量，默认为1
				RABBIT_CONNECTION_TIMEOUT	建立连接时的超时时间，默认为 60000毫秒，即 1 分钟
				RABBIT_QUEUE	从中获取 span 信息的队列，默认为 zipkin
				RABBIT_URI	符合 RabbitMQ URI 规范 的 URI，例如amqp://user:pass@host:10000/vhost
			如果设置了URI，则一下属性可以忽略：
				RABBIT_ADDRESSES	用逗号分隔的 RabbitMQ 地址列表，例如localhost:5672,localhost:5673
				RABBIT_PASSWORD	连接到 RabbitMQ 时使用的密码，默认为 guest
				RABBIT_USER	连接到 RabbitMQ 时使用的用户名，默认为guest
				RABBIT_VIRTUAL_HOST	使用的 RabbitMQ virtual host，默认为 /
				RABBIT_USE_SSL	设置为true则用 SSL 的方式与 RabbitMQ 建立链接

- 客户端

		1.http方式：
			jar包:		
				<dependency>
					<groupId>org.springframework.cloud</groupId>
					<artifactId>spring-cloud-starter-sleuth</artifactId>
				</dependency>
	
				<dependency>
		            <groupId>org.springframework.cloud</groupId>
		            <artifactId>spring-cloud-starter-zipkin</artifactId>
        		</dependency>
			配置文件：
				spring:
				  zipkin:
				    base-url: http://47.106.95.183:9411/
				  sleuth:
				    sampler:
				      probability: 1.0
						
		2.rabbitmq方式
			jar包：
				添加：<dependency>
						<groupId>org.springframework.cloud</groupId>
						<artifactId>spring-cloud-stream-binder-rabbit</artifactId>
					</dependency>
			配置文件：
				添加
				spring:
					rabbitmq:
					    host: 47.106.95.183
					    port: 5672
					    username: dao
					    password: 121994hu
				修改：
				spring:
					zipkin:
						sender:
	     					 type: rabbit
				
			
- 各阶段消耗时间

		cs - Client Sent -客户端发送一个请求，这个注解描述了这个Span的开始
		sr - Server Received -服务端获得请求并准备开始处理它，如果将其sr减去cs时间戳便可得到网络传输的时间。
		ss - Server Sent （服务端发送响应）–该注解表明请求处理的完成(当请求返回客户端)，如果ss的时间戳减去sr时间戳，就可以得到服务器请求的时间。
		cr - Client Received （客户端接收响应）-此时Span的结束，如果cr的时间戳减去cs时间戳便可以得到整个请求所消耗的时间


					
