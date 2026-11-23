# Arranca el Sistema de Control de Acceso UACM en modo demostración.
# No requiere MySQL ni Docker: usa una base de datos H2 en memoria.
#
# Uso:
#   .\ejecutar-demo.ps1
#
# Si PowerShell bloquea el script, ejecuta una vez:
#   Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass

$ErrorActionPreference = 'Stop'
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$proyecto = Split-Path -Parent $MyInvocation.MyCommand.Path

# Busca la instalación de IntelliJ IDEA más reciente.
$idea = Get-ChildItem 'C:\Program Files\JetBrains' -Directory -Filter 'IntelliJ IDEA*' -ErrorAction SilentlyContinue |
        Sort-Object Name -Descending |
        Select-Object -First 1

if (-not $idea) {
    Write-Host 'No se encontró IntelliJ IDEA en C:\Program Files\JetBrains.' -ForegroundColor Red
    Write-Host 'Instala IntelliJ IDEA o edita este script con la ruta de tu JDK y Maven.' -ForegroundColor Yellow
    exit 1
}

$jbr = Join-Path $idea.FullName 'jbr'
$mvn = Join-Path $idea.FullName 'plugins\maven-plugin\lib\maven3\bin\mvn.cmd'

if (-not (Test-Path $mvn)) {
    Write-Host "No se encontró Maven en: $mvn" -ForegroundColor Red
    exit 1
}

Write-Host ''
Write-Host '==========================================================' -ForegroundColor Cyan
Write-Host '  Sistema de Control de Acceso UACM - Modo demostración' -ForegroundColor Cyan
Write-Host '==========================================================' -ForegroundColor Cyan
Write-Host ''
Write-Host "JDK:    $jbr"
Write-Host "Maven:  $mvn"
Write-Host ''
Write-Host 'Base de datos: H2 en memoria (no requiere MySQL).'
Write-Host 'Cuando veas "Started ControlAccesoApplication", abre:' -ForegroundColor Green
Write-Host '  http://localhost:8080' -ForegroundColor Green
Write-Host ''
Write-Host 'Para detener la aplicación presiona Ctrl + C.' -ForegroundColor Yellow
Write-Host ''

$env:JAVA_HOME = $jbr
$env:SPRING_PROFILES_ACTIVE = 'demo'
Set-Location $proyecto

& $mvn spring-boot:run
