package io.j0a0m4.portsandadapters.domain.usecases

import io.j0a0m4.portsandadapters.domain.OtpMismatch
import io.j0a0m4.portsandadapters.domain.model.*
import io.j0a0m4.portsandadapters.domain.usecases.dependencies.OtpRepository
import io.j0a0m4.portsandadapters.domain.usecases.dependencies.Sender
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.result.shouldBeSuccess
import io.mockk.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.task.AsyncTaskExecutor

@SpringBootTest
class OtpInteractorTest : FeatureSpec() {
	@TestConfiguration
	class OtpInteractorTestConfig {
		@Bean
		fun storage() = mockk<OtpRepository>(relaxed = true)

		@Bean
		fun sender() = mockk<Sender>(relaxed = true)

		@Bean
		fun contactPort() = mockk<ContactUseCases>(relaxed = true)

		@Bean
		fun async() = mockk<AsyncTaskExecutor>(relaxed = true)
	}

	@Autowired
	private lateinit var storage: OtpRepository

	@Autowired
	private lateinit var sender: Sender

	@Autowired
	private lateinit var contactPort: ContactUseCases

	@Autowired
	private lateinit var async: AsyncTaskExecutor

	@Autowired
	private lateinit var interactor: OtpService

	private val contact = contact {
		it.name = "Apashe"
		it.email = "master@apashe.com"
		it.phone = "+12505550199"
	}

	init {
		afterTest {
			clearMocks(contactPort, storage, sender, async)
		}

		feature("verify OTP code") {
			val sendMethod = SendMethod.Phone
			val otp: VerificationCode = 835214

			scenario("provided OTP matches record") {
				every { storage.retrieveOtp(contact.id, sendMethod) } returns
					Result.success(otp)

				interactor.verify(contact.id, sendMethod, otp) shouldBeSuccess Unit

				verifyOrder {
					contactPort.setToVerified(contact.id)
					storage.invalidate(contact.id, sendMethod)
				}
			}

			scenario("provided OTP does not match record") {
				every { storage.retrieveOtp(contact.id, sendMethod) } returns
					Result.failure(OtpMismatch())

				interactor.verify(contact.id, sendMethod, otp)

				verify(inverse = true) {
					contactPort.setToVerified(contact.id)
					storage.invalidate(contact.id, sendMethod)
				}
			}
		}
	}
}
