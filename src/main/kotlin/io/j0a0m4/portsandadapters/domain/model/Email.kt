package io.j0a0m4.portsandadapters.domain.model

class Email(email: String) :
	ValidatedString(
		value = email,
		name = "email",
		regExp = "[^@ \\t\\r\\n]+@[^@ \\t\\r\\n]+\\.[^@ \\t\\r\\n]+"
	)
