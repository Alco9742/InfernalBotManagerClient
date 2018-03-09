echo off
set arg1=%1
set arg2=%2
set arg3=%3
SLEEP 30
echo "Waiting 30 seconds"
copy /b/v/y %arg1% %arg2%
copy /b/v/y %arg3% %arg1%
echo "Restarting manager in 10 seconds"
java -jar %arg1%

