package io.j0a0m4.portsandadapters.adapter.driver.http.response

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.reactive.function.server.ServerResponse

@ControllerAdvice
class FailureHandler() {
	val logger: Logger = LoggerFactory.getLogger(javaClass)

	operator fun invoke(e: Throwable) =
		when (e) {
			is NoSuchElementException   -> ServerResponse.notFound()
			is IllegalArgumentException -> ServerResponse.unprocessableEntity()
			else                        -> ServerResponse.badRequest()
		}.also {
			logger.warn("[ Handle ${e.javaClass.name} ] ${e.message}")
		}
}
