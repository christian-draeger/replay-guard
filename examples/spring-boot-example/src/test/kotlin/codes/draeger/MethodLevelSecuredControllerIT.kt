package codes.draeger

import codes.draeger.replayguard.core.AesSecurityStrategy
import codes.draeger.replayguard.core.HEADER_NAME
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MethodLevelSecuredControllerIT(
    @Autowired val restTemplate: TestRestTemplate,
    @Autowired private val aesSecurityStrategy: AesSecurityStrategy,
    @Value("\${replay.guard.secret-key}") val secretKey: String,
) {

    @LocalServerPort
    private val port: Int = 0

    @Test
    fun `replay guard secured endpoint with valid encrypted header returns success`() {
        val headers = HttpHeaders()
        headers.add(HEADER_NAME, createValidEncryptedHeader())
        val entity = HttpEntity<String>(headers)

        val response = restTemplate.exchange("http://localhost:$port/secured", HttpMethod.GET, entity, String::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Hello World", response.body)
    }

    @Test
    fun `replay guard secured endpoint without encrypted header returns bad request`() {
        val response = restTemplate.getForEntity("http://localhost:$port/secured", String::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `unsecured endpoint returns success`() {
        val response = restTemplate.getForEntity("http://localhost:$port/unsecured", String::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Hello World", response.body)
    }

    private fun createValidEncryptedHeader(): String {
        return aesSecurityStrategy.encrypt("someData", secretKey)
    }
}
