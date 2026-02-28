# Задачи, которые нужно сделать вручную

Этот файл содержит задачи, которые AI не может выполнить и которые требуют участия человека.

---

## Критически важные (до начала разработки)

### 1. Логотип и иконка приложения
- [ ] Создать логотип Sleep Guardian
- [ ] Адаптивная иконка для Android (foreground + background layer)
- [ ] Размеры: mdpi (48x48), hdpi (72x72), xhdpi (96x96), xxhdpi (144x144), xxxhdpi (192x192)
- [ ] Формат: PNG или векторный drawable
- [ ] Стиль: минималистичный, ассоциации с будильником/сном
- **Куда положить:** `app/src/main/res/mipmap-*/ic_launcher.png` + `ic_launcher_round.png`

### 2. GitHub Repository Setup
- [ ] Создать репозиторий на GitHub (если ещё не создан)
- [ ] Настроить branch protection rules для `main`
- [ ] Добавить GitHub Secrets:
  - `KEYSTORE_BASE64`
  - `KEYSTORE_PASSWORD`
  - `KEY_ALIAS`
  - `KEY_PASSWORD`
  - `TELEGRAM_TOKEN` (опционально)
  - `TELEGRAM_CHAT_ID` (опционально)

### 3. Signing Keystore
- [ ] Сгенерировать release keystore:
  ```bash
  keytool -genkey -v -keystore sleep-guardian-release.keystore \
    -alias sleep-guardian -keyalg RSA -keysize 2048 -validity 10000
  ```
- [ ] Сохранить keystore и пароли в безопасном месте
- [ ] Закодировать в base64 для GitHub Secrets

---

## Перед MVP 1.5 (QR-код задание)

### 4. QR-коды для тестирования
- [ ] Распечатать/наклеить QR-код в месте, куда нужно дойти утром (ванная, кухня)
- [ ] Для тестирования: любой QR-код подойдёт

---

## Перед Iteration 4.3 (Sleep Sounds)

### 5. Аудио-файлы для сна
- [ ] Найти бесплатные звуки (CC0/Public Domain):
  - White noise
  - Brown noise
  - Pink noise
  - Дождь
  - Океан
  - Лес
- [ ] Рекомендуемые источники: freesound.org, pixabay.com/sound-effects
- [ ] Формат: MP3, ~1-2 минуты loop, 128kbps
- **Куда положить:** `app/src/main/assets/sounds/`

---

## Перед Iteration 5.2 (Claude API)

### 6. Claude API Key
- [ ] Получить API key на console.anthropic.com
- [ ] Пополнить баланс (AI-задания стоят ~$0.01-0.05 за вопрос)

---

## Перед публикацией

### 7. Google Play Console (опционально)
- [ ] Зарегистрировать аккаунт разработчика ($25)
- [ ] Создать listing:
  - Название: Sleep Guardian
  - Описание (короткое и полное)
  - Скриншоты (минимум 2)
  - Feature graphic (1024x500)
  - Категория: Health & Fitness или Productivity
- [ ] Privacy Policy (можно сгенерировать)

### 8. Splash Screen / Onboarding
- [ ] Графика для splash screen (опционально — можно логотип)
- [ ] Иллюстрации для onboarding-экранов (опционально)

---

## Тестирование на устройствах

### 9. Тестирование будильника
- [ ] Установить APK на реальное устройство
- [ ] Проверить срабатывание на lock screen
- [ ] Проверить после перезагрузки
- [ ] Проверить при включённом Battery Saver
- [ ] Проверить на разных производителях (Samsung, Xiaomi — у них агрессивный power management)

### 10. Разрешения battery optimization
- [ ] На Samsung: Settings → Battery → App power management → Sleep Guardian → "Unrestricted"
- [ ] На Xiaomi: Settings → Battery → App Battery Saver → Sleep Guardian → "No restrictions"
- [ ] На Huawei: Settings → Battery → App Launch → Sleep Guardian → "Manage manually" → все toggle ON

---

## Периодические задачи

### 11. Telegram Bot для уведомлений (опционально)
- [ ] Создать бота через @BotFather
- [ ] Получить token
- [ ] Получить chat_id (отправить боту сообщение, потом запросить updates)
- [ ] Добавить в GitHub Secrets

---

## Заметки

- Задачи отсортированы по порядку их необходимости
- Задачи с пометкой "опционально" можно пропустить или отложить
- AI может помочь с генерацией текстов (описания, privacy policy), но не с графикой и настройкой аккаунтов
