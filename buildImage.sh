#!/usr/bin/env bash

./gradlew clean bootJar
mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)
docker build -t shervin/user_service:$(./gradlew properties -q | grep version | awk '{print $2}') .
docker tag shervin/user_service:$(./gradlew properties -q | grep version | awk '{print $2}') shervin/user_service:latest
