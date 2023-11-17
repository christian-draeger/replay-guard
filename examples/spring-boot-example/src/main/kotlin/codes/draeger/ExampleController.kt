package codes.draeger

import codes.draeger.replayguard.spring.ValidateEncryptedRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MethodLevelSecuredController {

    @ValidateEncryptedRequest
    @GetMapping("/secured")
    fun getSecured() = "Hello World"

    @GetMapping("/unsecured")
    fun getUnsecured() = "Hello World"
}

@ValidateEncryptedRequest
@RestController
class ClassLevelSecuredController {

    @GetMapping("/foo")
    fun getFoo() = "Hello World"

    @GetMapping("/bar")
    fun getBar() = "Hello World"
}
