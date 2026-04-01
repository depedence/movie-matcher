# 🎬 Movie Matcher

Movie Matcher — это MVP веб-приложения для подбора фильмов по принципу **Tinder-свайпов**.
Пользователь получает небольшую ленту фильмов, свайпает **LIKE / DISLIKE**, а система постепенно собирает информацию о его вкусах.

Проект сделан как **pet-project для практики backend разработки**.

---

# 🚀 Возможности MVP

* Получение фильмов из внешнего API (**OMDb**)
* Генерация случайной ленты фильмов
* Свайпы пользователей (**LIKE / DISLIKE**)
* Сохранение реакций пользователя в БД
* Получение истории свайпов
* Получение списка лайкнутых фильмов

---

# 🧱 Архитектура

Backend построен на:

* **Java 21**
* **Spring Boot**
* **Spring Web**
* **Spring Data JPA**
* **PostgreSQL**
* **OMDb API**

Структура проекта:

```
src/main/java/movie/matcher/ru

controller     → REST API
service        → бизнес логика
repository     → доступ к БД
entity         → JPA сущности
dto            → модели для API
omdb           → клиент для OMDb API
mapper         → маппинг моделей
```

---

# ⚙️ Настройка проекта

## 1️⃣ Клонировать репозиторий

```
git clone https://github.com/your-username/movie-matcher.git
```

---

## 2️⃣ Создать базу данных

Пример PostgreSQL:

```
CREATE DATABASE movie_matcher;
```

---

## 3️⃣ Настроить `application.yml`

Файл:

```
src/main/resources/application.yml
```

Пример конфигурации:

```yaml
omdb:
  apiKey: jhbxrcv3
  url: http://www.omdbapi.com

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/movie-matcher
    username: user
    password: password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

Получить API ключ можно здесь:

https://www.omdbapi.com/apikey.aspx

---

MIT
