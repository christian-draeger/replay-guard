package codes.draeger.replayguard.core

import java.security.SecureRandom
import java.time.Instant

object SecurityUtils {
    /**
     * Generates a cryptographically strong random nonce.
     *
     * This method creates a 16-byte nonce using a secure random number generator.
     * The generated nonce is a sequence of random bytes, formatted as a hexadecimal string.
     *
     * @return A hexadecimal string representing the randomly generated nonce.
     */
    fun generateNonce(): String =
        ByteArray(16).also { SecureRandom().nextBytes(it) }
            .joinToString("") { "%02x".format(it) }

    /**
     * Retrieves the current Unix timestamp in seconds (UTC).
     *
     * @return The current Unix timestamp as a long value.
     */
    fun getCurrentTimestamp(): Long = Instant.now().epochSecond
}
