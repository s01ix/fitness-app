# Fitness App – system obsługi sieci klubów fitness

Aplikacja desktopowa klient–serwer do obsługi klubów fitness: sprzedaż karnetów, kampanie promocyjne, zajęcia grupowe, plany treningowe i reklamacje. Projekt zespołowy realizowany na Politechnice Świętokrzyskiej.

**Stack:** Java 21 · JavaFX · gniazda TCP (własny protokół tekstowy) · JDBC · Oracle Database · JUnit 5 + Mockito

> Interfejs JavaFX jest prosty – służy głównie do pokazania operacji na danych. Najważniejsza część projektu to model danych, logika biznesowa po stronie serwera i warstwa dostępu do bazy.

## Architektura

```
Klient JavaFX (widoki per rola)  ──TCP:5000──►  FitnessServer
                                                  ├─ ReceptionistCommandHandler
                                                  ├─ TrainerCommandHandler
                                                  ├─ ManagerCommandHandler
                                                  └─ Club/EquipmentCommandHandler
                                                         │
                                                  DAO (JDBC, PreparedStatement)
                                                         │
                                                   Oracle Database
```

Każda rola ma osobny widok w kliencie i osobny zestaw poleceń na serwerze, np. recepcja: `SELL_PASS`, `CREATE_RESERVATION`, `VERIFY_ENTRY`, `LOG_COMPLAINT`; trener: `CREATE_PLAN`, `ADD_PLAN_ITEM`, `SEND_MESSAGE`; manager: `ADD_CAMPAIGN`, `ADD_DISCOUNT`, `GET_MGR_STATS`.

## Model danych

Schemat: [`src/main/resources/db/schema.sql`](src/main/resources/db/schema.sql), diagram: [`schema.pdf`](src/main/resources/db/schema.pdf).

16 tabel pogrupowanych wokół procesów klubu:

| Obszar | Tabele |
|---|---|
| Użytkownicy i role | `gym_user` (CLIENT, TRAINER, RECEPTIONIST, MANAGER, ADMIN) |
| Kluby i sprzęt | `club`, `equipment` (OPERATIONAL / MAINTENANCE / BROKEN) |
| Sprzedaż | `pass_type`, `gym_pass` (ACTIVE / EXPIRED / SUSPENDED / FROZEN), `payment` (PENDING / COMPLETED / FAILED / REFUNDED; CASH, CARD, TRANSFER, BLIK) |
| Marketing | `promo_campaign`, `discount` (PERCENT / AMOUNT), `campaign_pass` |
| Zajęcia | `group_class`, `reservation` |
| Trening | `exercise_dict`, `training_plan`, `plan_item`, `message` |
| Obsługa klienta | `complaint` (OPEN / IN_PROGRESS / RESOLVED / REJECTED) |

Reguły biznesowe są wymuszane w bazie: dozwolone statusy i typy przez ograniczenia `CHECK`, unikalność e-maila i PESEL-u, powiązania przez klucze obce. Kwoty przechowywane jako `NUMBER(10,2)` i obsługiwane w Javie przez `BigDecimal`.

**Sprzedaż karnetu jest transakcyjna:** karnet i płatność zapisywane są w jednej transakcji JDBC (`setAutoCommit(false)` → `commit` / `rollback`), więc nie powstanie karnet bez płatności ani płatność bez karnetu (`GymPassDaoJdbc.purchasePass`).

## Uruchomienie

**Wymagania:** JDK 21, Maven, Oracle Database (domyślnie `localhost:1522/FREEPDB1`, użytkownik `system` / hasło `oracle` – tylko lokalnie, zob. `DatabaseConfig`).

```bash
# 1. utwórz schemat bazy
sqlplus system/oracle@//localhost:1522/FREEPDB1 @src/main/resources/db/schema.sql

# 2. uruchom serwer (klasa com.example.fitnessapp.server.FitnessServer, port 5000) z IDE

# 3. uruchom klienta
./mvnw javafx:run

# testy
./mvnw test
```

## Mój wkład (Michał Lesiak)

- Inicjalizacja projektu, konfiguracja połączenia z bazą i **projekt schematu bazy danych** (16 tabel, ograniczenia, relacje).
- Modele i DAO dla sprzedaży i marketingu: typy karnetów, karnety, kampanie promocyjne, rabaty, karnety kampanijne; transakcyjny zakup karnetu.
- Podział aplikacji na osobne widoki dla każdej roli; zarządzanie klubami i sprzętem.
- Funkcje recepcji (sprzedaż karnetu, zapisy na zajęcia grupowe) i trenera (ćwiczenia, plany treningowe, wiadomości).
- Testy jednostkowe trenera i dodatkowe testy.

Pozostali członkowie zespołu: Jakub Żyła, Kamil Korzeniowski, Bartłomiej Sztandera.

## Znane ograniczenia i co bym poprawił

1. **Hasła** są porównywane i przechowywane jawnym tekstem (mimo nazwy kolumny `password_hash`) – do zmiany na BCrypt.
2. **PESEL** jest daną wrażliwą i powinien być szyfrowany lub maskowany.
3. **Dane dostępowe do bazy** są w kodzie (`DatabaseConfig`) – powinny trafić do zmiennych środowiskowych.
4. **Protokół tekstowy** między klientem a serwerem nie ma uwierzytelnienia per żądanie ani szyfrowania; w wersji produkcyjnej zastąpiłbym go REST API z tokenami.
5. **Interfejs** JavaFX jest podstawowy i nie wszystkie widoki są dopracowane.
