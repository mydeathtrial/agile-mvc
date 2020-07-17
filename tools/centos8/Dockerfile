# define constants

FROM centos:latest
MAINTAINER Tongmeng <mydeathtrial@163.com>

ARG mysql_rpm=https://repo.mysql.com//mysql80-community-release-el7-3.noarch.rpm

RUN echo "alias ll = 'ls -l'" >> ~/.bashrc && \
#   设置中文字符集
    yum install -y langpacks-zh_CN glibc-common && \
    echo -e 'export LANG="zh_CN.UTF-8"\nexport LC_ALL="zh_CN.UTF-8"' > /etc/locale.conf && \
    source /etc/locale.conf && \
#   设置时区
    echo "Asia/shanghai" > /etc/timezone && \
#   更新所有组件
    yum -y update && \
    yum -y install epel-release

# install mysql
RUN yum -y localinstall $mysql_rpm && \
    yum -y module disable mysql && \
    yum -y install mysql-community-server && \
    mkdir -p /var/run/mysqld && \
    chown mysql:mysql /var/run/mysqld && \
#   sed -i -e "s/^bind-address\s*=\s*127.0.0.1/bind-address = 0.0.0.0/" /etc/mysql/mysql.conf.d/mysqld.cnf && \
    sed -i 's/^\(log_error\s.*\)/# \1/' /etc/mysql/my.cnf && \
    echo "mysqld_safe &" > /tmp/mysql_config && \
    echo "mysqladmin --silent --wait=30 ping || exit 1" >> /tmp/mysql_config && \
    echo "mysql -e \"GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' IDENTIFIED BY '123456' WITH GRANT OPTION;\"" >> /tmp/mysql_config && \
    bash /tmp/mysql_config && \
    rm -f /tmp/mysql_config
VOLUME ["/etc/mysql", "/var/lib/mysql"]

RUN yum -y install redis

# install nginx
RUN yum -y install nginx
ADD ../nginx.conf /nginx/conf/nginx.conf
VOLUME ["/nginx/conf", "/nginx/logs"]

