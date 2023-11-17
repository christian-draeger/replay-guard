package codes.draeger.replayguard.core

object Default {
    const val SECRET = "secret"
    const val MAX_AGE_IN_SECONDS = 10L
}

interface SecurityStrategy {
    fun encrypt(data: String, key: String = Default.SECRET): String
    fun decrypt(data: String, key: String = Default.SECRET, maxAgeInSeconds: Long = Default.MAX_AGE_IN_SECONDS): String
}
