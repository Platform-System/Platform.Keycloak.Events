param(
    [switch]$Clean,
    [switch]$SkipTests
)

$ErrorActionPreference = "Stop"

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$toolsDir = Join-Path $repoRoot ".tools"
$jdkDir = Join-Path $toolsDir "jdk-17"
$workspaceJdkDir = Join-Path (Resolve-Path (Join-Path $repoRoot "..")) ".tools\jdk-17"
if (-not (Test-Path (Join-Path $jdkDir "bin\java.exe")) -and (Test-Path (Join-Path $workspaceJdkDir "bin\java.exe"))) {
    $jdkDir = $workspaceJdkDir
}
$javaExe = Join-Path $jdkDir "bin\java.exe"
$jdkZip = Join-Path $toolsDir "temurin-jdk-17.zip"
$jdkUrl = "https://api.adoptium.net/v3/binary/latest/17/ga/windows/x64/jdk/hotspot/normal/eclipse?project=jdk"

if (-not (Test-Path $javaExe)) {
    Write-Host "JDK 17 not found. Downloading portable JDK to $jdkDir..."
    New-Item -ItemType Directory -Force -Path $toolsDir | Out-Null

    if (-not (Test-Path $jdkZip)) {
        Invoke-WebRequest -Uri $jdkUrl -OutFile $jdkZip
    }

    $extractDir = Join-Path $toolsDir "jdk-17-extract"
    if (Test-Path $extractDir) {
        Remove-Item -LiteralPath $extractDir -Recurse -Force
    }

    New-Item -ItemType Directory -Force -Path $extractDir | Out-Null
    Expand-Archive -LiteralPath $jdkZip -DestinationPath $extractDir -Force

    $jdkHomeDir = Get-ChildItem $extractDir -Directory | Select-Object -First 1
    if (-not $jdkHomeDir) {
        throw "JDK archive did not contain a directory."
    }

    if (Test-Path $jdkDir) {
        Remove-Item -LiteralPath $jdkDir -Recurse -Force
    }

    Move-Item -LiteralPath $jdkHomeDir.FullName -Destination $jdkDir
    Remove-Item -LiteralPath $extractDir -Recurse -Force
}

$env:JAVA_HOME = $jdkDir
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

$mavenVersion = "3.9.9"
$localMaven = Join-Path $repoRoot ".mvn\wrapper\dists\apache-maven-$mavenVersion\apache-maven-$mavenVersion\bin\mvn.cmd"
$workspaceMaven = Join-Path (Resolve-Path (Join-Path $repoRoot "..")) ".mvn\wrapper\dists\apache-maven-$mavenVersion\apache-maven-$mavenVersion\bin\mvn.cmd"
$mavenCommand = ".\mvnw.cmd"
if (Test-Path $localMaven) {
    $mavenCommand = $localMaven
} elseif (Test-Path $workspaceMaven) {
    $mavenCommand = $workspaceMaven
}

$mavenArgs = @()
if ($Clean) {
    $mavenArgs += "clean"
}

if ($SkipTests) {
    $mavenArgs += "-DskipTests"
}

$mavenArgs += "package"

Push-Location $repoRoot
try {
    & $mavenCommand @mavenArgs
}
finally {
    Pop-Location
}
