package io.j0a0m4.portsandadapters.adapter.driver.http.handlers

import io.j0a0m4.portsandadapters.adapter.driver.http.Failure
import io.j0a0m4.portsandadapters.adapter.driver.http.request.*
import io.j0a0m4.portsandadapters.adapter.driver.http.response.accepted
import io.j0a0m4.portsandadapters.adapter.driver.http.response.noContentOrElse
import io.j0a0m4.portsandadapters.domain.usecases.OtpUseCases
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@Controller
class OtpHandlers(
	val otpPort: OtpUseCases,
	val failureHandler: Failure.Handler
) {
	suspend fun createOne(request: ServerRequest): ServerResponse =
		with(request) {
			pathId to sendMethod
		}.let { (contactId, method) ->
			otpPort.send(contactId, method)
		}.accepted()

	suspend fun verifyOne(request: ServerRequest): ServerResponse =
		with(request) {
			toVerifyOtpCommand()
		}.let { (contactId, sendMethod, otp) ->
			otpPort.verify(contactId, sendMethod, otp)
		}.noContentOrElse {
			failureHandler(it)
		}
}