@echo off
title Comandos do Projeto
setlocal

:menu
cls
echo ===============================
echo       MENU DE COMANDOS
echo ===============================
echo.
echo 1 - Build Completo
echo 2 - Build da Aplicacao
echo 3 - Rodar Testes
echo 4 - Gerar Documentacao (Swagger + Redoc)
echo 0 - Sair
echo.
set /p opcao=Escolha uma opcao: 

if "%opcao%"=="1" goto buildCompleto
if "%opcao%"=="2" goto build
if "%opcao%"=="3" goto testes
if "%opcao%"=="4" goto docs
if "%opcao%"=="0" exit

echo Opcao invalida!
pause
goto menu

:: ===============================
:: 1 - Build Completo
:: ===============================
:buildCompleto
echo.
echo [BUILD COMPLETO]
call :build
call :testes
call :docs
echo.
echo Processo completo finalizado!
pause
goto menu

:: ===============================
:: 2 - Build
:: ===============================
:build
echo.
echo [BUILD]
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo ERRO no build!
    pause
    goto menu
)
echo Build finalizado com sucesso!
exit /b 0

:: ===============================
:: 3 - Testes
:: ===============================
:testes
echo.
echo [TESTES]
call mvn test
if %errorlevel% neq 0 (
    echo ERRO nos testes!
    pause
    goto menu
)
echo Testes executados com sucesso!
exit /b 0

:: ===============================
:: 4 - Documentacao
:: ===============================
:docs
echo.
echo [DOCUMENTACAO]

REM Criar pasta se nao existir
if not exist docs\openapi (
    mkdir docs\openapi
)

echo Gerando spec.json...
curl http://localhost:8080/v3/api-docs -o docs\openapi\spec.json
if %errorlevel% neq 0 (
    echo ERRO ao gerar spec.json
    echo Verifique se a API esta rodando!
    pause
    goto menu
)

echo Gerando spec.html com Redoc...
call redoc-cli build docs\openapi\spec.json -o docs\openapi\spec.html
if %errorlevel% neq 0 (
    echo ERRO ao gerar spec.html
    pause
    goto menu
)

echo Documentacao gerada com sucesso!
echo Caminho: docs\openapi\spec.html
pause
goto menu