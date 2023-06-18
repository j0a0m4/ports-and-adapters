package io.j0a0m4.portsandadapters.adapter.driver.http

import io.j0a0m4.portsandadapters.adapter.driver.http.request.*
import io.j0a0m4.portsandadapters.adapter.driver.http.response.FailureHandler
import io.j0a0m4.portsandadapters.adapter.driver.http.response.toResponse
import io.j0a0m4.portsandadapters.domain.usecases.ContactUseCases
import io.j0a0m4.portsandadapters.domain.usecases.OtpUseCases
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.*


@Configuration
class ContactVerificationApi {

	@Bean
	fun otpRoutes(
		otpPort: OtpUseCases,
		contactPort: ContactUseCases,
		failureHandler: FailureHandler
	) = coRouter {
		"/api/contact".nest {
			accept(APPLICATION_JSON).nest {
				POST("/{id}/otp") { request ->
					val contactId = request.parseId()
					val method = request.parseMethod()
					otpPort.send(contactId, method)
					ServerResponse.accepted().buildAndAwait()
				}
				PATCH("/{id}") { request ->
					val contactId = request.parseId()
					request.parseVerification().run {
						otpPort.verify(contactId, method, otp)
					}.fold(
						onSuccess = { ServerResponse.noContent() },
						onFailure = { exception -> failureHandler(exception) }
					).buildAndAwait()
				}
			}
		}
	}

	@Bean
	fun contactRoutes(
		contactPort: ContactUseCases,
		failureHandler: FailureHandler
	) = coRouter {
		"/api".nest {
			accept(APPLICATION_JSON).nest {
				POST("/contact") { request ->
					val contact = request.parseContact()
					contactPort.add(contact)
						.map { id -> request locationOf id }
						.fold(
							onSuccess = { location -> ServerResponse.created(location) },
							onFailure = { exception -> failureHandler(exception) }
						).buildAndAwait()
				}
				GET("/contact/{id}") { request ->
					val contactId = request.parseId()
					contactPort.findBy(contactId)
						.map { contact -> contact.toResponse }
						.fold(
							onSuccess = { responseBody -> ServerResponse.ok().bodyValueAndAwait(responseBody) },
							onFailure = { exception -> failureHandler(exception).buildAndAwait() }
						)
				}
			}
		}
	}
}
