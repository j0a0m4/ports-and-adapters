package io.j0a0m4.portsandadapters.adapter.driver.http

import io.j0a0m4.portsandadapters.adapter.driver.http.request.*
import io.j0a0m4.portsandadapters.adapter.driver.http.response.*
import io.j0a0m4.portsandadapters.domain.usecases.ContactUseCases
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.ServerRequest

@Controller
class ContactHandlers(
	val contactPort: ContactUseCases,
	val handleFailure: Failure.Handler
) {

	suspend fun createOne(request: ServerRequest) =
		with(request) {
			toContact()
		}.let { contact ->
			contactPort add contact
		}.map { id ->
			request locationOf id
		}.createdOrElse {
			handleFailure(it)
		}

	suspend fun getOne(request: ServerRequest) =
		with(request) {
			pathId
		}.let { contactId ->
			contactPort findBy contactId
		}.map { contact ->
			contact.toResponse
		}.okOrElse {
			handleFailure(it)
		}
}
