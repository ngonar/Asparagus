# Asparagus

![Asparagus](Asparagus.jpg)

**Asparagus** is a lightweight, high-performance Java REST API Framework built on top of the **Spark Java** microframework. Create your nutritious API with Asparagus — cook with your skill and add any ingredients available in your head!

---

## 🚀 Key Features & Technology Stack

- **REST API Microframework**: Powered by [Spark Java 2.9.4](https://sparkjava.com/).
- **Persistence & Database**: Integrated with [Hibernate 5.6 ORM](https://hibernate.org/orm/) and [SQLite](https://www.sqlite.org/) (`asparagus.db`). Includes automated DDL schema generation and database seeding (`com.util.DatabaseInitializer`).
- **Build System**: Built with **Gradle 8.14** and compatible with **Java 17+ / Java 21**.
- **Logging**: Enterprise logging via **Log4j 2** (`log4j-1.2-api` bridge).
- **Messaging**: Integrated with [RabbitMQ AMQP Client 5.25](https://www.rabbitmq.com/).
- **Caching**: Memcached support via `spymemcached`.
- **Data Serialization**: JSON processing with **Google Gson** & XML handling with **JAXB**.

---

## 🛠️ Prerequisites

- **Java JDK**: Version 17 or higher (tested with OpenJDK 21).
- **Gradle**: Included via the Gradle Wrapper (`./gradlew`).

---

## 🏃 Getting Started

### 1. Build the Project

Build the application and package the JAR file:
```bash
./gradlew build
```

### 2. Initialize the SQLite Database

Run the database initialization task to create the `asparagus.db` SQLite database and seed initial data:
```bash
./gradlew initDb
```

### 3. Run the Application

You can execute the built JAR directly:
```bash
java -jar build/libs/Asparagus.jar
```
Or run via Gradle:
```bash
./gradlew run
```

---

## ⚙️ Configuration Files

Configuration files are located in the `config/` directory:

- **`config/settings.xml`**: Port settings, thread pools, timeouts, salt, and Memcached connection settings.
- **`config/switch.xml`**: Switching and route service mappings.
- **`config/whitelist.xml`**: Authorized IP / partner whitelist settings.

---

<br />

Salam olahraga,<br />
**IS**