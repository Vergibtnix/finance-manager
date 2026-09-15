# Kassenbuch / Finance Manager
![img.png](img.png)
## Technologien

- Backend
    - **Java 21** 
    - **Spring Boot 4.1**
    - **Spring MVC**
    - **Spring Data JPA**
    - **Maven**

- DB
  - **PostgreSQL 16**
  - **Docker / Docker Compose**

- Frontend
    - **Thymeleaf**
    - **Bootstrap 5**
    - **Dark Mode mit Persistenz via localStorage**


## Features

### Buchungen Verwalten
- Neue buchungen erfassen(Datum, Betrag, Typ, Katigorie, Beschreibung(Optional))

![img_2.png](img_2.png)

- Buchungen **bearbeiten** 

![img_3.png](img_3.png)

- Buchungen **löschen**

![img_4.png](img_4.png)

- Farben für Einnahmen (Grün) und Ausgaben (Rot)

![img_5.png](img_5.png)

- Vordefinierte Katigorien:
  - Gehalt
  - Miete
  - Lebensmittel
  - KFZ-Kosten
  - Freizeit
  - Gesundheit
  - Sonstiges

![img_6.png](img_6.png)

###  Statistik‑Dashboard
- Einnahmen‑Summe
- Ausgaben‑Summe
- Kontostand

v


### Navigation
- Moderne Bootstrap‑Navbar
- Aktiver Menüpunkt wird automatisch hervorgehoben
- Integrierter Dark‑Mode‑Schalter
- Links zu:
    - Buchungen
    - Neue Buchung
    - Statistik

![img_7.png](img_7.png)

### Responsive Design
- Vollständig responsive dank Bootstrap 5
- Mobile‑freundliche Navigation
- Tabellen skalieren automatisch
- Dashboard passt sich an alle Bildschirmgrößen an

## PostgreSQL mit Docker starten

1. PostgreSQL und pgAdmin starten:

```powershell
docker compose up -d
```

2. Optional den Status prüfen:

```powershell
docker compose ps
```

3. Spring Boot App starten (nutzt PostgreSQL auf `localhost:5433`):

```powershell
.\mvnw.cmd spring-boot:run
```

4. Container stoppen:

```powershell
docker compose down
```

Wenn du das Datenbank-Volume komplett neu initialisieren willst, z. B. nach einer Schema-Änderung:

```powershell
docker compose down -v
docker compose up -d
```

### Verwendete DB-Konfiguration

- URL: `jdbc:postgresql://localhost:5433/finance_db`
- User: `finance_user`
- Passwort: `finance_password`
- pgAdmin: `http://localhost:5050`
  - E-Mail: `admin@finance-manager.local`
  - Passwort: `admin123`

Die Spring-Konfiguration in `src/main/resources/application.properties` verwendet Umgebungsvariablen mit sinnvollen Defaults:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

Hinweis: Das Init-Skript liegt unter `docker/init/01-init.sql` und wird nur beim ersten Start mit leerem Docker-Volume ausgeführt.
Wenn `5433` oder `5050` bereits belegt ist, passe das Port-Mapping in `docker-compose.yml` an.

## Dark Mode

- Der Dark Mode ist auf allen Seiten verfügbar.
- Die Einstellung bleibt nach einem Reload im Browser gespeichert.
- Der Umschalter sitzt rechts in der Navbar.
