package io.j0a0m4.portsandadapters.adapter.driver.http

import io.j0a0m4.portsandadapters.adapter.driver.http.request.from
import io.j0a0m4.portsandadapters.adapter.driver.http.request.newStatus
import io.j0a0m4.portsandadapters.domain.model.VerifiedStatus
import io.j0a0m4.portsandadapters.domain.model.VerifiedStatus.Unverified
import io.j0a0m4.portsandadapters.domain.model.VerifiedStatus.Verified
import io.j0a0m4.portsandadapters.domain.usecases.contact.*
import io.j0a0m4.portsandadapters.domain.usecases.otp.OtpUseCases
import io.j0a0m4.portsandadapters.domain.usecases.otp.SendOtpCommand
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.*
import java.util.*


val ServerRequest.contactId: UUID
	get() = UUID.fromString(pathVariable("id"))

infix fun ServerRequest.locationOf(id: UUID) = uriBuilder()
	.path("/{id}")
	.build(id.toString())

@Configuration
class ContactApi {
	@Bean
	fun otpRoutes(
		otpPort: OtpUseCases,
		contactPort: ContactUseCases,
		async: AsyncTaskExecutor
	) = coRouter {
		"/api/contact".nest {
			POST("/{id}/otp", accept(APPLICATION_JSON)) { request ->
				async.execute {
					with(otpPort) {
						execute(SendOtpCommand from request)
					}.onSuccess { _ ->
						contactPort(
							UpdateStatusCommand from (request.contactId to Verified)
						)
					}.onFailure { _ ->
						contactPort(
							UpdateStatusCommand from (request.contactId to Unverified)
						)
					}
				}
				ServerResponse.accepted().buildAndAwait()
			}
		}
	}

	@Bean
	fun contactRoutes(contactPort: ContactUseCases) = coRouter {
		"/api".nest {
			accept(APPLICATION_JSON).nest {
				POST("/contact") { request ->
					contactPort(AddContactCommand from request)
						.fold(
							onSuccess = { id -> ServerResponse.created(request locationOf id) },
							onFailure = { ServerResponse.badRequest() }
						).buildAndAwait()
				}
				GET("/contact/{id}") { request ->
					contactPort(FindByIdCommand from request)
						.fold(
							onSuccess = { contact -> ServerResponse.ok().bodyValueAndAwait(contact) },
							onFailure = { ServerResponse.notFound().buildAndAwait() }
						)
				}
				PATCH("/contact/{id}") {
					contactPort(
						UpdateStatusCommand from (it.contactId to it.newStatus())
					).fold(
						onSuccess = { ServerResponse.noContent() },
						onFailure = { ServerResponse.notFound() }
					).buildAndAwait()
				}
			}
		}
	}
}

infix fun UpdateStatusCommand.Companion.from(pair: Pair<UUID, VerifiedStatus>) =
	pair.let { (id, newStatus) -> UpdateStatusCommand(id, newStatus) }

infix fun FindByIdCommand.Companion.from(it: ServerRequest) = FindByIdCommand(it.contactId)
