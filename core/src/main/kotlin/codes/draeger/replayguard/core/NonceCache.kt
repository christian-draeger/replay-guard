package codes.draeger.replayguard.core

interface NonceCache {
    /**
     * @return true if the nonce was not present in the cache and was added, false otherwise
     */
    fun put(nonce: String): Boolean

    /**
     * @return true if the nonce was present in the cache, false otherwise
     */
    fun contains(nonce: String): Boolean
}
