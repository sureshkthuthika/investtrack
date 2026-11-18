@'
# InvestTrack Backend

## Overview

InvestTrack is a portfolio management application backend built with Java and Spring Boot.

## Technology Stack

- Java 21
- Spring Boot
- Spring Security
- JWT Authentication
- Spring Data MongoDB
- MongoDB Atlas
- Maven
- WebSocket with STOMP
- Spring Boot Actuator

## Prerequisites

- Java 21
- MongoDB Atlas account
- Git

## Configuration

Configure the following environment variables before starting the application:

- `MONGODB_URI` - MongoDB Atlas connection string
- `JWT_SECRET` - Secret used for JWT signing

Never commit actual credentials or secrets.

## Run Locally

From the backend project directory:

```powershell
.\mvnw.cmd spring-boot:run