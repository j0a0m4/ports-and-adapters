package io.j0a0m4.portsandadapters.adapter.driver.http.request

import org.springframework.web.reactive.function.server.ServerRequest
import java.util.*

val ServerRequest.pathId: UUID
	get() = UUID.fromString(pathVariable("id"))

infix fun ServerRequest.locationOf(id: UUID) = uriBuilder()
	.path("/{id}")
	.build(id.toString())
