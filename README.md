# Hocus Coquus

Cookbook web application developed for IT 326.

## Project Structure

The Spring Boot application lives at the repository root. Its Thymeleaf templates
and static assets are in `src/main/resources`, and are served by the application.

## Requirements

- Java 25
- Maven Wrapper included

## Running the Application

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS or Linux:

```sh
./mvnw spring-boot:run
```

The application imports database settings from a root-level `.env` file, if
present. Keep credentials out of version control.

## Testing

On Windows, run `.\mvnw.cmd test`. On macOS or Linux, run `./mvnw test`.
