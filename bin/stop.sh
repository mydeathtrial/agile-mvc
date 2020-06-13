#!/bin/bash

source /etc/profile
source ~/.bash_profile

#加载配置文件
function prepare()
{
    LOCAL_WORK_PATH=`echo $( cd "$( dirname "${BASH_SOURCE[0]}" )/../" && pwd )`
    LOCAL_BIN_PATH=${LOCAL_WORK_PATH}/bin
    DIALUP_PID=${LOCAL_BIN_PATH}/argus.pid
	return 0
}

function run()
{
    while read line
    do
	echo $line
	kill -9 $line
    done < ${DIALUP_PID}
    rm -rf ${DIALUP_PID}
    return 0
}

prepare

run

exit 0
