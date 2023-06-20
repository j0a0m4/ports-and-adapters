package io.j0a0m4.portsandadapters.adapter.driver.http.request


import io.j0a0m4.portsandadapters.domain.model.SendMethod
import org.springframework.web.reactive.function.server.ServerRequest

val ServerRequest.sendMethod: SendMethod
	get() = queryParam("sendMethod")
		.map { SendMethod from it }
		.orElseThrow()

private infix fun SendMethod.Companion.from(param: String) = SendMethod.values()
	.first { it.name.lowercase() == param.lowercase() }
