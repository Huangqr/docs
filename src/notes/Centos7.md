# Centos7

**在线安装jdk1.8**

    安装之前先检查一下系统有没有自带open-jdk
    命令：
    rpm -qa |grep java
    rpm -qa |grep jdk
    rpm -qa |grep gcj

    rpm -ivh 包全名
    选项:
        -i(install)    安装
        -v(verbose)    显示详细信息
        -h(hash)       显示进度
        --nodeps       不检测依赖性

    如果没有输入信息表示没有安装。
    如果安装可以使用rpm -qa | grep java | xargs rpm -e --nodeps 批量卸载所有带有Java的文件  这句命令的关键字是java
    首先检索包含java的列表
    yum list java*
    检索1.8的列表
    yum list java-1.8*   
    安装1.8.0的所有文件

    yum install java-1.8.0-openjdk* -y
    
    使用命令检查是否安装成功
    java -version
    到此安装结束了。这样安装有一个好处就是不需要对path进行设置，自动就设置好了
    
    
    修改iptables配置
    vim /etc/sysconfig/iptables

**RocketMQ**
   
    启动namesrv：
    nohup sh ./bin/mqnamesrv &
    关闭namesrv服务：sh ./bin/mqshutdown namesrv
    查看启动日志：tail -f ~/logs/rocketmqlogs/namesrv.log
    启动成功：The Name Server boot success…
    
    启动mqbroker
          nohup sh ./bin/mqbroker -n localhost:9876 autoCreateTopicEnable=true &
    关闭broker服务 ：sh ./bin/mqshutdown broker
          查看日志：tail -f ~/logs/rocketmqlogs/broker.log
          启动成功：register broker

**Consul**
   
	mkdir consul
	chmod 777 consul
    cd consul
    wget https://releases.hashicorp.com/consul/1.3.0/consul_1.3.0_linux_amd64.zip
    unzip consul_1.3.0_linux_amd64.zip
    cp consul /usr/local/bin/
    consul agent -dev -ui -node=consul-dev -client=10.39.45.56
    consul agent -data-dir /tmp/node0 -node=node0 -bind=10.39.45.56 -datacenter=dc1 -ui -client=10.39.45.56 -server -bootstrap-expect 1

**yum**
   
    yum install -y lrzsz        上传下载工具
    yum install -y unzip zip	解压缩工具
    
    python
    安装python后，Yum无法执行问题
    vim /usr/bin/yum 修改python解析版本
    vim /usr/libexec/urlgrabber-ext-down

**Docker部署**

    Docker修改容器名称：
    Docker rename <原名称> <新名称>
    
    Docker时间和主机时间不一致加下列参数
    -v /etc/localtime:/etc/localtime

    安装jenkins
    mkdir /var/Jenkins
    chown -R 1000:1000 /var/jenkins
    启动容器：docker run -itd -p 8080:8080 -p 50000:50000 --name jenkins --privileged=true -v /var/jenkins:/var/jenkins_home Jenkins

    RabbitMQ安装
    docker pull rabbitmq: management
    启动容器：docker run -d rabbitmq --name rabbit -e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=admin -p 15672:15672 rabbitmq:management

    Redis安装:
    Docker pull redis:4.0
    docker run -d -p 6379:6379 -it -v redis.conf:/etc/redis/redis.conf --name redis redis:4.0 redis-server /etc/redis/redis.conf

    zipkin安装：
    docker pull zipkin
    docker run --name zipkin -d -p 9411:9411 openzipkin/zipkin

