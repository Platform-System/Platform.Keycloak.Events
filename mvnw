#!/usr/bin/env sh
set -eu

BASE_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
MAVEN_VERSION="3.9.9"
DIST_DIR="$BASE_DIR/.mvn/wrapper/dists/apache-maven-$MAVEN_VERSION"
MAVEN_HOME="$DIST_DIR/apache-maven-$MAVEN_VERSION"
MAVEN_ZIP="$DIST_DIR/apache-maven-$MAVEN_VERSION-bin.zip"
MAVEN_URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MAVEN_VERSION/apache-maven-$MAVEN_VERSION-bin.zip"

if [ ! -x "$MAVEN_HOME/bin/mvn" ]; then
  echo "Maven $MAVEN_VERSION not found. Downloading to $DIST_DIR..."
  mkdir -p "$DIST_DIR"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$MAVEN_URL" -o "$MAVEN_ZIP"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$MAVEN_URL" -O "$MAVEN_ZIP"
  else
    echo "curl or wget is required to download Maven." >&2
    exit 1
  fi
  unzip -q -o "$MAVEN_ZIP" -d "$DIST_DIR"
fi

exec "$MAVEN_HOME/bin/mvn" "$@"
