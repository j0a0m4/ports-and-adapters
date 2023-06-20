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
import org.springframework.context.annotation.*
import org.springframework.core.task.AsyncTaskExecutor

class OtpServiceTest : OtpServiceTestSpec() {
	init {
		val contact = contact {
			it.name = "Apashe"
			it.email = "master@apashe.com"
			it.phone = "+12505550199"
		}

		feature("verify OTP code") {
			val sendMethod = SendMethod.Phone
			val otp: VerificationCode = 835214

			scenario("provided OTP matches record") {
				every {
					otpRepository.retrieveOtp(contact.id, sendMethod)
				} answers {
					Result.success(otp)
				}

				service.verify(contact.id, sendMethod, otp) shouldBeSuccess Unit

				verifyOrder {
					contactPort.setToVerified(contact.id)
					otpRepository.invalidate(contact.id, sendMethod)
				}
			}

			scenario("provided OTP does not match record") {
				every {
					otpRepository.retrieveOtp(contact.id, sendMethod)
				} answers {
					Result.failure(OtpMismatch())
				}

				service.verify(contact.id, sendMethod, otp)

				verify(inverse = true) {
					contactPort.setToVerified(contact.id)
					otpRepository.invalidate(contact.id, sendMethod)
				}
			}
		}
	}
}

@TestConfiguration
class OtpServiceTestConfig {
	@Bean
	@Primary
	fun otpRepository() = mockk<OtpRepository>(relaxed = true)

	@Bean
	@Primary
	fun sender() = mockk<Sender>(relaxed = true)

	@Bean
	@Primary
	fun contactPort() = mockk<ContactUseCases>(relaxed = true)

	@Bean
	@Primary
	fun async() = mockk<AsyncTaskExecutor>(relaxed = true)
}

@Import(OtpServiceTestConfig::class)
@SpringBootTest
class OtpServiceTestSpec : FeatureSpec() {
	@Autowired
	lateinit var service: OtpService

	@Autowired
	lateinit var otpRepository: OtpRepository

	@Autowired
	lateinit var sender: Sender

	@Autowired
	lateinit var contactPort: ContactUseCases

	@Autowired
	lateinit var async: AsyncTaskExecutor

	init {
		afterTest {
			clearMocks(contactPort, otpRepository, sender, async)
		}
	}
}
