package io.j0a0m4.portsandadapters.adapter.driver.http

import io.j0a0m4.portsandadapters.adapter.NoSuchOtpKey
import io.j0a0m4.portsandadapters.adapter.NoSuchUUID
import io.j0a0m4.portsandadapters.adapter.driver.http.request.AddContactRequest
import io.j0a0m4.portsandadapters.adapter.driver.http.request.PatchContactRequest
import io.j0a0m4.portsandadapters.domain.model.SendMethod
import io.j0a0m4.portsandadapters.domain.model.contact
import io.j0a0m4.portsandadapters.domain.usecases.*
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@ExtendWith(SpringExtension::class)
@WebFluxTest(ContactVerificationApi::class)
internal class ContactVerificationApiTest() : BehaviorSpec() {

	@TestConfiguration
	class ApiTestConfig {
		@Bean
		fun otpPort() = mockk<OtpUseCases>()

		@Bean
		fun contactPort() = mockk<ContactUseCases>()
	}

	@Autowired
	private lateinit var client: WebTestClient

	@Autowired
	private lateinit var otpPort: OtpUseCases

	@Autowired
	private lateinit var contactPort: ContactUseCases

	init {
		Given("a POST request to /api/contact") {
			val request = client.post().uri("/api/contact")
			When("the payload is processable") {
				val payload = AddContactRequest(
					"Thom Yorke",
					"yorke@radiohead.co.uk",
					"+44219847529"
				)
				val contactId = UUID.randomUUID()
				every { contactPort.add(any()) } returns Result.success(contactId)
				Then("it returns status <201 CREATED> and the location of the resource in the header") {
					request.bodyValue(payload)
						.accept(APPLICATION_JSON)
						.exchange()
						.expectHeader().valueMatches("Location", ".*($contactId)")
						.expectStatus().isCreated
						.expectBody().isEmpty
				}
			}
		}
		Given("a GET request to /api/contact/{id}") {
			val contactId = UUID.randomUUID()
			val request = client.get().uri("/api/contact/$contactId")
			When("the contact id is found") {
				every { contactPort.findBy(contactId) } returns
					Result.success(contact {
						it.name = "Jonny Greenwood"
						it.email = "greenwood@radiohead.co.uk"
						it.phone = "+44217742329"
					})
				Then("it returns status <200 OK> and payload in body") {
					request.accept(APPLICATION_JSON)
						.exchange()
						.expectHeader().contentType(APPLICATION_JSON)
						.expectStatus().isOk
						.expectBody()
						.jsonPath("$").isNotEmpty
						.jsonPath("$.status").isEqualTo("Unverified")
						.jsonPath("$.firstName").isEqualTo("Jonny")
						.jsonPath("$.lastName").isEqualTo("Greenwood")
						.jsonPath("$.email").isEqualTo("greenwood@radiohead.co.uk")
						.jsonPath("$.phone").isEqualTo("+44217742329")
				}
			}
			When("the contact id is not found") {
				every { contactPort.findBy(contactId) } returns
					Result.failure(NoSuchUUID(contactId))
				Then("it returns status <404 NOT_FOUND> and empty body") {
					request.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isNotFound
						.expectBody().isEmpty
				}
			}
		}
		Given("a POST request to /api/contact/{id}/otp") {
			val contactId = UUID.randomUUID()
			val sendMethod = SendMethod.Phone
			val uri = "/api/contact/$contactId/otp?sendMethod=$sendMethod"
			val request = client.post().uri(uri)
			When("send method query param is present") {
				every { otpPort.send(contactId, sendMethod) } answers { }
				Then("it should return status <201 ACCEPTED> and empty body") {
					request.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isAccepted
						.expectBody().isEmpty
				}
			}
		}
		Given("a PATCH request to /api/contact/{id}") {
			val contactId = UUID.randomUUID()
			val sendMethod = SendMethod.Phone
			val otp = 423891
			val request = client.patch().uri("/api/contact/$contactId")
				.bodyValue(PatchContactRequest(sendMethod, otp))
			When("it verifies given OTP matches with stored OTP value") {
				every { otpPort.verify(contactId, sendMethod, otp) } returns
					Result.success(Unit)
				Then("it should return status <204 NO_CONTENT> and empty body") {
					request.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isNoContent
						.expectBody().isEmpty
				}
			}
			When("it verifies given OTP does not match with stored OTP value") {
				every { otpPort.verify(contactId, sendMethod, otp) } returns
					Result.failure(OtpMismatch())
				Then("it should return status <422 UNPROCESSABLE_ENTITY> and empty body") {
					request.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
						.expectBody().isEmpty
				}
			}
			When("given OTP key is not found") {
				every { otpPort.verify(contactId, sendMethod, otp) } returns
					Result.failure(NoSuchOtpKey(contactId to sendMethod))
				Then("it should return status <404 NOT_FOUND> and empty body") {
					request.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isNotFound
						.expectBody().isEmpty
				}
			}
		}
	}
}
