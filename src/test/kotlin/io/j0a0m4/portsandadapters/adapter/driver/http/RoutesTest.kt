package io.j0a0m4.portsandadapters.adapter.driver.http

import io.j0a0m4.portsandadapters.adapter.driver.http.handlers.*
import io.j0a0m4.portsandadapters.adapter.driver.http.request.AddContactRequest
import io.j0a0m4.portsandadapters.adapter.driver.http.request.PatchContactRequest
import io.j0a0m4.portsandadapters.adapter.driver.http.response.toResponse
import io.j0a0m4.portsandadapters.domain.model.SendMethod
import io.j0a0m4.portsandadapters.domain.model.contact
import io.j0a0m4.portsandadapters.domain.usecases.*
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.*
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.*
import java.net.URI
import java.util.*

class RoutesTest() : RoutesTestSpec() {
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

				coEvery {
					contactHandlers.createOne(any())
				} coAnswers {
					val uri = URI("/api/contact/$contactId")
					ServerResponse.created(uri).buildAndAwait()
				}

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
			val request = client.get().uri("/api/contact/${UUID.randomUUID()}")

			When("the contact id is found") {
				coEvery {
					contactHandlers.getOne(any())
				} coAnswers {
					val response = contact {
						it.name = "Jonny Greenwood"
						it.email = "greenwood@radiohead.co.uk"
						it.phone = "+44217742329"
					}.toResponse

					ServerResponse.ok().bodyValueAndAwait(response)
				}

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
				coEvery {
					contactHandlers.getOne(any())
				} coAnswers {
					ServerResponse.notFound().buildAndAwait()
				}

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
				coEvery {
					otpHandlers.createOne(any())
				} coAnswers {
					ServerResponse.accepted().buildAndAwait()
				}

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
			val body = PatchContactRequest(sendMethod, otp)
			val request = client.patch().uri("/api/contact/$contactId")
				.bodyValue(body)

			When("it verifies given OTP matches with stored OTP value") {
				coEvery {
					otpHandlers.verifyOne(any())
				} coAnswers {
					ServerResponse.noContent().buildAndAwait()
				}

				Then("it should return status <204 NO_CONTENT> and empty body") {
					request.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isNoContent
						.expectBody().isEmpty
				}
			}
			When("it verifies given OTP does not match with stored OTP value") {
				coEvery {
					otpHandlers.verifyOne(any())
				} coAnswers {
					ServerResponse.unprocessableEntity().buildAndAwait()
				}

				Then("it should return status <422 UNPROCESSABLE_ENTITY> and empty body") {
					request.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
						.expectBody().isEmpty
				}
			}
			When("given OTP key is not found") {
				coEvery {
					otpHandlers.verifyOne(any())
				} coAnswers {
					ServerResponse.notFound().buildAndAwait()
				}

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

@TestConfiguration
class RoutesTestConfig {
	@Bean
	@Primary
	fun otpHandlers() = mockk<OtpHandlers>(relaxed = true)

	@Bean
	@Primary
	fun contactHandlers() = mockk<ContactHandlers>()
}

@Import(RoutesTestConfig::class)
@WebFluxTest(Routes::class)
open class RoutesTestSpec : BehaviorSpec() {
	@Autowired
	lateinit var client: WebTestClient

	@Autowired
	lateinit var otpHandlers: OtpHandlers

	@Autowired
	lateinit var contactHandlers: ContactHandlers

	init {
		afterTest {
			clearMocks(otpHandlers, contactHandlers)
		}
	}
}
