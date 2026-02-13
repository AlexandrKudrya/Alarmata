# Sleep Guardian - CI/CD Setup

> Continuous Integration и Continuous Deployment для автоматической сборки и тестирования

---

## Overview

Цель: автоматизировать процесс от коммита в GitHub до готового APK, который можно скачать и установить.

**Workflow:**
1. Claude Code пушит изменения в GitHub
2. GitHub Actions автоматически:
   - Запускает тесты
   - Собирает APK
   - Публикует APK как artifact
3. Ты скачиваешь готовый APK с телефона

---

## GitHub Actions Setup

### Main Workflow File

Создай файл `.github/workflows/android-build.yml`:

```yaml
name: Android CI/CD

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    name: Run Unit Tests
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle
      
      - name: Grant execute permission for gradlew
        run: chmod +x gradlew
      
      - name: Run unit tests
        run: ./gradlew test --stacktrace
      
      - name: Upload test results
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: test-results
          path: |
            **/build/reports/tests/
            **/build/test-results/
      
      - name: Publish Test Report
        uses: mikepenz/action-junit-report@v4
        if: always()
        with:
          report_paths: '**/build/test-results/test/TEST-*.xml'
          detailed_summary: true
          include_passed: true

  lint:
    name: Run Lint Checks
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle
      
      - name: Grant execute permission for gradlew
        run: chmod +x gradlew
      
      - name: Run lint
        run: ./gradlew lint
      
      - name: Upload lint results
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: lint-results
          path: '**/build/reports/lint-results-*.html'

  build:
    name: Build APK
    runs-on: ubuntu-latest
    needs: [test, lint]
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle
      
      - name: Grant execute permission for gradlew
        run: chmod +x gradlew
      
      - name: Build Debug APK
        run: ./gradlew assembleDebug --stacktrace
      
      - name: Build Release APK
        run: ./gradlew assembleRelease --stacktrace
        env:
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
      
      - name: Upload Debug APK
        uses: actions/upload-artifact@v4
        with:
          name: sleep-guardian-debug
          path: app/build/outputs/apk/debug/app-debug.apk
      
      - name: Upload Release APK
        uses: actions/upload-artifact@v4
        with:
          name: sleep-guardian-release
          path: app/build/outputs/apk/release/app-release.apk
      
      - name: Create Release
        if: github.ref == 'refs/heads/main' && github.event_name == 'push'
        uses: softprops/action-gh-release@v1
        with:
          tag_name: v${{ github.run_number }}
          name: Release ${{ github.run_number }}
          files: |
            app/build/outputs/apk/debug/app-debug.apk
            app/build/outputs/apk/release/app-release.apk
          body: |
            Automated build from commit ${{ github.sha }}
            
            **Changes:**
            ${{ github.event.head_commit.message }}
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}

  instrumentation-test:
    name: Run Instrumentation Tests
    runs-on: macos-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle
      
      - name: Grant execute permission for gradlew
        run: chmod +x gradlew
      
      - name: Run instrumentation tests
        uses: reactivecircus/android-emulator-runner@v2
        with:
          api-level: 33
          target: google_apis
          arch: x86_64
          profile: pixel_6
          script: ./gradlew connectedDebugAndroidTest
      
      - name: Upload instrumentation test results
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: instrumentation-test-results
          path: |
            **/build/reports/androidTests/
            **/build/outputs/androidTest-results/
```

---

## Signing Configuration

### Для Release APK

1. **Создай keystore локально:**

```bash
keytool -genkey -v -keystore sleep-guardian-release.keystore \
  -alias sleep-guardian \
  -keyalg RSA -keysize 2048 -validity 10000
```

2. **Закодируй keystore в base64:**

```bash
base64 sleep-guardian-release.keystore > keystore.base64
```

3. **Добавь secrets в GitHub:**

- `KEYSTORE_BASE64` - содержимое keystore.base64
- `KEYSTORE_PASSWORD` - пароль от keystore
- `KEY_ALIAS` - alias ключа
- `KEY_PASSWORD` - пароль от ключа

