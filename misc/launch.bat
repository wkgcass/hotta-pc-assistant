@echo off

REM https://stackoverflow.com/questions/11525056/how-to-create-a-batch-file-to-run-cmd-as-administrator   Northstrider

if _%1_==_payload_  goto :payload

:getadmin
    echo %~nx0: elevating self
    set vbs=%temp%\getadmin.vbs
    echo Set UAC = CreateObject^("Shell.Application"^)                >> "%vbs%"
    echo UAC.ShellExecute "%~s0", "payload %~sdp0 %*", "", "runas", 1 >> "%vbs%"
    "%temp%\getadmin.vbs"
    del "%temp%\getadmin.vbs"
goto :eof

:payload

set DIR="%~dp0"

pushd %DIR% & "%~dp0\bin\app.bat" %* & popd

echo.
echo...Script Complete....
echo.

pause
