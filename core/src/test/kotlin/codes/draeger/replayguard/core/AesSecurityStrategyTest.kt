package codes.draeger.replayguard.core

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import java.time.Instant

class AesSecurityStrategyTest {

    private lateinit var securityStrategy: SecurityStrategy
    private val secretKey = "my-secret"
    private var mockCurrentTime = Instant.now().epochSecond

    @BeforeEach
    fun setUp() {
        mockCurrentTime = Instant.now().epochSecond
        securityStrategy = AesSecurityStrategy(
            SimpleInMemoryNonceCache,
            getCurrentTimestamp = { mockCurrentTime },
        )
    }

    @Test
    fun `encrypt and decrypt with valid key and fresh nonce`() {
        val data = "Hello, World!"
        val encryptedData = securityStrategy.encrypt(data, secretKey)
        val decryptedData = securityStrategy.decrypt(encryptedData, secretKey)

        expectThat(decryptedData).isEqualTo(data)
    }

    @Test
    fun `decrypt with wrong key should throw exception`() {
        val data = "Hello, World!"
        val encryptedData = securityStrategy.encrypt(data, secretKey)

        expectThrows<InvalidEncryptionKeyException> {
            securityStrategy.decrypt(encryptedData, "wrong-secret")
        }
    }

    @Test
    fun `reuse of nonce should be detected and throw exception`() {
        val data = "Hello, World!"
        val encryptedData = securityStrategy.encrypt(data, secretKey)

        // using nonce first time should be successful
        assertDoesNotThrow {
            securityStrategy.decrypt(encryptedData, secretKey)
        }

        // using nonce second time should fail
        expectThrows<InvalidNonceException> {
            securityStrategy.decrypt(encryptedData, secretKey)
        }
    }

    @Test
    fun `decrypt with expired timestamp should throw exception`() {
        val data = "Hello, World!"
        val encryptedData = securityStrategy.encrypt(data, secretKey)
        val maxAgeInSeconds = 20L

        mockCurrentTime += maxAgeInSeconds + 1

        expectThrows<NonceExpiredException> {
            securityStrategy.decrypt(encryptedData, secretKey, maxAgeInSeconds)
        }
    }

    @Test
    fun `decrypt without expire timestamp will expire after default`() {
        val data = "Hello, World!"
        val encryptedData = securityStrategy.encrypt(data, secretKey)

        mockCurrentTime += Default.MAX_AGE_IN_SECONDS - 5

        assertDoesNotThrow {
            securityStrategy.decrypt(encryptedData, secretKey)
        }

        mockCurrentTime += 10

        expectThrows<NonceExpiredException> {
            securityStrategy.decrypt(encryptedData, secretKey)
        }
    }
}
