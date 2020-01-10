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



					
