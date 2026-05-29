@echo off
echo ========================================
echo  Personal Nutritionist App - Auto Setup
echo ========================================
echo.
echo Step 1: Setting JAVA_HOME to your Android Studio JBR...
set "STUDIO_PATH=C:\Program Files\Android\Android Studio"
set "JAVA_HOME=%STUDIO_PATH%\jbr"
echo JAVA_HOME set to: %JAVA_HOME%
echo.
echo Step 2: Done! Now open Android Studio and:
echo   1. File - Open - Select this NutritionistApp folder
echo   2. When error appears, click the BLUE LINK to upgrade Gradle
echo   3. Change Gradle JDK to "Embedded JDK" in Settings
echo.
pause
