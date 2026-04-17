#!/bin/zsh

# Set Java home to local JDK
export JAVA_HOME="/Users/bharathank/pjs/l&t_capstone/oracleJdk-26.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

# Go to project directory
cd /Users/bharathank/pjs/l\&t_capstone/attendance-backend

# Build with Maven
/opt/homebrew/bin/mvn clean install -DskipTests
