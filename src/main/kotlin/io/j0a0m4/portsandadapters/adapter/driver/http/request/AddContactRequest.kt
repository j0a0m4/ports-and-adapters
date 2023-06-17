package io.j0a0m4.portsandadapters.adapter.driver.http.request

import io.j0a0m4.portsandadapters.domain.model.contact
import io.j0a0m4.portsandadapters.domain.usecases.contact.AddContactCommand
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.awaitBody

data class AddContactRequest(val name: String, val email: String, val phone: String)

suspend infix fun AddContactCommand.Companion.from(request: ServerRequest) =
	request.parseAddContactCommand()
		.run(::AddContactCommand)

suspend fun ServerRequest.parseAddContactCommand() =
	awaitBody<AddContactRequest>().run {
		contact {
			it.name = name
			it.email = email
			it.phone = phone
		}
	}