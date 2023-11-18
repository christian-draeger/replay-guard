package codes.draeger.replayguard.spring

import codes.draeger.replayguard.core.AesSecurityStrategy
import codes.draeger.replayguard.core.REPLAY_GUARD_HEADER_NAME
import codes.draeger.replayguard.core.InvalidEncryptionKeyException
import codes.draeger.replayguard.core.InvalidNonceException
import codes.draeger.replayguard.core.NonceExpiredException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class EncryptedRequestInterceptor(
    private val config: SecurityConfigurationProperties,
    private val aesSecurityStrategy: AesSecurityStrategy,
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod && shouldValidateRequest(handler)) {
            val encryptedHeader = request.getHeader(REPLAY_GUARD_HEADER_NAME)

            if (encryptedHeader.isNullOrEmpty()) {
                response.sendError(HttpStatus.BAD_REQUEST.value(), "Missing encrypted header")
                return false
            }

            return try {
                val decryptedData =
                    aesSecurityStrategy.decrypt(encryptedHeader, config.secretKey, config.maxAgeInSeconds)
                request.setAttribute("decryptedData", decryptedData)
                true
            } catch (e: InvalidEncryptionKeyException) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid encryption key")
                false
            } catch (e: InvalidNonceException) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid nonce - already used")
                false
            } catch (e: NonceExpiredException) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Nonce max age exceeded")
                false
            }
        }
        return true
    }

    private fun shouldValidateRequest(handler: HandlerMethod): Boolean {
        return handler.method.isAnnotationPresent(ValidateEncryptedRequest::class.java) ||
            handler.beanType.isAnnotationPresent(ValidateEncryptedRequest::class.java)
    }
}
