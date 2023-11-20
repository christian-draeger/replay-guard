package codes.draeger.replayguard.spring

import codes.draeger.replayguard.core.AesSecurityStrategy
import codes.draeger.replayguard.core.SecurityStrategy
import codes.draeger.replayguard.core.SimpleInMemoryNonceCache
import org.springframework.boot.autoconfigure.AutoConfigurationPackage
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@AutoConfigurationPackage
@Configuration
class ReplayGuardSecurityConfig {

    @Bean
    fun aesSecurityStrategy(): SecurityStrategy {
        return AesSecurityStrategy(SimpleInMemoryNonceCache)
    }
}
