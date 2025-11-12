# Проект стажировки GlobusLabs

## Описание

Проект представляет из себя монолитное Spring Boot приложение. Есть две основные сущности:
- Пользователь (User)
- Банковский счёт (BankAccount)

Приложение использует Spring Boot, Spring Data JPA, Liquibase для миграций, OpenFeign для внешних HTTP вызовов и MapStruct для маппинга DTO.

## Требования

- Java 21
- База данных PostgreSQL (локально для интеграционных тестов или CI можно использовать Testcontainers)
- Git (для работы с репозиторием и CI)

Для запуска сборки и тестов локально достаточно установленной JDK 21 и доступа к интернету

## Быстрый старт (локально)

1. Клонируйте репозиторий:

```bash
git clone <репозиторий>
cd GlobusLabs
```

2. Скопируйте .env файл

```bash
cp .env.example .env
```

3. Запуск (с помощью докера)

```bash
docker compose --env-file .env build
docker compose --env-file .env up -d
```

4. Тестирование с помощью Postman или Swagger UI

```
http://localhost:8080/swagger-ui/index.html#/
```

## Структура проекта (ключевые директории)

- `src/main/java` — исходный код приложения
  - `ru.globus` — основной пакет
    - `controller` — REST контроллеры
    - `service` — бизнес-логика
    - `repository` — Spring Data репозитории
    - `model` — JPA сущности и перечисления
    - `dto` — объекты передачи данных
    - `mapper` — MapStruct мапперы
    - `config` — конфигурация приложения
    - `util` — утилитарные классы
- `src/main/resources` — ресурсы приложения (включая `application.yml` и миграции Liquibase)
- `src/test/java` — модульные и интеграционные тесты