4. **Обнови build.gradle.kts:**

```kotlin
android {
    signingConfigs {
        create("release") {
            // Для CI/CD
            val keystorePropertiesFile = rootProject.file("keystore.properties")
            if (keystorePropertiesFile.exists()) {
                val keystoreProperties = Properties()
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
                
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            } else {
                // Для GitHub Actions
                storeFile = file("${buildDir}/release.keystore")
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

5. **Добавь step в workflow для декодирования keystore:**

```yaml
- name: Decode Keystore
  run: |
    echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > app/build/release.keystore
```

---

## Automated Testing

### Unit Tests Configuration

Добавь в `build.gradle.kts`:

```kotlin
android {
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
            
            all {
                it.useJUnitPlatform()
                it.testLogging {
                    events("passed", "skipped", "failed")
                    showStandardStreams = true
                }
            }
        }
    }
}

dependencies {
    // Unit Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("app.cash.turbine:turbine:1.0.0")
    testImplementation("com.google.truth:truth:1.1.5")
    
    // Android Testing
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("io.mockk:mockk-android:1.13.8")
}
```

### Coverage Reports

Добавь JaCoCo для coverage:

```kotlin
plugins {
    id("jacoco")
}

android {
    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    
    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/*_Hilt*.class"
    )
    
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
    classDirectories.setFrom(
        fileTree("build/tmp/kotlin-classes/debug") {
            exclude(fileFilter)
        }
    )
    executionData.setFrom(fileTree(buildDir) {
        include("jacoco/testDebugUnitTest.exec")
    })
}
```

Добавь в workflow:

```yaml
- name: Generate coverage report
  run: ./gradlew jacocoTestReport

- name: Upload coverage to Codecov
  uses: codecov/codecov-action@v3
  with:
    files: ./build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml
```

---

## Download APK from GitHub

### Вариант 1: Через GitHub Releases (автоматика)

Workflow автоматически создаёт Release при пуше в main. Ты можешь:

1. Открыть на телефоне: `https://github.com/YOUR_USERNAME/sleep-guardian/releases`
2. Скачать latest APK
3. Установить

### Вариант 2: Через GitHub Actions Artifacts

1. Открой на телефоне: `https://github.com/YOUR_USERNAME/sleep-guardian/actions`
2. Выбери последний успешный workflow
3. Scroll down → Artifacts
4. Скачай `sleep-guardian-debug.apk`

### Вариант 3: Прямая ссылка (для удобства)

Создай отдельный workflow `.github/workflows/quick-apk.yml`:

```yaml
name: Quick APK Build

on:
  workflow_dispatch:  # Ручной запуск

jobs:
  build-apk:
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle
      
      - name: Build Debug APK
        run: |
          chmod +x gradlew
          ./gradlew assembleDebug
      
      - name: Upload to Transfer.sh
        run: |
          APK_PATH=$(find app/build/outputs/apk/debug -name "*.apk" | head -1)
          UPLOAD_URL=$(curl --upload-file "$APK_PATH" https://transfer.sh/sleep-guardian.apk)
          echo "Download APK: $UPLOAD_URL"
          echo "APK_URL=$UPLOAD_URL" >> $GITHUB_ENV
      
      - name: Comment PR with download link
        if: github.event_name == 'pull_request'
        uses: actions/github-script@v6
        with:
          script: |
            github.rest.issues.createComment({
              issue_number: context.issue.number,
              owner: context.repo.owner,
              repo: context.repo.repo,
              body: `📱 **APK Ready!**\n\nDownload: ${{ env.APK_URL }}`
            })
```

Теперь можешь запустить workflow вручную через GitHub UI → Actions → Quick APK Build → Run workflow.

---

## Mobile-Friendly Setup

### QR Code для быстрого скачивания

Добавь в workflow:

```yaml
- name: Generate QR Code
  run: |
    sudo apt-get install -y qrencode
    RELEASE_URL="https://github.com/${{ github.repository }}/releases/latest"
    qrencode -o qr-code.png "$RELEASE_URL"

- name: Upload QR Code
  uses: actions/upload-artifact@v4
  with:
    name: qr-code
    path: qr-code.png
```

Теперь в Artifacts будет QR-код для скачивания.

---

## Notifications

### Telegram Bot для уведомлений

Создай Telegram бота и получи token. Добавь secret `TELEGRAM_TOKEN` и `TELEGRAM_CHAT_ID`.

```yaml
- name: Send Telegram notification
  if: success()
  uses: appleboy/telegram-action@master
  with:
    to: ${{ secrets.TELEGRAM_CHAT_ID }}
    token: ${{ secrets.TELEGRAM_TOKEN }}
    message: |
      ✅ Sleep Guardian APK готов!
      
      Commit: ${{ github.event.head_commit.message }}
      Branch: ${{ github.ref }}
      
      Download: https://github.com/${{ github.repository }}/releases/latest
    
- name: Send failure notification
  if: failure()
  uses: appleboy/telegram-action@master
  with:
    to: ${{ secrets.TELEGRAM_CHAT_ID }}
    token: ${{ secrets.TELEGRAM_TOKEN }}
    message: |
      ❌ Build failed!
      
      Commit: ${{ github.event.head_commit.message }}
      Check: https://github.com/${{ github.repository }}/actions
```

---

## Claude Code Integration

### Automatic Push Workflow

Claude Code может автоматически пушить изменения. Настрой `.claudecodeignore`:

```
# Don't commit build outputs
**/build/
**/.gradle/
*.apk
*.aab

# Don't commit secrets
keystore.properties
*.keystore
*.jks

# IDE
.idea/
*.iml
.DS_Store
```

### Git Hooks для автоматических тестов

Создай `.git/hooks/pre-push`:

```bash
#!/bin/bash

echo "Running tests before push..."
./gradlew test

if [ $? -ne 0 ]; then
    echo "Tests failed! Push aborted."
    exit 1
fi

echo "Tests passed! Pushing..."
exit 0
```

```bash
chmod +x .git/hooks/pre-push
```

---

## Branch Strategy

### Main Branches

- `main` - production-ready code, auto-deploys releases
- `develop` - integration branch for features
- `feature/*` - feature branches

### Workflow

```yaml
on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]
```

**Для feature branches:**

```yaml
on:
  push:
    branches: [ 'feature/**' ]

jobs:
  test-only:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Run tests
        run: ./gradlew test
```

---

## Caching для ускорения

### Gradle Cache

Уже включен через `cache: gradle` в setup-java, но можно усилить:

```yaml
- name: Cache Gradle packages
  uses: actions/cache@v3
  with:
    path: |
      ~/.gradle/caches
      ~/.gradle/wrapper
    key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
    restore-keys: |
      ${{ runner.os }}-gradle-

- name: Cache build outputs
  uses: actions/cache@v3
  with:
    path: |
      **/build
    key: ${{ runner.os }}-build-${{ github.sha }}
    restore-keys: |
      ${{ runner.os }}-build-
```

---

## Version Management

### Automatic Version Bumping

Создай `.github/workflows/version-bump.yml`:

```yaml
name: Auto Version Bump

on:
  push:
    branches: [ main ]

jobs:
  version-bump:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v4
        with:
          token: ${{ secrets.GITHUB_TOKEN }}
      
      - name: Bump version
        id: bump
        run: |
          # Read current version
          CURRENT_VERSION=$(grep "versionCode" app/build.gradle.kts | grep -o '[0-9]\+')
          NEW_VERSION=$((CURRENT_VERSION + 1))
          
          # Update versionCode
          sed -i "s/versionCode = $CURRENT_VERSION/versionCode = $NEW_VERSION/" app/build.gradle.kts
          
          # Update versionName
          CURRENT_NAME=$(grep "versionName" app/build.gradle.kts | grep -o '"[^"]*"')
          # Simple semantic versioning: increment patch
          
          echo "new_version=$NEW_VERSION" >> $GITHUB_OUTPUT
      
      - name: Commit version bump
        run: |
          git config user.name "GitHub Actions"
          git config user.email "actions@github.com"
          git add app/build.gradle.kts
          git commit -m "chore: bump version to ${{ steps.bump.outputs.new_version }}"
          git push
```

---

## Complete CI/CD Flow

**Итоговый flow:**

1. **Локальная разработка:**
   - Claude Code работает над фичей
   - Локально запускаются unit-тесты (pre-commit hook)

2. **Push в GitHub:**
   - Claude Code пушит в `feature/mvp-1.1`
   - GitHub Actions запускает тесты и lint

3. **Pull Request в develop:**
   - Создаёшь PR через GitHub UI (можно с телефона)
   - CI прогоняет полный набор тестов
   - Если всё ОК → merge

4. **Merge в main:**
   - Автоматически создаётся Release
   - APK доступен для скачивания
   - Telegram уведомление приходит
   - Version bump автоматически

5. **Скачивание на телефон:**
   - Открываешь GitHub Releases на телефоне
   - Скачиваешь APK
   - Устанавливаешь

---

## Troubleshooting

### Build fails с OutOfMemoryError

Создай `gradle.properties`:

```properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=512m -XX:+HeapDumpOnOutOfMemoryError
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.caching=true
android.useAndroidX=true
kotlin.code.style=official
```

### APK не устанавливается на телефон

1. Включи "Установка из неизвестных источников"
2. Скачай Debug APK вместо Release (не требует подписи)

### GitHub Actions quota exceeded

Free tier даёт 2000 минут/месяц. Оптимизируй:

```yaml
# Запускай instrumentation тесты только на PR в main
instrumentation-test:
  if: github.event_name == 'pull_request' && github.base_ref == 'main'
```

---

## Recommended GitHub Secrets

```
KEYSTORE_BASE64          # Base64 encoded keystore
KEYSTORE_PASSWORD        # Keystore password
KEY_ALIAS               # Key alias
KEY_PASSWORD            # Key password
TELEGRAM_TOKEN          # Telegram bot token (optional)
TELEGRAM_CHAT_ID        # Your Telegram chat ID (optional)
CLAUDE_API_KEY          # For AI features (optional, можно хранить локально)
```

---

## Commands Reference

### Локальные команды

```bash
# Build debug APK
./gradlew assembleDebug

# Run tests
./gradlew test

# Run tests with coverage
./gradlew jacocoTestReport

# Install on connected device
./gradlew installDebug

# Run lint
./gradlew lint

# Clean build
./gradlew clean build
```

### GitHub CLI команды (с телефона через Termux)

```bash
# Install GitHub CLI in Termux
pkg install gh

# Login
gh auth login

# View latest release
gh release view --repo YOUR_USERNAME/sleep-guardian

# Download latest APK
gh release download --repo YOUR_USERNAME/sleep-guardian --pattern "*.apk"

# Trigger workflow manually
gh workflow run quick-apk.yml
```

---

## Monitoring

### GitHub Actions Dashboard

Добавь в README.md badges:

```markdown
[![Android CI](https://github.com/YOUR_USERNAME/sleep-guardian/actions/workflows/android-build.yml/badge.svg)](https://github.com/YOUR_USERNAME/sleep-guardian/actions)
[![codecov](https://codecov.io/gh/YOUR_USERNAME/sleep-guardian/branch/main/graph/badge.svg)](https://codecov.io/gh/YOUR_USERNAME/sleep-guardian)
```

---

Всё готово! Теперь у тебя полноценный CI/CD pipeline:
- ✅ Автоматические тесты при каждом коммите
- ✅ APK доступен для скачивания с телефона
- ✅ Уведомления в Telegram
- ✅ Version bumping
- ✅ Code coverage tracking

**Workflow с Claude Code:**
1. Даёшь задачу Claude Code с телефона
2. Он пишет код и пушит в GitHub
3. Через 5-10 минут получаешь уведомление с готовым APK
4. Скачиваешь и тестируешь

Идеально для разработки в пути! 🚀
