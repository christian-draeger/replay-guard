# ReplayGuard

## Overview

ReplayGuard is a security-focused library designed to enhance the security of communications between mobile applications
and backend servers. This library addresses the critical need for secure data transmission, specifically targeting the
vulnerabilities associated with replay attacks and unauthorized data interception.

## Motivation

In the digital age, secure communication is paramount, especially in scenarios where sensitive information is
transmitted over networks. Standard security measures like OAuth2 tokens can be vulnerable to replay attacks if network
traffic is intercepted. ReplayGuard mitigates these risks by ensuring each request is unique and time-sensitive, using a
combination of encrypted nonces and timestamps.

## Problem Solved

ReplayGuard solves two major security concerns:

1. **Replay Attacks**: By embedding a unique, encrypted nonce with each request, the library prevents the possibility of
   replaying intercepted network traffic.
2. **Timestamp Validation**: Alongside the nonce, a current UTC timestamp is encrypted and sent. The backend server
   checks the timestamp to ensure the request is not older than a specified time window (e.g., 3 minutes), further
   securing against unauthorized request submissions.

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

### Plain

The `Replay Guard` core module can be directly utilized in any Java or Kotlin project to provide encryption and nonce validation features.

### Core Module Integration

1. **Add Dependency**: First, add the core module as a dependency in your project.

    ```gradle
    dependencies {
        implementation("codes.draeger:replay-guard-core:0.1.0")
    }
    ```

2. **Using `AesSecurityStrategy`**: Instantiate `AesSecurityStrategy` to encrypt and decrypt your data with nonce and timestamp validation.

    ```kotlin
    import codes.draeger.replayguard.core.AesSecurityStrategy
    import codes.draeger.replayguard.core.SimpleInMemoryNonceCache

    val aesSecurityStrategy = AesSecurityStrategy(SimpleInMemoryNonceCache)

    val encryptedData = aesSecurityStrategy.encrypt("YourData", "YourSecretKey")
    val decryptedData = aesSecurityStrategy.decrypt(encryptedData, "YourSecretKey", 180) // 180 seconds as max age
    ```

3. **Customize Nonce Cache**: You can implement your own `NonceCache` or use the provided `SimpleInMemoryNonceCache` for nonce management.

#### Encryption and Decryption

- **Encrypt Data**: Encrypt data with a secret key. The method automatically includes a nonce and a current timestamp.

    ```kotlin
    val encryptedData = aesSecurityStrategy.encrypt("YourData", "YourSecretKey")
    ```

- **Decrypt Data**: Decrypt data with the same secret key. The method validates the nonce and checks if the timestamp is within the allowed range (max age).

    ```kotlin
    val decryptedData = aesSecurityStrategy.decrypt(encryptedData, "YourSecretKey", 180) // Max age in seconds
    ```

#### Handling Nonce and Timestamp

- The `AesSecurityStrategy` automatically generates a nonce and uses the current timestamp during encryption.
- During decryption, it validates whether the nonce has already been used and checks if the timestamp is within the specified maximum age.

#### Custom Configuration

You can customize the `AesSecurityStrategy` by providing your own implementation of `NonceCache` and a custom method for getting the current timestamp.

#### Use Case

This direct usage is ideal for projects where you need robust security mechanisms for data transmission but are not using Spring Boot, or in scenarios where you have specific requirements that require direct control over the encryption and decryption process.

### Usage in Spring Boot Servers

`Replay Guard` is designed to be seamlessly integrated into Spring Boot applications. By using our custom annotation and
configuration, you can easily add encrypted request validation to your endpoints.

#### Setup

1. **Add Dependency**: Ensure that the `server-integration-spring-boot` module is included in your project's
   dependencies.

    ```gradle
    dependencies {
        implementation("codes.draeger:server-integration-spring-boot:0.1.0")
        // other necessary dependencies
    }
    ```

2. **Configure Application Properties**: In your `application.properties` or `application.yml`, set the secret key and
   other necessary properties.

    ```properties
    replay.guard.secret-key=YourSecretKey
    replay.guard.max-age-in-seconds=180
    ```

#### Using the Annotation

Annotate your controller or specific endpoints with `@ValidateEncryptedRequest` to enable encrypted request validation.

Either on method level:

```kotlin
import codes.draeger.replayguard.spring.ValidateEncryptedRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SecureController {

    @ValidateEncryptedRequest
    @GetMapping("/secure-endpoint")
    fun secureEndpoint(): String {
        return "Secure Response"
    }

    @GetMapping("/public-endpoint")
    fun publicEndpoint(): String {
        return "Public Response"
    }
}
```

or on class level:

```kotlin
import codes.draeger.replayguard.spring.ValidateEncryptedRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@ValidateEncryptedRequest
@RestController
class SecureController {

    
    @GetMapping("/secure-endpoint")
    fun secureEndpoint(): String {
        return "..."
    }

    @GetMapping("/also-secure-endpoint")
    fun publicEndpoint(): String {
        return "..."
    }
}
```

### Custom Configuration
Replay Guard allows customization of the security configuration. You can define your own bean for AesSecurityStrategy or use the default provided by our library.

### Testing
Integration tests can be written to ensure that your endpoints correctly handle encrypted and unencrypted requests. Refer to the example project in the examples/spring-boot-example directory for guidance on writing these tests.
