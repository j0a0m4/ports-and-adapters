package io.j0a0m4.portsandadapters.adapter.driver.http.request

import io.j0a0m4.portsandadapters.domain.model.SendMethod
import io.j0a0m4.portsandadapters.domain.model.VerificationCode
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.awaitBody

data class PatchContactRequest(val method: SendMethod, val otp: VerificationCode)

suspend fun ServerRequest.toVerifyOtpCommand()=
	awaitBody<PatchContactRequest>().run {
		Triple(pathId, method, otp)
	}