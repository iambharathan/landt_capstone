#!/bin/zsh

# Set Java home to local JDK
export JAVA_HOME="/Users/bharathank/pjs/l&t_capstone/oracleJdk-26.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

# Go to project directory
cd /Users/bharathank/pjs/l\&t_capstone/attendance-backend

# Run Spring Boot directly
echo "Starting Spring Boot application..."
/opt/homebrew/bin/mvn spring-boot:run -DskipTests
