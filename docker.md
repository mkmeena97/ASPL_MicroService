# Virtual Machines vs Containers

## Definition

### Virtual Machine (VM)

A virtual machine (VM) is an emulation of a physical computer system. It uses software to simulate the hardware of a physical computer, allowing multiple operating systems to run on a single physical machine. VMs rely on a **hypervisor**, a software layer that sits between the hardware and the operating systems, to manage these virtual environments. Each VM includes a full copy of an operating system, the application, necessary binaries, and libraries—taking up tens of gigabytes.

### Container

A container is a lightweight, standalone, and executable package of software that includes everything needed to run an application: the code, runtime, libraries, and system tools. Unlike VMs, containers do not include a full operating system. Instead, they share the host OS kernel and isolate applications at the process level using kernel features such as **namespaces** and **control groups (cgroups)**.

## Working

### How VMs Work

* **Hypervisor**: The core technology that enables virtualization. It can be of two types:

  * Type 1 (bare-metal): Runs directly on hardware (e.g., VMware ESXi).
  * Type 2 (hosted): Runs on top of an OS (e.g., VirtualBox).
* Each VM runs a complete guest OS, independent of the host.
* Resources (CPU, memory, storage) are allocated and virtualized.
* Typically requires more resources and has slower boot times.

### How Containers Work

* Containers are managed by container runtimes like Docker or containerd.
* Use **kernel-level features** for process isolation and resource control:

  * **Namespaces**: Isolate process trees, users, networks, etc.
  * **cgroups**: Limit resource usage.
* Share the host OS kernel, making them much lighter and faster.
* Containers are created from **images**, which are layered filesystems.

## Differences

| Feature        | Virtual Machines                       | Containers                                     |
| -------------- | -------------------------------------- | ---------------------------------------------- |
| OS             | Full Guest OS                          | Share Host OS Kernel                           |
| Boot Time      | Several minutes                        | A few seconds                                  |
| Resource Usage | Heavy (each VM needs full OS)          | Lightweight (shares host OS kernel)            |
| Isolation      | Strong (via hypervisor)                | Weaker (process-level isolation)               |
| Portability    | Less portable (OS dependent)           | Highly portable (runs consistently everywhere) |
| Management     | Slower to provision, harder to migrate | Fast provisioning, easy to migrate             |

---

# Docker Image vs Docker Container

## Docker Image Definition

A Docker image is a read-only template used to create Docker containers. It includes the application code, libraries, dependencies, environment variables, and configuration files needed to run the application. Images are built using **Dockerfiles**, which define a series of steps and commands to assemble the image.

## Docker Container Definition

A Docker container is a runtime instance of a Docker image. When an image is executed, it becomes a container. Containers are isolated from each other and the host system, yet they share the host OS kernel. Containers are ephemeral by nature—once stopped, their state is lost unless explicitly saved.

## Differences

| Feature  | Docker Image            | Docker Container               |
| -------- | ----------------------- | ------------------------------ |
| Nature   | Blueprint/Template      | Running or executable instance |
| State    | Immutable               | Mutable during runtime         |
| Lifespan | Persistent as files     | Temporary unless committed     |
| Creation | Created from Dockerfile | Created from Docker image      |

## How They Work

* **Image**: Created by executing a Dockerfile with `docker build`. It is a stack of read-only layers.
* **Container**: Instantiated with `docker run`, adding a writable layer on top of the image. This is where application changes during runtime occur.

---

# Software Containerization

## What is Software Containerization?

Software containerization is a lightweight form of virtualization that involves encapsulating an application and its dependencies into a container image. This ensures that the application runs uniformly across different environments—from developer laptops to production servers.

## Benefits:

* Environment consistency
* Faster development and deployment
* Better resource utilization
* Easier scaling and orchestration (with tools like Kubernetes)

## How to Containerize

1. **Write a Dockerfile**: Describe the image’s build process.
2. **Build the Image**: Use `docker build` to turn the Dockerfile into an image.
3. **Run the Container**: Use `docker run` to start the container from the image.

---

# Docker

Docker is an open-source platform designed to automate the deployment, scaling, and management of containerized applications. It uses OS-level virtualization provided by the Linux kernel (or via a lightweight VM on non-Linux systems) to deliver software in packages called containers.

---

# Linux and Containerization

## Key Features Used:

### Namespaces

Namespaces in Linux provide isolated views of system resources for each process. Types of namespaces include:

* **PID**: Isolates process IDs, so processes in different containers can have the same PID.
* **NET**: Isolates network interfaces. Each container can have its own IP and routing table.
* **MNT**: Isolates filesystem mount points, allowing containers to have their own filesystem hierarchy.
* **IPC**: Isolates inter-process communication, preventing shared memory collisions.
* **UTS**: Allows containers to have independent hostnames.
* **USER**: Maps user IDs inside the container to different IDs on the host, enhancing security.

Each time a container is created, Docker configures the necessary namespaces to isolate it from other processes.

### Control Groups (cgroups)

cgroups limit, account for, and isolate the resource usage (CPU, memory, disk I/O, etc.) of a group of processes. This prevents a single container from consuming all the host resources, enabling resource quotas and prioritization.

How it works:

* The Linux kernel maintains hierarchies of resources.
* Docker assigns containers to specific cgroups.
* These cgroups enforce the resource constraints defined at runtime.

