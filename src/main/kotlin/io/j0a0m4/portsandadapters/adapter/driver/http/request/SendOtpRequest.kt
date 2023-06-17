package io.j0a0m4.portsandadapters.adapter.driver.http.request


import io.j0a0m4.portsandadapters.adapter.driver.http.contactId
import io.j0a0m4.portsandadapters.domain.model.SendMethod
import io.j0a0m4.portsandadapters.domain.usecases.otp.SendOtpCommand
import org.springframework.web.reactive.function.server.ServerRequest

infix fun SendOtpCommand.Companion.from(request: ServerRequest) =
	SendOtpCommand(request.contactId, request.sendMethod)

val ServerRequest.sendMethod: SendMethod
	get() = queryParam("method")
		.map { SendMethod from it }
		.orElseThrow()

private infix fun SendMethod.Companion.from(param: String) = SendMethod.values()
	.first { it.name.lowercase() == param.lowercase() }
