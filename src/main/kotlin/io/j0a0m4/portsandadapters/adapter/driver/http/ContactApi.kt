package io.j0a0m4.portsandadapters.adapter.driver.http

import io.j0a0m4.portsandadapters.adapter.driver.http.request.*
import io.j0a0m4.portsandadapters.adapter.driver.http.response.FailureHandler
import io.j0a0m4.portsandadapters.domain.usecases.contact.ContactUseCases
import io.j0a0m4.portsandadapters.domain.usecases.otp.OtpUseCases
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.*

@Configuration
class ContactApi {

	@Bean
	fun otpRoutes(
		otpPort: OtpUseCases,
		contactPort: ContactUseCases,
		failureHandler: FailureHandler
	) = coRouter {
		"/api/contact".nest {
			accept(APPLICATION_JSON).nest {
				POST("/{id}/otp") {
					val contactId = it.parseId()
					val method = it.parseMethod()
					otpPort.send(contactId, method)
					ServerResponse.accepted().buildAndAwait()
				}
				PATCH("/{id}") { request ->
					val contactId = request.parseId()
					request.parseVerification().run {
						otpPort.verify(contactId, method, otp)
					}.fold(
						onSuccess = { ServerResponse.noContent() },
						onFailure = { failureHandler(it) }
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
					contactPort.add(contact).fold(
						onSuccess = { id -> ServerResponse.created(request locationOf id) },
						onFailure = { failureHandler(it) }
					).buildAndAwait()
				}
				GET("/contact/{id}") { request ->
					val contactId = request.parseId()
					contactPort.findBy(contactId).fold(
						onSuccess = { contact -> ServerResponse.ok().bodyValueAndAwait(contact) },
						onFailure = { failureHandler(it).buildAndAwait() }
					)
				}
			}
		}
	}
}