### Union File Systems (UnionFS)

UnionFS allows files and directories of separate file systems to be transparently overlaid, forming a single coherent file system. Docker uses UnionFS to:

* Stack multiple layers of images.
* Enable copy-on-write behavior, which saves storage and increases speed.

### chroot and pivot\_root

* **chroot**: Changes the root directory for a process, restricting its view of the filesystem.
* **pivot\_root**: More advanced than `chroot`, used during container startup to switch the root filesystem of a process.

Together, these tools provide the foundation for container isolation, ensuring that processes inside containers don't interfere with the host or other containers.

---

# Docker on Mac, Linux, and Windows

## Linux

* Runs natively using kernel features (namespaces, cgroups).
* Most efficient setup.

## macOS & Windows

* Uses a lightweight VM to simulate a Linux environment.

  * **macOS**: Uses HyperKit or QEMU
  * **Windows**: Uses WSL2 or Hyper-V
* Docker Desktop manages VM behind the scenes.

---

# Docker Architecture

## Docker Client

* **Docker CLI**: Interface for users to interact with Docker (`docker build`, `docker run`, etc.).
* **Docker Remote API**: Allows external tools or services to interact with Docker daemon programmatically over HTTP.

## Docker Host / Server

* **Docker Daemon (`dockerd`)**: Background service that manages containers, images, networks, and volumes.
* **Containers**: Running instances of Docker images.
* **Images**: Immutable templates for containers.
* **Volumes and Networks**: Persistent storage and isolated networking for containers.

## Docker Registry

* **Docker Hub**: Public repository of Docker images.
* **Private Registry**: Used for secure or internal image sharing.

  * Tools: Docker Trusted Registry, Harbor, GitLab Container Registry.

---
# Complete Guide: Building and Running Docker Images for ASPL Microservices

This document explains **three different approaches** to generate Docker images for your Spring Boot microservices and how to run them.

---

## Prerequisites

- Java JDK 21 or higher installed  
- Maven installed  
- Docker installed and running  
- A Spring Boot project with `packaging` set to `jar` in `pom.xml`

---

## Common Setup: Packaging Configuration

Make sure your `pom.xml` includes this at the top level (inside `<project>`):

```xml
<packaging>jar</packaging>
```
This enables packaging your application as a jar file for containerization.

### ***Approach 1:*** Using a Dockerfile (Traditional Method)
**Step 1:** Set packaging in pom.xml
Ensure your pom.xml has:

```xml
<packaging>jar</packaging>
```
**Step 2:** Create a Dockerfile in your project root folder
```dockerfile
# Start with base image that has Java 24 runtime
FROM openjdk:24-jdk-slim

MAINTAINER mahendrakumar27697@gmail.com

# Copy your jar file from target directory to container
COPY target/Account-0.0.1-SNAPSHOT.jar account-0.0.1-SNAPSHOT.jar

# Run the jar file
ENTRYPOINT ["java", "-jar", "account-0.0.1-SNAPSHOT.jar"]
```
**Step 3:** Build the Docker image
Open terminal in project root and run:

```
docker build . -t mkmeena97/accounts:aspl-bank-microservice
```
**Step 4:** Run the Docker container
```
docker run -d -p 8080:8080 mkmeena97/accounts:aspl-bank-microservice
```
### ***Approach 2:*** Using Spring Boot Maven Plugin build-image
Spring Boot can create Docker images without writing a Dockerfile.

**Step 1:** Update pom.xml to set packaging and image config
Add or update the following inside your <project> tag, preferably under <build><plugins>:

```xml
<packaging>jar</packaging>

<build>
  <plugins>
    <plugin>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-maven-plugin</artifactId>
      <configuration>
        <image>
          <name>mkmeena97/${project.artifactId}:aspl-bank-microservice</name>
        </image>
      </configuration>
    </plugin>
  </plugins>
</build>
```
**Step 2:** Build Docker image via Maven
Run this command in terminal:
```
mvn spring-boot:build-image
```
**Step 3:** Run the Docker container
```
docker run -d -p 8080:8080 mkmeena97/accounts:aspl-bank-microservice
```
### ***Approach 3:*** Using Google Jib Maven Plugin
Jib builds optimized Docker images without Dockerfile or Docker daemon.

**Step 1:** Configure your pom.xml
Inside <build><plugins> add:

```xml
<packaging>jar</packaging>

<build>
  <plugins>
    <plugin>
      <groupId>com.google.cloud.tools</groupId>
      <artifactId>jib-maven-plugin</artifactId>
      <version>3.4.5</version>
      <configuration>
        <from>
          <image>eclipse-temurin:21-jdk</image>
        </from>
        <to>
          <image>mkmeena97/${project.artifactId}:aspl-cards-microservice</image>
        </to>
      </configuration>
    </plugin>
  </plugins>
</build>
```
**Step 2:** Build Docker image with Jib
Run in terminal:

```bash
mvn clean compile jib:dockerBuild
```
**Step 3:** Run the Docker container
```bash
docker run -d -p 8082:8082 mkmeena97/cards:aspl-cards-microservice
```

**Additional Notes**
- Replace mkmeena97 and ${project.artifactId} with your actual Docker Hub username and artifact ID.

- Ensure the base image Java version matches your project Java version to avoid compatibility issues.

- Use ``` docker ps ``` to check running containers and docker logs <container_id> for logs.

---








