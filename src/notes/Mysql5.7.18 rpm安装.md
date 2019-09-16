## ==Mysql5.7.18 rpm安装==

### 1、安装新版mysql之前，我们需要将系统自带的mariadb-lib卸载
```
rpm -e --nodeps mariadb-libs-5.5.52-1.el7.x86_64
```
### 2、到mysql的官网下载包
最新版mysql的rpm集合包：mysql-5.7.18-1.el6.x86_64.rpm-bundle.tar

### 3、上传包
上传mysql-5.7.18-1.el6.x86_64.rpm-bundle.tar到linux服务器，并解压tar包

### 4、使用rpm -ivh命令进行安装
```
注：安装包有依赖关系，执行有先后，必须按顺序。
rpm -ivh mysql-community-common-5.7.18-1.el6.x86_64.rpm --nosignature
rpm -ivh mysql-community-libs-5.7.18-1.el6.x86_64.rpm --nosignature
rpm -ivh mysql-community-client-5.7.18-1.el6.x86_64.rpm --nosignature
rpm -ivh mysql-community-server-5.7.18-1.el6.x86_64.rpm --nosignature
```
使用rpm安装方式安装mysql，安装的路径如下：
```
a、数据库目录
/var/lib/mysql/

b、配置文件
/usr/share/mysql(mysql.server命令及配置文件)

c、相关命令
/usr/bin(mysqladmin mysqldump等命令)

d、启动脚本
/etc/rc.d/init.d/(启动脚本文件mysql的目录)

e、/etc/my.conf

```
修改my.cnf文件
```
vi /etc/my.cnf
```
```
#做如下配置  
[client]
password = 123456
port = 3306
default-character-set=utf8
[mysql]
default-character-set = utf8
[mysqld]
port = 3306
character_set_server=utf8
character_set_client=utf8
collation-server=utf8_general_ci
#linux下mysql安装完后是默认：表名区分大小写，列名不区分大小写； 0：区分大小写，1：不区分大小写
lower_case_table_names=1
#设置最大连接数，默认为 151，MySQL服务器允许的最大连接数16384
max_connections=1000
```
```
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8
[mysqld]
query_cache_type=1
innodb_online_alter_log_max_size=1G
innodb_open_files=4096
table_open_cache=4096
innodb_lock_wait_timeout=120
innodb_log_files_in_group=3
thread_cache_size=300
max_connect_errors=3000
back_log=800
server-id=1
innodb_log_file_size=4G
innodb_buffer_pool_size=5G
log_bin_trust_function_creators=true
slave_skip_errors=all
character_set_server=utf8
default_storage_engine=InnoDB
wait_timeout=28800
expire_logs_days=7
long_query_time=10
binlog_format=ROW
log-bin=mysql-bin
query_cache_size=128M
join_buffer_size=16M
sort_buffer_size=16M
max_heap_table_size=256M
binlog_cache_size=4M
max_allowed_packet=32M
max_connections=3000
character_set_server=utf8
character_set_client=utf8
collation-server=utf8_general_ci
datadir=/var/lib/mysql
socket=/var/lib/mysql/mysql.sock
symbolic-links=0
log-error=/var/log/mysqld.log
pid-file=/var/run/mysqld/mysqld.pid

```

### 5、 数据库初始化

为了保证数据库目录为与文件的所有者为 mysql 登陆用户，如果你的linux系统是以 root 身份运行 mysql 服务，需要执行下面的命令初始化
```
mysqld --initialize --user=mysql
```
注：
如果是以 mysql 身份登录运行，则可以去掉 --user 选项。

另外 --initialize 选项默认以“安全”模式来初始化，则会为 root 用户生成一个密码并将该密码标记为过期，登陆后你需要设置一个新的密码。

而使用 --initialize-insecure 命令则不使用安全模式，则不会为 root 用户生成一个密码。

这里演示使用的 --initialize 初始化的，会生成一个 root 账户密码，密码在log文件里，红色区域的就是自动生成的密码。
```
cat /var/log/mysqld.log
```
返回结果 ：
```
2017-07-07T14:05:59.206431Z 0 [Warning] TIMESTAMP with implicit DEFAULT value is deprecated. Please use --explicit_defaults_for_timestamp server option (see documentation for more details).
2017-07-07T14:06:00.819481Z 0 [Warning] InnoDB: New log files created, LSN=45790
2017-07-07T14:06:01.442226Z 0 [Warning] InnoDB: Creating foreign key constraint system tables.
2017-07-07T14:06:01.465202Z 0 [Warning] No existing UUID has been found, so we assume that this is the first time that this server has been started. Generating a new UUID: 6863a61c-631d-11e7-88a2-000c29e8ddc9.
2017-07-07T14:06:01.600093Z 0 [Warning] Gtid table is not ready to be used. Table mysql.gtid_executed cannot be opened.
2017-07-07T14:06:01.601339Z 1 [Note] A temporary password is generated for root@localhost: 'nlEkl>wiD5P_'
```
或使用grep查找密码
```
grep "temporary password" /var/log/mysqld.log
```

### 6、启动服务。
```
service mysqld start
/etc/inint.d/mysqld start
注：centos7启动方式：
systemctl start mysqld.service
```
### 7、配置。
连接数据库。
```
mysql -u root -p
```
修改密码
```
set password = password('你的密码');
```
设置远程访问
```
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '123abcABC' WITH GRANT OPTION;
FLUSH PRIVILEGES;
quit;
```
设置mysql开机启动
```
加入到系统服务：
chkconfig --add mysqld
自动启动：
chkconfig mysqld on
查询列表：
chkconfig
说明：都没关闭（off）时是没有自动启动。
```










