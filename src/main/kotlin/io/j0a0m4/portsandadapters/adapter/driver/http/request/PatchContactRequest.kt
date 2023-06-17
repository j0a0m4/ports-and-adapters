package io.j0a0m4.portsandadapters.adapter.driver.http.request

import io.j0a0m4.portsandadapters.domain.model.VerifiedStatus
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.awaitBody

data class PatchContactRequest(val status: VerifiedStatus)

suspend fun ServerRequest.parseStatus() =
	awaitBody<PatchContactRequest>().status