# Spring Boot Configuration & Externalization Notes

---

## Configuration Priority Order (Highest to Lowest)

1. Command Line Arguments (`--key=value`)
2. JVM System Properties (`-Dkey=value`)
3. OS Environment Variables (`KEY=value`)
4. `application-{profile}.yml` or `.properties`
5. `application.yml` or `.properties`
6. `@PropertySource` in code
7. Default Values in code

---

## Reading Configuration in Spring Boot

### 1. `@Value`
```java
@Value("${build.version}")
private String buildVersion;
```
2. Environment Interface
```java
@Autowired
private Environment env;
String version = env.getProperty("build.version");
```
3. `@ConfigurationProperties` (Recommended)
```java
@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String name;
    private String version;
    // getters & setters
}
```
```yaml
app:
  name: AccountsApp
  version: 1.0
```
## Spring Profiles
### Creating Profiles:
- `application.yml`

- `application-dev.yml`

- `application-qa.yml`

- `application-prod.yml`

### Activating Profile
1. **In application.yml:**
```yaml
spring:
  profiles:
    active: prod
```    
2. **Via Command Line:**
```bash

--spring.profiles.active=prod
```
3. **Via JVM System Property:**
```bash

-Dspring.profiles.active=prod
```
4. **Via Environment Variable:**
```bash

SPRING_PROFILES_ACTIVE=prod
```
## Externalizing Configuration
### 1. Command Line Arguments
- **From terminal:**
    ```bash
    
    java -jar app.jar --spring.profiles.active=prod --build.version=1.2
    ```
- **From IntelliJ:**

  - Right-click main class → Modify Run Configuration

   - Add in “Program Arguments”:

    ```ini
    
    --spring.profiles.active=prod --build.version=1.2
    ```
### 2. JVM System Properties
  - **From terminal:**
      ```bash
    
      java -Dspring.profiles.active=prod -Dbuild.version=1.2 -jar app.jar
      ```
  - **From IntelliJ:**

    - Modify Run Config → In “VM options”, add:
    ```ini
    -Dspring.profiles.active=prod -Dbuild.version=1.2
    ```
### 3. Environment Variables
  - **From terminal (Linux/Mac):**

    - ```bash
      export SPRING_PROFILES_ACTIVE=prod
      export BUILD_VERSION=1.3
      ```

    - **From IntelliJ:**
      - Modify Run Config → Environment Variables:
      ```ini
      SPRING_PROFILES_ACTIVE=prod;BUILD_VERSION=1.3
      ```
      - Mapping Rule:
        - Dots (.) → Underscores (_)
        - Lowercase → UPPERCASE
        - spring.datasource.url → SPRING_DATASOURCE_URL

## Custom Config File Location
```bash

java -jar app.jar --spring.config.location=file:/path/to/config/
```
OR
```bash

java -Dspring.config.location=file:/path/to/config/ -jar app.jar
```

## Summary
- Use @Value, Environment, or @ConfigurationProperties to read values.

- Use Spring Profiles (dev, qa, prod) for grouping config files.

- Externalize using CLI args, JVM props, or env vars for flexibility.

- Always prefer externalization for sensitive and environment-dependent configs.