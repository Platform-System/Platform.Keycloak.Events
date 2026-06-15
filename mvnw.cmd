@echo off
setlocal

set "BASE_DIR=%~dp0"
set "MAVEN_VERSION=3.9.9"
set "WRAPPER_DIR=%BASE_DIR%.mvn\wrapper"
set "DIST_DIR=%WRAPPER_DIR%\dists\apache-maven-%MAVEN_VERSION%"
set "MAVEN_HOME=%DIST_DIR%\apache-maven-%MAVEN_VERSION%"
set "MAVEN_ZIP=%DIST_DIR%\apache-maven-%MAVEN_VERSION%-bin.zip"
set "MAVEN_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip"

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
  echo Maven %MAVEN_VERSION% not found. Downloading to %DIST_DIR%...
  powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "$ErrorActionPreference='Stop';" ^
    "New-Item -ItemType Directory -Force -Path '%DIST_DIR%' | Out-Null;" ^
    "Invoke-WebRequest -Uri '%MAVEN_URL%' -OutFile '%MAVEN_ZIP%';" ^
    "Expand-Archive -LiteralPath '%MAVEN_ZIP%' -DestinationPath '%DIST_DIR%' -Force;"
  if errorlevel 1 (
    echo Failed to download Maven %MAVEN_VERSION%.
    exit /b 1
  )
)

"%MAVEN_HOME%\bin\mvn.cmd" %*
exit /b %ERRORLEVEL%
