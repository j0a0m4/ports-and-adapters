package io.j0a0m4.portsandadapters.adapter.driver.http

import io.j0a0m4.portsandadapters.adapter.driver.http.request.*
import io.j0a0m4.portsandadapters.domain.usecases.OtpUseCases
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.*

@Controller
class OtpHandlers(
	private val otpPort: OtpUseCases,
	private val failureHandler: FailureHandler
) {
	suspend fun createOne(request: ServerRequest) =
		with(request) {
			pathId to sendMethod
		}.let { (contactId, method) ->
			otpPort.send(contactId, method)
		}.run {
			ServerResponse.accepted().buildAndAwait()
		}

	suspend fun verifyOne(request: ServerRequest) =
		with(request) {
			toVerifyOtpCommand()
		}.let { (contactId, sendMethod, otp) ->
			otpPort.verify(contactId, sendMethod, otp)
		}.run {
			fold(
				onSuccess = { ServerResponse.noContent() },
				onFailure = failureHandler::invoke
			)
		}.run {
			buildAndAwait()
		}
}
