package io.j0a0m4.portsandadapters.adapter.driver.http

import io.j0a0m4.portsandadapters.adapter.NoSuchKeyException
import io.j0a0m4.portsandadapters.adapter.driver.http.Failure.Handler
import io.j0a0m4.portsandadapters.domain.DomainException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class Failure {
	private val logger: Logger = LoggerFactory.getLogger(javaClass)

	fun interface Handler {
		operator fun invoke(e: Throwable?): ServerResponse.HeadersBuilder<*>
	}

	@Bean
	fun advice() = Handler {
		logger.warn("FailureHandler [ ${it?.javaClass?.simpleName} ] ${it?.message}")
		when (it) {
			is NoSuchKeyException -> ServerResponse.notFound()
			is DomainException    -> ServerResponse.unprocessableEntity()
			else                  -> ServerResponse.badRequest()
		}.also { response ->
			logger.warn("FailureHandler [ ${it?.javaClass?.simpleName} ] resolved to $response")
		}
	}
}
