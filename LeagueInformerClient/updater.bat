echo off
set arg1=%1
set arg2=%2
set arg3=%3
set arg3=%4
timeout 10
echo "Waiting 10 seconds"
copy /b/v/y %arg1% %arg2%
copy /b/v/y %arg3% %arg1%
echo "Restarting manager in 5 seconds"
timeout 5
java -jar %arg1% %arg4%

