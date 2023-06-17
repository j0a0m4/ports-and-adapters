package io.j0a0m4.portsandadapters.domain.model

sealed interface ValueObject<T> {
	val value: T
}
