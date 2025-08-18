# LibraryManager API - Snelstartgids

Welkom bij de LibraryManager back-end API. Deze API is gebouwd met Spring Boot en Java 17 en beheert een bibliotheekcollectie, inclusief boeken, leningen en gebruikers. Het maakt gebruik van een PostgreSQL-database voor dataopslag en is beveiligd met Spring Security en JWT voor authenticatie en autorisatie.

### Vereisten

Zorg ervoor dat de volgende software is geïnstalleerd en geconfigureerd:
* Java Development Kit (JDK) 17 of hoger 
* Apache Maven 
* PostgreSQL-database

### Installatie en opstarten

1. **Kloon de repository:**

`Bash`
`git clone https://github.com/MarjolijnMuller/LibraryManager.git
cd LibraryManager`

2. **Databaseconfiguratie:**

* Start je PostgreSQL-database. 
* Maak een database aan met de naam LibraryManager. 
* Stel de omgevingsvariabelen POSTGRESQL_USERNAME en POSTGRESQL_PASSWORD in met je database-inloggegevens, of pas deze aan in het bestand `src/main/resources/application.properties`.

3. **Applicatie starten:**

`Bash
mvn spring-boot:run`

De API is nu beschikbaar op `http://localhost:8080`.

### Kernfunctionaliteiten
**Authenticatie:** Beveiligde endpoints via JWT-token.
**Gebruikersbeheer:** Verschillende rollen (ADMIN, LIBRARIAN, MEMBER) met verschillende rechten.
**CRUD-operaties:** Beheer van boeken, leningen en boetes.
**Bestandsopslag:** Mogelijkheid om profielfoto's te uploaden.

### API-endpoints
De API-endpoints zijn beveiligd en vereisen een JWT-token in de Authorization header.
* POST /auth - Genereert een JWT-token voor de ingelogde gebruiker. 
* GET /profiles/user - Bekijk het profiel van de ingelogde gebruiker.
* GET /books/{bookId} - Zoek een specifiek boek. 
* POST /loans - Maak een nieuwe lening aan.

### Volledige documentatie
Voor een volledige lijst van endpoints, testgebruikers en gedetailleerde instructies, raadpleeg de `Installatiehandleiding.pdf` in de root van de repository.