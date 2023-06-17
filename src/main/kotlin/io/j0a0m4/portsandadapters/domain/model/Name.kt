package io.j0a0m4.portsandadapters.domain.model

data class Name (val first: String, val last: String) {
	val full: String
		get() = "$first $last"
}
