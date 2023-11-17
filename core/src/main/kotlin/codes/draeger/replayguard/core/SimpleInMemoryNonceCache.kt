package codes.draeger.replayguard.core

object SimpleInMemoryNonceCache : NonceCache {
    private val nonces = mutableSetOf<String>()

    @Synchronized
    override fun put(nonce: String): Boolean = nonces.add(nonce)

    @Synchronized
    override fun contains(nonce: String): Boolean = nonces.contains(nonce)
}
