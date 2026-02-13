# Iteration 5.2: Claude API Integration + Smart Content

**Итерация:** 5 (Gamification + Advanced Features)
**Срок:** 6-8 дней
**Статус:** Не начата

## Цель

Интеграция с Claude API: AI-задания, персонализированные утренние цитаты, умные инсайты.

## Задачи

### Claude API Setup
- [ ] Retrofit + OkHttp настройка
- [ ] `ClaudeApiService` — Retrofit interface
- [ ] `AuthInterceptor` — добавление API key в заголовки
- [ ] `EncryptedSharedPreferences` для хранения API key
- [ ] Settings экран: ввод API key
- [ ] Error handling: таймауты, rate limits, network errors
- [ ] Retry logic с exponential backoff

### AI Question Task
- [ ] Новый `TaskType.AI_QUESTION`
- [ ] Генерация вопросов через API:
  - Общие знания ("Столица Перу?")
  - Логические задачи
  - Интересные факты с вопросом
- [ ] Проверка ответа через API (нечёткое сравнение)
- [ ] Кэширование: предзагрузка 5-10 вопросов в offline
- [ ] Fallback: если API недоступен → переключение на Math/другое задание
- [ ] Rate limiting: max N запросов в день

### Smart Morning Quotes
- [ ] Персонализированная цитата на основе статистики:
  - "Ты спал 7.5ч — отлично! Твой стрик: 12 дней."
  - "Сегодня понедельник — обычно тебе сложнее, но ты справишься!"
- [ ] Offline cache: сохранение последних 10 цитат
- [ ] Fallback: локальная база цитат если API down

### AI Insights (опционально)
- [ ] Анализ паттернов сна через API
- [ ] Персональные рекомендации
- [ ] "Умная" интерпретация статистики

## API Integration

```kotlin
interface ClaudeApiService {
    @POST("/v1/messages")
    suspend fun sendMessage(
        @Header("x-api-key") apiKey: String,
        @Header("anthropic-version") version: String = "2023-06-01",
        @Body request: MessageRequest
    ): MessageResponse
}
```

## Критерии завершения

- AI-задания генерируются и проверяются через API
- Graceful degradation при проблемах с сетью
- API key хранится в encrypted storage
- Кэширование работает для offline
- Утренние цитаты персонализированы

## Зависимости

- MVP 1.4 (Task Engine для нового типа задания)
- IT-3.3 (статистика для персонализации)

## Риски

- API costs — ограничить количество запросов
- Latency — предзагрузка вопросов заранее
- API downtime — обязательный fallback
