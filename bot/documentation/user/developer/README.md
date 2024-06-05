# CommIT / ITCrowd documentation

## Setup
The Crazy88 bot is written in Java. At the time of writing, targeting Java 21 (though there is no reason for this to stay 21).
We use Gradle as the build system.

### Building
This requires the Java JDK version `>= 21`

Linux:
```bash
./gradlew shadowJar
```
Windows:
```
.\gradlew.bat shadowJar 
```
This will create a JAR-file containing all required dependencies in the `app/build/libs/` directory.

### Configuration
The application takes one environmental variable:
```
CONFIG_PATH=<path to config.json>
```
and requires a configuration file in JSON format. Refer to `examle_config.json` for the available options.

### Running
This requires the Java JRE version `>= 21`
```bash
java -jar <path to JAR>
```
where `<path to JAR>` is the JAR-file ending in `-dist.jar` found in the `app/build/libs/` directory.

## Modules
All source code is contained in the `app/` directory.
The main Java package (`nl.svsticky.crazy88`) is subdivided in the following packages:
- `command` Dispatching a Discord command to the responsible handler
  - `handler` The actual command handlers
- `config` Confiration of the application
  - `model` Java representation of the JSON file
- `database`
  - `driver` The database driver, responsible for opening connections and handling migrations
  - `model` Java representation of the database and the associated SQL statements
- `events` Discord event handlers
- `http` Built-in HTTP server
  - `response` HTTP response types (think JSON, plain bytes, etc)
  - `routes` HTTP route handlers
  
Furthermore, the `resources` directory contains the log4j configuration and the Database migrations.

## Database migrations
A database migration can be created by adding another SQL file to the `resources/migrations` directory.
The file name should of the format: `<number>_<name>.sql`. Where `<number>` is incremental and `<name>` is arbitrary.

Existing migrations cannot be altered. There is no mechanism checking this, but changes will not be applied.

