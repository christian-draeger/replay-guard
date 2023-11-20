package codes.draeger.replayguard.spring

import codes.draeger.replayguard.core.Default
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "replay.guard")
class SecurityConfigurationProperties {
    var secretKey: String = Default.SECRET
    var maxAgeInSeconds: Long = Default.MAX_AGE_IN_SECONDS
}
