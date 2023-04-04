# ED-cloud
该项目是一个开源的加密去重云存储原型系统，优化了系统的数据存储效率和数据传输效率，采用前后端
分离开发，基于web进行数据的存储，适合当前多端访问的情景。
## 目录说明

- back-cloud 后端代码
- vue-cloud 前端代码
- doc 项目相关文档
- log 工作日志

## 项目部署手册

项目代码地址https://github.com/darKmili/cloud  

back-cloud 为后端代码

vue-cloud为前端代码

前端后端分别部署，下面分别介绍各端的部署。

### 1. 后端

#### 2.1数据库

数据库推荐使用mysql5.7，如果是mysql8需要修改驱动。redis推荐7.0以上

##### 2.1.1 MySQL

1. 安装MySQL
在Ubuntu中可执行以下默认命令安装MySQL

`$ sudo apt-get update  #更新软件源`

`$ sudo apt-get install mysql-server  #安装mysql`

Ubuntu 16中执行上述操作默认安装的是MySQL5版本的，若想要安装特定版本的mysql，可以采用下述三种方式安装：
-  直接下载二进制压缩包进行安装，解压并设置相关的参数就可以运行。
- 下载你想要安装的版本的源，然后通过apt install mysql-server=5.7.31的方式进行安装。·
- 下载特定版本的deb文件，然后通过dpkg -i的方式安装。

安装成功后，可以通过命令service MySQL status，可以查看当前MySQL的状态。
使用mysql默认安装完成就启动了mysql。若操作无法完成，可以使用命令 `service mysql start`
启动mysql服务器

2. 创建数据库
输入`mysql -u root -p`，然后输入安装时设置的密码。

进入MySQL shell界面，执行以下命令：

`mysql>  create  database training;  # 创建数据库`

`mysql> use cloud;  # 使用数据库`

`mysql> source /home/leon/cloud.sql  # 执行sql脚本，路径可以根据具体的sql文件决定。`

3. 修改数据库表名大小写不区分

mysql数据库一般表名都是不区分大写小的,但这个不区分大小写只在windows系统中有效,在linux系统中是区分大小写的,所以需要修改mysql的配置文件。

`$ vim /etc/mysql/mysql.conf.d/ mysqld.cnf  # 进入配置文件`

在文件中添加一行：

`# 大小写不敏感 `

`lower_case_table_names=1`

然后重启数据库

##### 2.1.2 redis

redis只需要单机版即可。具体安装步骤见[redis部署指南](https://redis.io/docs/getting-started/installation/install-redis-on-linux/)

建议给redis设置密码,后端默认redis密码为123456，密码根据具体情况修改

有需要和条件的，可以部署redis多机版

#### 2.2 ceph分布式存储

ceph的相关部署和安装具体见 [ceph集群搭建](https://github.com/darKmili/cloud/blob/main/doc/ceph%E9%9B%86%E7%BE%A4%E6%90%AD%E5%BB%BA.pdf)

#### 2.3 相关配置参数说明

#### 2.3.1 数据库配置

对应后端代码中的application.yml文件



```yaml
spring:
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    username: root # 账号视情况而定
    password: 123456 # 密码视情况而定
    url: jdbc:mysql://localhost:3306/cloud?characterEncoding=UTF-8&useSSL=false

  jpa:
    properties:
      hibernate:
        format_sql: true
        hbm2ddl:
          auto: update
  #    show-sql: true
```

#### 2.3.2 redis配置

```yaml
spring:
  redis:
    host: localhost #地址按照情况填写
    # 连接超时时间（记得添加单位，Duration）
    timeout: 10000ms
    # Redis默认情况下有16个分片，这里配置具体使用的分片
    # database: 0
    lettuce:
      pool:
        # 连接池最大连接数（使用负值表示没有限制） 默认 8
        max-active: 8
        # 连接池最大阻塞等待时间（使用负值表示没有限制） 默认 -1
        max-wait: -1ms
        # 连接池中的最大空闲连接 默认 8
        max-idle: 8
        # 连接池中的最小空闲连接 默认 0
        min-idle: 0
    password: 123456 # 密码根据自己情况，没有则不填
```

#### 2.3.3 s3配置

```yaml
ceph:
  accessKey: RXX2H1XCSUHM3VTR7DEH # 来自于CEPH中生成的
  secretKey: VsxM8RqRfQCmfHg4GO6uTRaidEp90lWedgVzWBdf # 来自于CEPH中生成的
  bucket: cloud
  ## 填写域名或者IP
  host: 192.168.1.112
```

#### 2.3.4 打包部署

项目采用spring boot开发，只需要简单通过maven打包成jar包，通过`java -jar `命令运行即可。

也可以采用war包部署，具体参考网上教程，不在赘述

### 2. 前端



1.  安装nginx  `sudo apt install nginx`
2.  验证安装成功 `nginx -v`
3. 修改配置文件  `vim /etc/nginx/nginx.conf` 

![关键配置](https://github.com/darKmili/cloud/blob/main/doc/image-20230324174547873.png)

4. 将前端 /dist 目录上传到服务器/home/leon/下，上传前先 npm run build前端

5.  重启nginx  `nginx -s reload`

## 项目架构

该云存储采用三层架构模型，前端-后端-存储端，整个系统从客户端到存储端，自上而下，用户可以在PC或者手机上通过Web浏览器访问加密云存储系统。
1. 客户端：客户端为用户提高简洁的直接交互平台。用户进入客户端前，需要注册账号，初次注册用户时，云端会主动为用户分配云存储空间，生成主密钥、用户密钥、验证哈希、客户端随机数等。在用户登录账号进入客户端后，会为列出他所有的文件和文件夹。
2. 服务端：服务控制端是云存储系统的核心部分。所有用户数据的元数据(metadata)的由服务控制端进行管理和存储，元数据中记录了文件的相关信息，即文件名、文件类型、文件大小等信息。还提供了身份验证、权限认定、业务逻辑、数据处理等功能。
3. 存储端：存储端是基于ceph的存储服务器集群，提供对象存储服务，负责对象数据的存储与维护，保证数据访问的稳定性，数据在存储端以对象存储的方式保存。

系统原型拓扑图：
![系统拓扑图](https://github.com/darKmili/cloud/blob/main/doc/系统拓扑图.png)
