# define constants

FROM centos:centos7
MAINTAINER Tongmeng <mydeathtrial@163.com>

ARG mysql_rpm=https://repo.mysql.com//mysql80-community-release-el7-3.noarch.rpm

RUN yum -y update && \
    yum -y install epel-release && \
    yum -y install initscripts && \
    localedef -c -f UTF-8 -i zh_CN zh_CN.utf8 && \
    #设置时区
    echo "Asia/shanghai" > /etc/timezone && \
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

# install mysql
RUN yum -y install mysql

COPY ../mysql /mysql
EXPOSE 3306
VOLUME ["/etc/mysql", "/var/lib/mysql"]
ENTRYPOINT ["sh","/mysql/mysql_start.sh"]



## install redis
#RUN yum -y install redis && \
#    #修改绑定IP地址
#    sed -i -e 's@bind 127.0.0.1@bind 0.0.0.0@g' /etc/redis.conf && \
#    #关闭保护模式
#    sed -i -e 's@protected-mode yes@protected-mode no@g' /etc/redis.conf && \
#    #设置密码
#    echo "requirepass 123456" >> /etc/redis.conf
#EXPOSE 6379
#ENTRYPOINT ["/usr/bin/redis-server","/etc/redis.conf"]
#
## install nginx
#RUN yum -y install nginx
#ADD ../nginx.conf /etc/nginx/nginx.conf
#VOLUME ["/etc/nginx/conf.d", "/var/log/nginx"]
#ENTRYPOINT ["nginx","-c","/etc/nginx/nginx.conf","-s","reload"]
