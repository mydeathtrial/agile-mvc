#!/bin/bash

source /etc/profile
source ~/.bash_profile

#加载配置文件
function prepare()
{
    LOCAL_WORK_PATH=`echo $( cd "$( dirname "${BASH_SOURCE[0]}" )/../" && pwd )`
    LOCAL_BIN_PATH=${LOCAL_WORK_PATH}/bin
    LOCAL_LIB_PATH=${LOCAL_WORK_PATH}/lib
    LOCAL_CONF_PATH=${LOCAL_WORK_PATH}/conf
    LOCAL_LOGS_PATH=${LOCAL_WORK_PATH}/logs
    LOCAL_TMP_PATH=${LOCAL_WORK_PATH}/tmp
    DIALUP_PID=${LOCAL_BIN_PATH}/argus.pid
    #如果配置文件中需要获取环境变量,需要加export
    export LOCAL_LOGS_PATH
    #创建上传文件临时路径
    LOCAL_UPLOAD_PATH=${LOCAL_TMP_PATH}
    mkdir -p ${LOCAL_UPLOAD_PATH}
    export LOCAL_UPLOAD_PATH

    return 0
}

function run()
{
    #WORK_ROOT=$(cd "$(dirname "$0")"; pwd)
    #echo $WORK_ROOT
    #cd $WORK_ROOT
    
    #JAVA_OPTS="$JAVA_OPTS -Drun_dir=$WORK_ROOT"
    JAVA_OPTS="$JAVA_OPTS -Xss256k -Xms1g -Xmx1g -Xss256k"
    JAVA_OPTS="$JAVA_OPTS -XX:PermSize=512m -XX:MaxPermSize=512m"
    JAVA_OPTS="$JAVA_OPTS -XX:+DisableExplicitGC -XX:ParallelGCThreads=16 -XX:+CMSClassUnloadingEnabled -XX:+UseConcMarkSweepGC -XX:+UseParNewGC"
    JAVA_OPTS="$JAVA_OPTS -XX:CMSFullGCsBeforeCompaction=5 -XX:CMSInitiatingOccupancyFraction=80 -XX:MaxTenuringThreshold=15 "
    #JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintSafepointStatistics -XX:+PrintTenuringDistribution -XX:+PrintHeapAtGC -Xloggc:./gc.log "
    JAVA_OPTS="$JAVA_OPTS -Dclient.enczoding.override=UTF-8 -Dfile.encoding=UTF-8 -Duser.language=zh -Duser.region=CN"
    JAVA_OPTS="$JAVA_OPTS -Djava.ext.dirs=$LOCAL_LIB_PATH -Djava.library.path=$LOCAL_LIB_PATH -Xbootclasspath/a:$LOCAL_CONF_PATH"

    JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4001,suspend=n"

    #开启jmx远程
    JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote"
    JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote=true"
    JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.port=18889"
    JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.ssl=false"
    JAVA_OPTS="$JAVA_OPTS -Dcom.sun.managementote.ssl=false"
    JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.local.only=false"
    JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.authenticate=false"

    #下面是飞行记录取样分析
    JAVA_OPTS="$JAVA_OPTS -XX:+UnlockCommercialFeatures"
    JAVA_OPTS="$JAVA_OPTS -XX:+FlightRecorder"
    echo $JAVA_OPTS
    
    #nohup java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n $JAVA_OPTS com.idss.argus.Application $@ >/dev/null 2>&1 &
    #java $JAVA_OPTS com.agile.App
    #线上环境
    #nohup java $JAVA_OPTS com.agile.App $@ >/dev/null 2>&1 &
    #测试环境
    nohup java $JAVA_OPTS com.idss.App 主程序 $@ > ${LOCAL_WORK_PATH}/run.logs  2>&1 &
    echo $! > ${DIALUP_PID}
    return 0
}

prepare

run

