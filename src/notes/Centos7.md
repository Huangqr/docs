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

	docker pull consul
	docker run -d --name consul --privileged=true -p 8400:8400 -p 8500:8500 -p 8600:53/udp -u root  -v /data/consul:/consul/data consul agent -dev -ui -node=consul-dev -client=0.0.0.0

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

	如果创建时未指定 --restart=always ,可通过update 命令设置（容器自启动）
	docker update --restart=always <容器名称>
    
    Docker时间和主机时间不一致加下列参数
    -v /etc/localtime:/etc/localtime

    安装jenkins
    mkdir /var/Jenkins
    chown -R 1000:1000 /var/jenkins
    启动容器：docker run -itd -p 8080:8080 -p 50000:50000 --name jenkins --privileged=true -v /var/jenkins:/var/jenkins_home Jenkins

    RabbitMQ安装
    docker pull rabbitmq: management
    启动容器：docker run -d --name rabbitmq -e RABBITMQ_DEFAULT_USER=root -e RABBITMQ_DEFAULT_PASS=root -p 15672:15672 -p 5672:5672 rabbitmq:management

    Redis安装:
    Docker pull redis:4.0
    docker run -d -p 6379:6379 -it -v redis.conf:/etc/redis/redis.conf --name redis redis:4.0 redis-server /etc/redis/redis.conf

    zipkin安装：
    docker pull zipkin
    docker run --name zipkin -d -p 9411:9411 openzipkin/zipkin
    
    mongoDB安装：
    docker pull mongo:latest
    docker run -itd --name mongo -p 27017:27017 mongo --auth
    设置账号：
        docker exec -it mongo mongo admin
        use db
		db.createUser({user:"admin",pwd:"123456",roles:[{role: 'userAdminAnyDatabase', db: 'admin'},{role: 'readWrite', db: 'admin'}]})
        db.auth('admin', '123456')
        
     zookeeper:
     docker run --privileged=true -d --name zookeeper --publish 2181:2181  -d zookeeper:latest

**Docker镜像加速地址**

	docker pull dockerhub.azk8s.cn/xxx/xxx:yyy
	
**Docker-compose**

    https://github.com/docker/compose/releases/ docker-compose版本选择地址
    
    curl -L https://github.com/docker/compose/releases/download/1.25.4/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
	
**CentOS**

    设置服务开机启动
    systemctl enable <serverName>
    systemctl daemon-reload


**History**
	
	显示操作时间/操作账号/IP

	vim /etc/bashrc

	在最后加上

	HISTFILESIZE=4000 #默认保存命令是1000条，这里修改为4000条
	HISTSIZE=4000
	USER_IP=`who -u am i 2>/dev/null| awk '{print $NF}'|sed -e 's/[()]//g'` #取得登录客户端的IP
	if [ -z $USER_IP ]
	then
	USER_IP=`hostname`
	fi
	HISTTIMEFORMAT="%F %T $USER_IP:`whoami` " #设置新的显示history的格式
	export HISTTIMEFORMAT

	source /etc/bashrc