package codes.draeger.replayguard.core

import java.security.MessageDigest
import java.time.Instant
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class AesSecurityStrategy(
    private val nonceCache: NonceCache = SimpleInMemoryNonceCache,
    private val getCurrentTimestamp: () -> Long = { Instant.now().epochSecond },
) : SecurityStrategy {

    override fun encrypt(data: String, key: String): String {
        val nonce = SecurityUtils.generateNonce()
        val timestamp = SecurityUtils.getCurrentTimestamp()
        val dataToEncrypt = "$nonce|$timestamp|$data"
        return encryptData(dataToEncrypt, key)
    }

    override fun decrypt(data: String, key: String, maxAgeInSeconds: Long): String {
        val decryptedData = try {
            decryptData(data, key)
        } catch (e: Exception) {
            throw InvalidEncryptionKeyException("Invalid encryption key")
        }
        val (nonce, timestampStr, originalData) = decryptedData.split('|', limit = 3)
        val timestamp = timestampStr.toLong()

        if (!isTimestampValid(timestamp, maxAgeInSeconds)) {
            throw NonceExpiredException("Nonce max age exceeded")
        }

        if (!isNonceValid(nonce)) {
            throw InvalidNonceException("Invalid nonce - already used")
        }

        return originalData
    }

    private fun encryptData(data: String, key: String): String {
        val secretKey = SecretKeySpec(hashKey(key), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(data.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    private fun decryptData(data: String, key: String): String {
        val secretKey = SecretKeySpec(hashKey(key), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(data))
        return String(decryptedBytes)
    }

    private fun hashKey(key: String): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(key.toByteArray())
    }

    private fun isNonceValid(nonce: String): Boolean {
        return nonceCache.put(nonce)
    }

    private fun isTimestampValid(timestamp: Long, maxAgeInSeconds: Long = 20): Boolean {
        val currentTimestamp = getCurrentTimestamp()
        return currentTimestamp - timestamp <= maxAgeInSeconds
    }
}
