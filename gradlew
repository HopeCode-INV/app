#!/usr/bin/env sh

dirname="$(dirname "$0")"
cd "$dirname"

if [ -x "gradlew" ]; then
    exec "$dirname/gradlew" "$@"
else
    echo "Gradle wrapper not found. Please run: gradle wrapper"
    exit 1
fi
