:: https://github.com/wkgcass/public-chat/issues/23

@echo off
:: 首先检查是否以管理员身份运行脚本
net session >nul 2>&1
if %errorLevel% == 0 (
    echo Script is running as admin
    :: 下面以管理员权限启动其他程序
    start "You may close this window" "%~dp0\bin\app.bat"
) else (
    echo Script is not running as admin
    :: 若未以管理员权限运行，则使用以下命令重新以管理员权限运行脚本
    echo Restarting script with admin rights...
    powershell -Command "Start-Process cmd.exe -Verb RunAs -ArgumentList \"/c %~0\""
)
