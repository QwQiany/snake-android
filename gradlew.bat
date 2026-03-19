@ECHO OFF
SETLOCAL

SET APP_HOME=%~dp0
SET WRAPPER_JAR=%APP_HOME%gradle\wrapper\gradle-wrapper.jar

IF NOT EXIST "%WRAPPER_JAR%" (
    WHERE gradle >NUL 2>NUL
    IF %ERRORLEVEL% EQU 0 (
        gradle %*
        EXIT /B %ERRORLEVEL%
    )

    ECHO gradle-wrapper.jar is missing. Open the project in Android Studio or install Gradle and run "gradle wrapper".
    EXIT /B 1
)

IF DEFINED JAVA_HOME (
    SET JAVA_EXE=%JAVA_HOME%\bin\java.exe
) ELSE (
    SET JAVA_EXE=java.exe
)

"%JAVA_EXE%" -Xmx64m -Xms64m -classpath "%WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*
