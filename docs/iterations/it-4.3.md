# Iteration 4.3: Sleep Sounds + Morning Check-in

**Итерация:** 4 (Вечерний режим + Ритуалы)
**Срок:** 5-6 дней
**Статус:** Не начата

## Цель

Звуки для засыпания и утренний опрос качества сна для корреляции в аналитике.

## Задачи

### Sleep Sounds Player
- [ ] Набор звуков: white noise, brown noise, pink noise, rain, ocean, forest
- [ ] MediaPlayer с loop
- [ ] Volume control (slider)
- [ ] Auto-stop timer: 30мин / 1ч / 2ч / бесконечно
- [ ] Background playback через Foreground Service
- [ ] Notification с controls (play/pause/stop)
- [ ] Микширование 2-3 звуков одновременно (опционально)

### DND Integration (опционально)
- [ ] Системный Do Not Disturb API
- [ ] Автоматическое включение DND при старте Bedtime Mode
- [ ] Автоматическое выключение при пробуждении

### Morning Check-in
- [ ] Экран после отключения будильника:
  - "Как выспался?" — шкала 1-5 (emoji)
  - Опциональная заметка (TextField)
  - Кнопка "Пропустить"
- [ ] Сохранение в AlarmLog: `sleepQuality`, `notes`
- [ ] Корреляция в статистике: качество сна vs длительность

### Утренняя цитата
- [ ] База мотивационных цитат (50+ штук)
- [ ] Отображение на Morning Check-in экране
- [ ] Рандомизация без повторов

## Audio Assets

Звуки нужно либо найти бесплатные (CC0), либо сгенерировать.

```
app/src/main/assets/sounds/
├── white_noise.mp3
├── brown_noise.mp3
├── pink_noise.mp3
├── rain.mp3
├── ocean.mp3
└── forest.mp3
```

## Критерии завершения

- Звуки играют в фоне и останавливаются по таймеру
- Morning check-in появляется после будильника
- Качество сна записывается и отображается в статистике
- Утренняя цитата показывается

## Зависимости

- IT-4.1 (Bedtime Mode)
- IT-3.3 (статистика для корреляции)

## Риски

- Размер APK из-за аудио файлов — использовать сжатие, может быть скачивание по запросу
- Foreground Service для аудио — корректная реализация lifecycle
