@echo off
rem 设置监听的端口号

set "port=18088"
set /p port=your will stop application's port,default(18088):
echo kill port %port%

for /f "usebackq tokens=1-5" %%a in (`netstat -ano ^| findstr %port%`) do (
	if [%%d] EQU [LISTENING] (
		set pid=%%e
	)
)

if '%pid%'=='' goto notfound

for /f "usebackq tokens=1-5" %%a in (`tasklist ^| findstr %pid%`) do (
	set image_name=%%a
)

echo now will kill process : pid %pid%, image_name %image_name%
pause
rem 根据进程ID，kill进程
taskkill /f /pid %pid%
goto end

:notfound
echo I'm sorry ,I can't find this port

:end
pause