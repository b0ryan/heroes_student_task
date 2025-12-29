# Скрипт для сборки проекта в JAR файл

# Поиск Java
$javaHome = $env:JAVA_HOME
if (-not $javaHome) {
    # Попытка найти Java в стандартных местах
    $possiblePaths = @(
        "C:\Program Files\Java\jdk*",
        "C:\Program Files (x86)\Java\jdk*",
        "$env:ProgramFiles\Java\jdk*",
        "$env:ProgramFiles(x86)\Java\jdk*"
    )
    
    foreach ($path in $possiblePaths) {
        $jdkPath = Get-ChildItem -Path $path -ErrorAction SilentlyContinue | Sort-Object Name -Descending | Select-Object -First 1
        if ($jdkPath) {
            $javaHome = $jdkPath.FullName
            break
        }
    }
}

if (-not $javaHome) {
    Write-Host "Ошибка: Java не найдена. Установите JDK или установите переменную окружения JAVA_HOME." -ForegroundColor Red
    exit 1
}

$javac = Join-Path $javaHome "bin\javac.exe"
$jar = Join-Path $javaHome "bin\jar.exe"

if (-not (Test-Path $javac)) {
    Write-Host "Ошибка: javac не найден в $javac" -ForegroundColor Red
    exit 1
}

Write-Host "Используется Java: $javaHome" -ForegroundColor Green

# Создаем директорию для скомпилированных классов
$buildDir = "build\classes"
$libsDir = "libs"
$jarFile = "obf.jar"

if (Test-Path $buildDir) {
    Remove-Item -Path $buildDir -Recurse -Force
}
New-Item -ItemType Directory -Path $buildDir -Force | Out-Null

Write-Host "Компиляция Java файлов..." -ForegroundColor Yellow

# Компилируем Java файлы
$sourceFiles = Get-ChildItem -Path "src" -Filter "*.java" -Recurse
$classpath = "$libsDir\heroes_task_lib-1.0-SNAPSHOT.jar"

& $javac -encoding UTF-8 -cp "$classpath" -d $buildDir $sourceFiles.FullName

if ($LASTEXITCODE -ne 0) {
    Write-Host "Ошибка компиляции!" -ForegroundColor Red
    exit 1
}

Write-Host "Создание JAR файла..." -ForegroundColor Yellow

# Создаем временную директорию для JAR
$jarTempDir = "build\jar-temp"
if (Test-Path $jarTempDir) {
    Remove-Item -Path $jarTempDir -Recurse -Force
}
New-Item -ItemType Directory -Path $jarTempDir -Force | Out-Null

# Копируем скомпилированные классы
Copy-Item -Path "$buildDir\*" -Destination $jarTempDir -Recurse -Force

# Распаковываем библиотеку в JAR
Write-Host "Добавление зависимостей из библиотеки..." -ForegroundColor Yellow
Push-Location $jarTempDir
& $jar -xf "..\..\$libsDir\heroes_task_lib-1.0-SNAPSHOT.jar"
Pop-Location

# Создаем JAR файл
if (Test-Path $jarFile) {
    Remove-Item -Path $jarFile -Force
}

Push-Location $jarTempDir
& $jar -cf "..\..\$jarFile" *
Pop-Location

Write-Host "JAR файл создан: $jarFile" -ForegroundColor Green
Write-Host "Размер файла: $((Get-Item $jarFile).Length) байт" -ForegroundColor Green

