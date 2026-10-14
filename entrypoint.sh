#!/bin/sh
set -e

# Render assigns a dynamic port via the PORT environment variable (defaults to 8080 if not set)
TARGET_PORT="${PORT:-8080}"

echo "=========================================================="
echo " Starting Academic Management Backend on Tomcat 10.1"
echo " Listening Port: $TARGET_PORT"
echo " Environment: ${ENVIRONMENT:-production}"
echo "=========================================================="

# Dynamically update Tomcat HTTP connector port in server.xml
sed -i "s/port=\"8080\"/port=\"$TARGET_PORT\"/g" /usr/local/tomcat/conf/server.xml

# Execute catalina.sh in the foreground to keep container running with PID 1
exec catalina.sh run
