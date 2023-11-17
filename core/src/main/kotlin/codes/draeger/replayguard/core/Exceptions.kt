package codes.draeger.replayguard.core

class InvalidEncryptionKeyException(message: String) : Exception(message)
class InvalidNonceException(message: String) : Exception(message)
class NonceExpiredException(message: String) : Exception(message)
