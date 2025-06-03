# Spring Cloud Config Auto-Refresh using Spring Cloud Bus & Monitor

## 🔁 Goal

Enable automatic refresh of configuration properties in microservices when the config changes in the GitHub repo, using Spring Cloud Config, Spring Cloud Bus (with RabbitMQ), and Spring Cloud Config Monitor.

---

## 🔠 Step-by-Step Setup Guide

### ✅ Step 1: Run RabbitMQ

Run RabbitMQ locally or via Docker:

```bash
docker run -d --hostname rabbitmq --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

---

### ✅ Step 2: Configure RabbitMQ in All Microservices

In each microservice's `application.yml`:

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

---

### ✅ Step 3: Add Spring Cloud Bus Dependency

In *all* microservices (including the config server):

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```

---

### ✅ Step 4: Expose Actuator Endpoints

In `application.yml` of all services:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

To manually test:

```bash
curl -X POST http://localhost:<port>/actuator/busrefresh
```

---

### ✅ Step 5: Add Config Monitor to Config Server

Only in the config server:

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-config-monitor</artifactId>
</dependency>
```

---

### ✅ Step 6: Set Up GitHub Webhook

Since GitHub cannot call `localhost`, use [Hookdeck](https://console.hookdeck.com/).

#### Hookdeck Setup:

1. Sign up and log in.
2. Add new **Destination** to your config server port.
3. Hookdeck gives you a **Source URL**, like:

   ```
   https://hkdk.events/h73z61vyt312i6
   ```
4. Append `/monitor` → Final URL:

   ```
   https://hkdk.events/h73z61vyt312i6/monitor
   ```
5. In terminal:

   ```bash
   hookdeck login --cli-key <your-cli-key>
   hookdeck listen 8071
   ```

---

### ✅ Step 7: Add GitHub Webhook

1. Go to GitHub repo → Settings → Webhooks → Add Webhook.
2. Payload URL: Hookdeck `/monitor` URL.
3. Content type: `application/json`
4. Event: Only "Push Event"

---

### ✅ Step 8: Test It

1. Change a config value in the GitHub repo.
2. Commit & push.
3. Check logs in microservices — should show `RefreshRemoteApplicationEvent`.

Optional: Check `/actuator/env` or `/actuator/configprops` endpoints to verify updates.

---

## 🔄 Optional: Manual Refresh

```bash
curl -X POST http://localhost:8071/actuator/busrefresh
```

---

## 📊 Final Architecture Diagram (Mental Map)

```
[ GitHub Repo ]
      |
      V  (Webhook)
[ Hookdeck Tunnel ] --> [ /monitor Endpoint on Config Server ]
      |
      V
[ Spring Cloud Bus (RabbitMQ) ]
      |
      V
[ Microservices with /actuator/busrefresh ]
```

---

## ✨ Next Steps (Optional Enhancements)

* Add security to actuator endpoints.
* Use `destination` to target refresh to specific services.
* Add logging/alerts for auto-refresh events.
* Dockerize the whole setup for easy local testing.
