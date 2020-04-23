**学习路线**  

	服务注册与发现 consul
	
**版本**

	Hoxton.RELEASE
	<dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-dependencies</artifactId>
        <version>Hoxton.RELEASE</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>

**服务注册**

- 配置
  
        配置文件需要配置到  bootstrap.yml
        	
		jar包
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-consul-discovery</artifactId>
            <version>2.2.1.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
		
		启动类
		@SpringBootApplication
        @EnableDiscoveryClient
        @EnableAutoConfiguration
        public class HoxtonConsulApplication {
        	public static void main(String[] args) {
        		SpringApplication.run(HoxtonConsulApplication.class, args);
        	}
        }
		
		在配置文件中如上配置后可以使得服务下线后自动删除无效服务
		spring.cloud.consul.host：配置consul地址
        spring.cloud.consul.port：配置consul端口
        spring.cloud.consul.discovery.enabled：启用服务发现
        spring.cloud.consul.discovery.register：启用服务注册
        spring.cloud.consul.discovery.deregister：服务停止时取消注册
        spring.cloud.consul.discovery.prefer-ip-address：表示注册时使用IP而不是hostname
        spring.cloud.consul.discovery.health-check-interval：健康检查频率
        spring.cloud.consul.discovery.health-check-path：健康检查路径
        spring.cloud.consul.discovery.health-check-critical-timeout：健康检查失败多长时间后，取消注册
        spring.cloud.consul.discovery.instance-id：服务注册标识

		配置文件
		server:
		  port: 8001
		spring:
          application:
            name: spring-cloud-hoxton
          cloud:
            consul:
              enabled: true
              host: 192.168.17.132
              port: 8500
              discovery:
                register: true
                prefer-ip-address: true
                port: ${server.port}
                deregister: true
                healthCheckPath: /actuator/health
                healthCheckInterval: 15s
                health-check-critical-timeout: 30s

**配置管理**

- 配置

        jar包
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-consul-config</artifactId>
            <version>2.2.1.RELEASE</version>
        </dependency>
        
        spring:
          profiles:
            active: dev
          application:
            name: spring-cloud-hoxton-consul
          cloud:
            consul:
              config:
                # 是否启用配置中心功能
                enabled: true
                # 设置配置值的格式
                format: yaml
                # 设置配置所在目录
                prefix: config
                # 设置配置的分隔符
                profile-separator: ':'
                # 配置key的名字，由于Consul是K/V存储，配置存储在对应K的V中
                data-key: data
                watch:
                  enabled: true
                  
        配置文件 K键 规则：  config/spring-cloud-hoxton-consul:dev/data
        
        实时刷新配置方式：
        @RefreshScope   或   @ConfigurationProperties文件可自动刷新

**配置管理**

- 配置

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
            <version>${spring-cloud-starter.version}</version>
        </dependency>

        Ribbon 中有两种和时间相关的设置，分别是请求连接的超时时间和请求处理的超时时间，设置规则如下：
        # 请求连接的超时时间
        ribbon.ConnectTimeout=2000
        # 请求处理的超时时间
        ribbon.ReadTimeout=5000
        
        也可以为每个Ribbon客户端设置不同的超时时间, 通过服务名称进行指定：
        <服务名>.ribbon.ConnectTimeout=2000
        <服务名>.ribbon.ReadTimeout=5000
        
        并发参数
        # 最大连接数
        ribbon.MaxTotalConnections=500
        # 每个host最大连接数
        ribbon.MaxConnectionsPerHost=500

**错误**

- 启动报错

        Error running '服务名': Command line is too long. Shorten command line for ServiceStarter or also for Application default configuration.
        解决办法：
        修改项目下 .idea\workspace.xml，找到标签 <component name="PropertiesComponent"> 
        在标签里加一行 <property name="dynamic.classpath" value="true"/>
    
    
					
