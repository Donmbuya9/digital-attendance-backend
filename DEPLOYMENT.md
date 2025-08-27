# Deployment Guide: Digital Attendance System (Backend)

This document provides a complete guide for deploying the Digital Attendance System backend on a Windows machine using XAMPP for the database.

**Version:** 1.0
**Stack:** Java 21+ (Spring Boot), Maven, MySQL (via XAMPP)

---

## 1. Prerequisites

Before you begin, ensure the following software is installed on the deployment server:

1.  **Java Development Kit (JDK):** Version 21 or newer. You can verify your installation by running `java --version` in the command prompt.
2.  **Apache Maven:** The project's build tool. You can verify by running `mvn --version`.
3.  **XAMPP:** Provides the Apache and MySQL database services. Install it from the [official website](https://www.apachefriends.org/).
4.  **Git:** For cloning the project repository.

---

## 2. Step 1: Database Setup with XAMPP

The application requires a MySQL database. We will use the one provided by XAMPP.

1.  **Start XAMPP Services:**
    *   Open the **XAMPP Control Panel**.
    *   Start the **Apache** and **MySQL** services.

2.  **Create the Database:**
    *   In the XAMPP Control Panel, click the **"Admin"** button next to the MySQL service. This will open phpMyAdmin in your browser.
    *   Click on the **"Databases"** tab.
    *   Under "Create database", enter the exact name: `attendance_db`.
    *   Ensure the collation is set to `utf8mb4_unicode_ci` or a similar UTF-8 standard.
    *   Click **"Create"**.

3.  **Database Credentials:**
    *   By default, XAMPP's MySQL database uses the following credentials:
        *   **Host:** `localhost`
        *   **Port:** `3306`
        *   **Username:** `root`
        *   **Password:** (empty - no password)
    *   These are the credentials the application is configured to use. If your XAMPP installation has a password for the `root` user, you must update the application configuration in Step 3.

---

## 3. Step 2: Configure and Build the Application

Now we will configure the Spring Boot application to connect to our new database and package it for deployment.

1.  **Clone the Repository:**
    *   Open a command prompt or Git Bash.
    *   Navigate to the directory where you want to store the project.
    *   Run: `git clone <URL_to_your_backend_repository>`
    *   `cd` into the cloned project directory.

2.  **Review Configuration:**
    *   Open the file `src/main/resources/application.properties`.
    *   Verify the following settings match your XAMPP database setup. The default values should be correct.
        ```properties
        spring.datasource.url=jdbc:mysql://localhost:3306/attendance_db
        spring.datasource.username=root
        spring.datasource.password=
        ```
    *   **If you set a password for your XAMPP `root` user, you must enter it in the `spring.datasource.password=` field.**

3.  **Build the Application:**
    *   In the root directory of the project (where `pom.xml` is located), run the Maven package command:
        ```shell
        mvn clean package
        ```
    *   This command will compile the code, run any tests, and create a single, executable JAR file in the `target/` directory.
    *   The file will be named something like `digitalattendance-0.0.1-SNAPSHOT.jar`. This JAR contains the entire application, including the web server.

---

## 4. Step 3: Run the Application

With the database ready and the application built, the final step is to run it.

1.  **Navigate to the JAR file:**
    *   In your command prompt, stay in the project's root directory. The JAR is located in the `target/` sub-directory.

2.  **Run the JAR file:**
    *   Execute the following command:
        ```shell
        java -jar target/digitalattendance-0.0.1-SNAPSHOT.jar
        ```
    *   **Note:** If your JAR file has a slightly different version number, adjust the command to match the exact filename in your `target/` folder.

3.  **Verification:**
    *   The command prompt will fill with Spring Boot startup logs.
    *   The key things to look for are:
        *   The Spring Boot banner.
        *   Log messages from Hibernate indicating it is connecting to the `attendance_db` and potentially creating/updating tables.
        *   The final log message: `Tomcat started on port(s): 8080 (http)`

**Congratulations!** The backend is now running. It is accessible on the deployment machine at `http://localhost:8080`. The frontend application can now be configured to point its API calls to the IP address of this machine on port `8080`.

---

## 5. Keeping the Application Running (Production Tip)

Running the `java -jar` command in a standard command prompt means the application will stop when you close the window. For a more permanent deployment, you should run the application as a background service.

*   On Windows, you can use tools like **NSSM (Non-Sucking Service Manager)** to easily turn the `java -jar ...` command into a proper Windows Service that will run continuously and restart automatically if it crashes.