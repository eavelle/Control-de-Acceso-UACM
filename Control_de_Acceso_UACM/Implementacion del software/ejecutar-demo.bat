@echo off
chcp 65001 >nul
title Control de Acceso UACM - Modo demostracion
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0ejecutar-demo.ps1"
echo.
echo La aplicacion se detuvo. Presiona una tecla para cerrar esta ventana.
pause >nul
