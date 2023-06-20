package io.j0a0m4.portsandadapters.adapter.driver.http

import io.j0a0m4.portsandadapters.adapter.driver.http.request.*
import io.j0a0m4.portsandadapters.adapter.driver.http.response.accepted
import io.j0a0m4.portsandadapters.adapter.driver.http.response.noContentOrElse
import io.j0a0m4.portsandadapters.domain.usecases.OtpUseCases
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.ServerRequest

@Controller
class OtpHandlers(
	private val otpPort: OtpUseCases,
	private val failureHandler: Failure.Handler
) {
	suspend fun createOne(request: ServerRequest) =
		with(request) {
			pathId to sendMethod
		}.let { (contactId, method) ->
			otpPort.send(contactId, method)
		}.accepted()

	suspend fun verifyOne(request: ServerRequest) =
		with(request) {
			toVerifyOtpCommand()
		}.let { (contactId, sendMethod, otp) ->
			otpPort.verify(contactId, sendMethod, otp)
		}.noContentOrElse {
			failureHandler(it)
		}
}
