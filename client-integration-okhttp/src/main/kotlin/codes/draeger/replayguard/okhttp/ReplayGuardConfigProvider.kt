package codes.draeger.replayguard.okhttp

import okhttp3.Request

interface ReplayGuardConfigProvider {
    fun getSecretKeyForRequest(request: Request): String
    fun getDataToEncrypt(request: Request): String
}
