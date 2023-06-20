package io.j0a0m4.portsandadapters.adapter.driver.http

import io.j0a0m4.portsandadapters.adapter.driver.http.handlers.ContactHandlers
import io.j0a0m4.portsandadapters.adapter.driver.http.handlers.OtpHandlers
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class Routes {

	@Bean
	fun otpRoutes(handlers: OtpHandlers) = coRouter {
		"/api/contact".nest {
			accept(APPLICATION_JSON).nest {
				POST("/{id}/otp", handlers::createOne)
				PATCH("/{id}", handlers::verifyOne)
			}
		}
	}

	@Bean
	fun contactRoutes(handler: ContactHandlers) = coRouter {
		"/api".nest {
			accept(APPLICATION_JSON).nest {
				POST("/contact", handler::createOne)
				GET("/contact/{id}", handler::getOne)
			}
		}
	}
}
