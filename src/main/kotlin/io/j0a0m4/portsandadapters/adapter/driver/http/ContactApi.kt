package io.j0a0m4.portsandadapters.adapter.driver.http

import io.j0a0m4.portsandadapters.adapter.driver.http.request.*
import io.j0a0m4.portsandadapters.domain.usecases.contact.ContactUseCases
import io.j0a0m4.portsandadapters.domain.usecases.otp.OtpUseCases
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.*
import java.util.*


fun ServerRequest.parseId(): UUID =
	UUID.fromString(pathVariable("id"))

infix fun ServerRequest.locationOf(id: UUID) = uriBuilder()
	.path("/{id}")
	.build(id.toString())

@Configuration
class ContactApi {
	@Bean
	fun otpRoutes(otpPort: OtpUseCases, contactPort: ContactUseCases, async: AsyncTaskExecutor) = coRouter {
		"/api/contact".nest {
			accept(APPLICATION_JSON).nest {
				POST("/{id}/otp") { request ->
					val contactId = request.parseId()
					val sendMethod = request.parseMethod()
					async.execute {
						otpPort.send(contactId, sendMethod)
							.onSuccess { _ -> contactPort.pending(contactId) }
							.onFailure { _ -> contactPort.unverified(contactId) }
					}
					ServerResponse.accepted().buildAndAwait()
				}
				PATCH("/{id}") { request ->
					val contactId = request.parseId()
					request.parseVerification().run {
						otpPort.verify(contactId, method, otp)
					}.map { status ->
						contactPort.updateStatus(contactId, status)
					}.fold(
						onSuccess = { ServerResponse.noContent() },
						onFailure = { ServerResponse.notFound() }
					).buildAndAwait()
				}
			}
		}
	}

	@Bean
	fun contactRoutes(contactPort: ContactUseCases) = coRouter {
		"/api".nest {
			accept(APPLICATION_JSON).nest {
				POST("/contact") { request ->
					val contact = request.parseContact()
					contactPort.add(contact).fold(
						onSuccess = { id -> ServerResponse.created(request locationOf id) },
						onFailure = { ServerResponse.badRequest() }
					).buildAndAwait()
				}
				GET("/contact/{id}") { request ->
					val contactId = request.parseId()
					contactPort.findBy(contactId).fold(
						onSuccess = { contact -> ServerResponse.ok().bodyValueAndAwait(contact) },
						onFailure = { ServerResponse.notFound().buildAndAwait() }
					)
				}
			}
		}
	}
}
