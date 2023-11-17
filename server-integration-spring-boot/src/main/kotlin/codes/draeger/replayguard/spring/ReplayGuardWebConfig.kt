package codes.draeger.replayguard.spring

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class ReplayGuardWebConfig(private val encryptedRequestInterceptor: EncryptedRequestInterceptor) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(encryptedRequestInterceptor)
    }
}
