# javaevaluation

Este es un ejercicio solicitado por BCI para evaluar el manejo del lenguaje Java. Es un microservicio con dos endpoints: uno para sign-in de un usuario que es persistido en una base de datos H2 y otro de login que consulta la base en busqueda de ese usuario.

## Instalación

Instrucciones para instalar y configurar el proyecto.

### Requisitos

- Java 11
- Gradle

### Instrucciones

1. Clona el repositorio:

    ```bash
    git clone https://github.com/chubu1982/javaevaluation.git
    ```

2. Navega al directorio del proyecto:

    ```bash
    cd javaevaluation
    ```

3. Construye el proyecto usando Gradle:

    ```bash
    ./gradlew build
    ```

4. Ejecuta la aplicación:

    ```bash
    ./gradlew bootRun
    ```

## Uso

Opcionalmente se brinda una collection de Postman con algunos request de ejemplo para poder probar la API.
