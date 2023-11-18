package codes.draeger.replayguard.okhttp

import codes.draeger.replayguard.core.AesSecurityStrategy
import codes.draeger.replayguard.core.REPLAY_GUARD_HEADER_NAME
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class ReplayGuardInterceptorTest {

    private lateinit var interceptor: ReplayGuardInterceptor
    private val mockChain: Interceptor.Chain = mockk(relaxed = true)
    private val mockSecurityStrategy: AesSecurityStrategy = mockk(relaxed = true)
    private val mockConfigProvider: ReplayGuardConfigProvider = mockk()

    @BeforeEach
    fun setUp() {
        every { mockConfigProvider.getSecretKeyForRequest(any()) } returns "TestSecretKey"
        every { mockConfigProvider.getDataToEncrypt(any()) } returns "TestData"
        every { mockSecurityStrategy.encrypt(any(), any()) } answers { arg<String>(0) + "_encrypted" }
        interceptor = ReplayGuardInterceptor(configProvider = mockConfigProvider, isGlobal = true)
    }

    @Test
    fun `interceptor adds encrypted header when globally enabled`() {
        val mockRequest: Request = Request.Builder().url("https://example.com").build()
        every { mockChain.request() } returns mockRequest
        val response: Response = mockk(relaxed = true)

        every { mockChain.proceed(any()) } answers {
            val modifiedRequest = firstArg<Request>()
            val encryptedHeader = modifiedRequest.header(REPLAY_GUARD_HEADER_NAME)
            assertNotNull(encryptedHeader)
            assertEquals("TestData", AesSecurityStrategy().decrypt(encryptedHeader!!, "TestSecretKey"))

            response
        }

        interceptor.intercept(mockChain)

        verify { mockChain.proceed(any()) }
    }

    @Test
    fun `interceptor adds encrypted header when not globally enabled but activated on request`() {
        val mockRequest: Request = Request.Builder()
            .url("https://example.com")
            .enableReplayGuard()
            .build()
        every { mockChain.request() } returns mockRequest

        interceptor = ReplayGuardInterceptor(
            securityStrategy = mockSecurityStrategy,
            configProvider = mockConfigProvider,
            isGlobal = false,
        )

        val mockResponse: Response = mockk(relaxed = true)
        every { mockChain.proceed(any()) } returns mockResponse

        every { mockChain.proceed(any()) } answers {
            val modifiedRequest = firstArg<Request>()
            val encryptedHeader = modifiedRequest.header(REPLAY_GUARD_HEADER_NAME)
            assertNotNull(encryptedHeader)
            assertEquals("TestData_encrypted", encryptedHeader)
            mockResponse // Stellen Sie sicher, dass die Antwort hier zurückgegeben wird
        }

        val response = interceptor.intercept(mockChain)
        assertEquals(mockResponse, response)

        verify { mockChain.proceed(any()) }
    }

    @Test
    @Disabled
    fun `interceptor does not add encrypted header when globally enabled but deactivated on request`() {
        val mockRequest: Request = Request.Builder()
            .url("https://example.com")
            .disableReplayGuard()
            .build()
        every { mockChain.request() } returns mockRequest

        interceptor = ReplayGuardInterceptor(
            securityStrategy = mockSecurityStrategy,
            configProvider = mockConfigProvider,
            isGlobal = true,
        )

        val mockResponse: Response = mockk(relaxed = true)
        every { mockChain.proceed(any()) } returns mockResponse

        interceptor.intercept(mockChain)

        verify {
            mockChain.proceed(
                match { req ->
                    req.header(REPLAY_GUARD_HEADER_NAME) == null
                },
            )
        }
    }

    @Test
    fun `interceptor does not add encrypted header when not globally enabled and not activated on request`() {
        val mockRequest: Request = Request.Builder().url("https://example.com").build()
        every { mockChain.request() } returns mockRequest

        interceptor = ReplayGuardInterceptor(
            securityStrategy = mockSecurityStrategy,
            configProvider = mockConfigProvider,
            isGlobal = false,
        )

        interceptor.intercept(mockChain)

        verify { mockChain.proceed(mockRequest) }
    }
}
