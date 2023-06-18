package io.j0a0m4.portsandadapters.adapter.driver.http.response

import io.j0a0m4.portsandadapters.adapter.NoSuchKeyException
import io.j0a0m4.portsandadapters.domain.usecases.DomainException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.reactive.function.server.ServerResponse

@ControllerAdvice
class FailureHandler() {
	val logger: Logger = LoggerFactory.getLogger(javaClass)

	operator fun invoke(e: Throwable) =
		when (e) {
			is NoSuchKeyException -> ServerResponse.notFound()
			is DomainException    -> ServerResponse.unprocessableEntity()
			else                   -> ServerResponse.badRequest()
		}.also {
			logger.warn("FailureHandler [ ${e.javaClass.simpleName} ] ${e.message}")
		}
}
