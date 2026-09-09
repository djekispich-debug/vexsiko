@echo off
set JAVA_HOME=D:\Programs\Java1\Java21
set GRADLE_USER_HOME=D:\gradle-home
cd /d D:\Downloads\Kimiko_Fix_Src
call D:\gradle-home\wrapper\dists\gradle-9.7.1-bin\1w1c7tv4s851m17nbqdsro2tv\gradle-9.7.1\bin\gradle.bat build -x check --offline --console=plain > build_log.txt 2>&1
echo EXITCODE=%ERRORLEVEL% >> build_log.txt
