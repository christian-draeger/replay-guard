# ReplayGuard

## Overview

ReplayGuard is a security-focused library designed to enhance the security of communications between mobile applications and backend servers. This library addresses the critical need for secure data transmission, specifically targeting the vulnerabilities associated with replay attacks and unauthorized data interception.

## Motivation

In the digital age, secure communication is paramount, especially in scenarios where sensitive information is transmitted over networks. Standard security measures like OAuth2 tokens can be vulnerable to replay attacks if network traffic is intercepted. ReplayGuard mitigates these risks by ensuring each request is unique and time-sensitive, using a combination of encrypted nonces and timestamps.

## Problem Solved

ReplayGuard solves two major security concerns:
1. **Replay Attacks**: By embedding a unique, encrypted nonce with each request, the library prevents the possibility of replaying intercepted network traffic.
2. **Timestamp Validation**: Alongside the nonce, a current UTC timestamp is encrypted and sent. The backend server checks the timestamp to ensure the request is not older than a specified time window (e.g., 3 minutes), further securing against unauthorized request submissions.

## How It Works

### Core Features

- **Nonce Generation**: Each request includes a securely generated random nonce.
- **Timestamp Embedding**: The current UTC timestamp is encrypted alongside the nonce.
- **AES Encryption**: Utilizes AES for strong encryption of the nonce and timestamp.
- **Server-side Validation**: The backend server decrypts the nonce and timestamp, verifying their validity.

### Server Integration Modules

Modules for easy integration with popular server frameworks like Spring Boot, Ktor, and Quarkus.

### Client Integration Modules

Client-side modules for seamless integration with libraries like OkHttp and Retrofit.

## Usage

To integrate ReplayGuard into your project, include the core module and the specific server/client modules you need.

### Gradle Dependency

```gradle
dependencies {
    implementation 'codes.draeger:replayguard-core:1.0.0'
    implementation 'codes.draeger:replayguard-server-integration-spring-boot:1.0.0'
}
```
