package codes.draeger.replayguard.okhttp

interface ReplayGuardConfigProvider {
    fun getSecretKeyForRequest(request: okhttp3.Request): String
    fun getDataToEncrypt(request: okhttp3.Request): String
}
