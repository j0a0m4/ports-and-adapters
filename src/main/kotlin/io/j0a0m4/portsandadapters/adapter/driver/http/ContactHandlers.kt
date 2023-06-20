package io.j0a0m4.portsandadapters.adapter.driver.http

import io.j0a0m4.portsandadapters.adapter.driver.http.request.*
import io.j0a0m4.portsandadapters.adapter.driver.http.response.toResponse
import io.j0a0m4.portsandadapters.domain.usecases.ContactUseCases
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.*

@Controller
class ContactHandlers(
	val contactPort: ContactUseCases,
	val failureHandler: FailureHandler
) {

	suspend fun createOne(request: ServerRequest) =
		with(request) {
			toContact()
		}.let { contact ->
			contactPort add contact
		}.map { id ->
			request locationOf id
		}.run {
			fold(
				onSuccess = { ServerResponse.created(it) },
				onFailure = failureHandler::invoke
			)
		}.run {
			buildAndAwait()
		}

	suspend fun getOne(request: ServerRequest) =
		with(request) {
			pathId
		}.let { contactId ->
			contactPort findBy contactId
		}.map { contact ->
			contact.toResponse
		}.run {
			fold(
				onSuccess = { ServerResponse.ok().bodyValueAndAwait(it) },
				onFailure = { failureHandler(it).buildAndAwait() }
			)
		}
}
