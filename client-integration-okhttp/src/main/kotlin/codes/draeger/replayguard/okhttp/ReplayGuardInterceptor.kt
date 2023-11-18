package codes.draeger.replayguard.okhttp

import codes.draeger.replayguard.core.AesSecurityStrategy
import codes.draeger.replayguard.core.REPLAY_GUARD_HEADER_NAME
import codes.draeger.replayguard.core.SecurityStrategy
import okhttp3.Interceptor
import okhttp3.Response

class ReplayGuardInterceptor(
    private val securityStrategy: SecurityStrategy = AesSecurityStrategy(),
    private val configProvider: ReplayGuardConfigProvider,
    private var isGlobal: Boolean = false,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val replayGuardTag = originalRequest.tag(ReplayGuardEnabled::class.java)
        val shouldActivate = isGlobal || replayGuardTag == ReplayGuardEnabled

        if (shouldActivate) {
            val secretKey = configProvider.getSecretKeyForRequest(originalRequest)
            val dataToEncrypt = configProvider.getDataToEncrypt(originalRequest)
            val encryptedHeader = securityStrategy.encrypt(dataToEncrypt, secretKey)

            val newRequest = originalRequest.newBuilder()
                .header(REPLAY_GUARD_HEADER_NAME, encryptedHeader)
                .build()

            return chain.proceed(newRequest)
        }

        return chain.proceed(originalRequest)
    }
}
