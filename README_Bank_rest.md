# Система управления банковскими картами

Система управления банковскими картами (REST API)
Обзор
Bankcards - backend-приложение на Spring Boot для управления банковскими картами предоставляет REST API для аутентификации пользователей, управления картами (создание, просмотр, блокировка, удаление) и выполнения переводов между картами. Приложение использует Spring Security с JWT для аутентификации и авторизации, а данные хранятся в реляционной базе данных (PostgreSQL/MySQL) с миграциями через Liquibase. Все номера карт шифруются, а в ответах API отображаются в замаскированном виде (например, **** **** **** 1234). API документировано с помощью Swagger/OpenAPI.

Основные компоненты
Контроллеры

AuthController: Отвечает за регистрацию и аутентификацию пользователей.
Эндпоинты:
POST /auth/register — регистрация нового пользователя.
POST /auth/login — аутентификация с возвратом JWT-токена.

CardController: Управление картами пользователя.
Эндпоинты:
POST /cards — создание новой карты.
GET /cards — получение списка карт пользователя (с пагинацией).
GET /cards/{id} — получение информации о карте по ID.
POST /cards/{id}/block — блокировка карты.
DELETE /cards/{id} — удаление карты.

TransferController: Выполнение переводов между картами.
Эндпоинт:
POST /transfers — перевод средств между картами пользователя.

Сервисы
AuthService: Логика регистрации и аутентификации пользователей.
CardService: Бизнес-логика для операций с картами (создание, просмотр, блокировка, удаление). Шифрует номера карт с использованием AES.
TransferService: Логика переводов между картами, включая валидацию баланса и статуса карт.
UserDetailsServiceImpl: Реализация UserDetailsService для загрузки данных пользователя из базы данных и предоставления информации об аутентификации (имя пользователя, пароль, роли) для Spring Security.

Безопасность

Spring Security с JWT для аутентификации и авторизации.
Роли: USER (доступ к своим картам и переводам) и ADMIN (доступ ко всем операциям, включая управление пользователями).
Номера карт шифруются с использованием AES (см. CardServiceTest).
Эндпоинты защищены фильтром JwtAuthenticationFilter.

Репозитории

CardRepository: Работа с сущностью Card в базе данных.
UserRepository: Работа с сущностью User.

DTO

AuthRequest, AuthResponse, RegisterRequest: Для операций аутентификации.
CardCreateDto, CardViewDto: Для создания и отображения карт.
TransferRequest: Для операций перевода.

REST API
Аутентификация

Регистрация:
Эндпоинт: POST /auth/register
Тело запроса: JSON (RegisterRequest с полями username, password)
Ответ: HTTP 200, сообщение "User registered successfully".
Тест: AuthControllerTest.register_success


Логин:
Эндпоинт: POST /auth/login
Тело запроса: JSON (AuthRequest с полями username, password)
Ответ: HTTP 200, JSON с полем token (JWT).
Тест: AuthControllerTest.login_success



Управление картами

Создание карты:
Эндпоинт: POST /cards
Тело запроса: JSON (CardCreateDto с полями cardNumber, owner, expiryDate, balance)
Ответ: HTTP 200, JSON (CardViewDto с замаскированным номером карты).
Тест: CardControllerTest.createCard


Получение списка карт:
Эндпоинт: GET /cards?page={page}&size={size}
Параметры: page (номер страницы), size (размер страницы)
Ответ: HTTP 200, JSON с пагинированным списком CardViewDto.
Тест: CardControllerTest.getUserCards


Получение карты по ID:
Эндпоинт: GET /cards/{id}
Ответ: HTTP 200, JSON (CardViewDto).
Тест: CardControllerTest.getCard


Блокировка карты:
Эндпоинт: POST /cards/{id}/block
Ответ: HTTP 200, JSON (CardViewDto).
Тест: CardControllerTest.blockCard


Удаление карты:
Эндпоинт: DELETE /cards/{id}
Ответ: HTTP 204 (No Content).
Тест: CardControllerTest.deleteCard



Переводы

Перевод между картами:
Эндпоинт: POST /transfers
Тело запроса: JSON (TransferRequest с полями fromCardId, toCardId, amount)
Ответ: HTTP 200.
Тест: TransferControllerTest.transfer_success



Технологии

Java 17+
Spring Boot: Основа приложения.
Spring Security: Аутентификация и авторизация с JWT.
Spring Data JPA: Работа с базой данных.
PostgreSQL/MySQL: Хранилище данных.
Liquibase: Управление миграциями БД (src/main/resources/db/migration).
Swagger/OpenAPI: Документация API (docs/openapi.yaml).
JUnit 5, Mockito, Spring Boot Test: Тестирование.
Docker: Для развертывания dev-среды.

Тестирование
Тесты покрывают ключевую бизнес-логику и REST API:
Контроллеры: Проверяют HTTP-запросы и ответы с помощью MockMvc (см. AuthControllerTest, CardControllerTest, TransferControllerTest).
Сервисы: Проверяют бизнес-логику с использованием моков (CardServiceTest, TransferServiceTest).
Запуск тестов:mvn clean package

Установка и запуск
Требования:
Java 21
Maven
PostgreSQL
Docker (для dev-среды)


Настройка базы данных:

Создайте базу данных (например, bankcards).
Настройте подключение в application.properties:spring.datasource.url=jdbc:postgresql://localhost:5432/bankcards
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=none
spring.liquibase.change-log=classpath:db/migration/changelog.xml




Сборка и запуск:
mvn clean install
mvn spring-boot:run

или с использованием Docker Compose:
docker-compose up


Документация API:

После запуска приложение доступно по адресу http://localhost:8080.
Swagger UI: http://localhost:8080/swagger-ui.html.



Безопасность

Номера карт шифруются с использованием AES (см. CardServiceTest).
Доступ к эндпоинтам ограничен ролями (USER, ADMIN) через JwtAuthenticationFilter.
Номера карт возвращаются в замаскированном виде (**** **** **** 1234).

