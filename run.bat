@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-17
echo Starting Pulse Check API...
echo JAVA_HOME is set to: %JAVA_HOME%
echo.
.\mvnw.cmd spring-boot:run