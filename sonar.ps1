<#
.SYNOPSIS
    Analiza el proyecto demo contra tu SonarQube.

.DESCRIPTION
    Pregunta la URL y el token, y lanza el analisis. NO guarda nada:
    el token vive solo mientras dure la sesion de PowerShell.

    Alternativa manual (lo mismo que hace este script):

        $env:SONAR_HOST_URL = "TU-SONARQUBE"
        $env:SONAR_TOKEN    = "squ_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
        mvn clean verify sonar:sonar

    ...o sin variables de entorno, pasandolo todo por parametro.
    OJO: las comillas alrededor de cada -D son obligatorias en PowerShell:

        mvn clean verify sonar:sonar `
            "-Dsonar.host.url=TU-SONARQUBE" `
            "-Dsonar.token=squ_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
#>

param(
    [string]$SonarUrl = "TU-SONARQUBE",
    [switch]$SinPreguntar
)

$ErrorActionPreference = "Stop"
$demo = Join-Path $PSScriptRoot "demo-app"

Write-Host ""
Write-Host "  Analisis de calidad - Curso Sonar" -ForegroundColor Cyan
Write-Host "  ---------------------------------" -ForegroundColor Cyan

if ($SonarUrl -eq "TU-SONARQUBE") {
    $SonarUrl = Read-Host "  URL de tu SonarQube"
    if ([string]::IsNullOrWhiteSpace($SonarUrl)) { $SonarUrl = "TU-SONARQUBE" }
}

$token = Read-Host "  Token (se genera en My Account > Security)" -AsSecureString
$tokenPlano = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [Runtime.InteropServices.Marshal]::SecureStringToBSTR($token))

if ([string]::IsNullOrWhiteSpace($tokenPlano)) {
    Write-Host "  Sin token: no se puede analizar." -ForegroundColor Yellow
    exit 1
}

$env:SONAR_HOST_URL = $SonarUrl
$env:SONAR_TOKEN    = $tokenPlano

try {
    Write-Host ""
    Write-Host "  mvn clean verify sonar:sonar" -ForegroundColor DarkGray
    Push-Location $demo
    mvn -B clean verify "sonar:sonar"
    $codigo = $LASTEXITCODE
}
finally {
    # El token no queda ni en el entorno ni en el historial
    Remove-Item Env:SONAR_TOKEN -ErrorAction SilentlyContinue
    Remove-Item Env:SONAR_HOST_URL -ErrorAction SilentlyContinue
    $tokenPlano = $null
    $token = $null
    [GC]::Collect()
    Pop-Location
}

Write-Host ""
if ($codigo -eq 0) {
    Write-Host "  Analisis enviado. Mira el resultado en tu SonarQube." -ForegroundColor Green
}
else {
    Write-Host "  El build ha fallado: no llego a analizar." -ForegroundColor Red
    Write-Host "  Si un test falla, el informe de cobertura tampoco se genera." -ForegroundColor Yellow
}
Write-Host ""
