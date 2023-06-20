@file:Suppress("UnusedReceiverParameter")

package io.j0a0m4.portsandadapters.adapter.driver.http.response

import io.j0a0m4.portsandadapters.adapter.driver.http.Failure
import org.springframework.web.reactive.function.server.*
import java.net.URI

suspend fun <T : Any> Result<T>.okOrElse(failureHandler: Failure.Handler): ServerResponse =
	if (isSuccess) {
		ServerResponse.ok().bodyValueAndAwait(getOrThrow())
	} else {
		failureHandler(exceptionOrNull()).buildAndAwait()
	}

suspend fun Result<URI>.createdOrElse(failureHandler: Failure.Handler) =
	if (isSuccess) {
		ServerResponse.created(getOrThrow())
	} else {
		failureHandler(exceptionOrNull())
	}.buildAndAwait()

suspend fun Unit.accepted() =
	ServerResponse.accepted().buildAndAwait()

suspend fun Result<Unit>.noContentOrElse(failureHandler: Failure.Handler) =
	if (isSuccess) {
		ServerResponse.noContent()
	} else {
		failureHandler(exceptionOrNull())
	}.buildAndAwait()
