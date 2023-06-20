package io.j0a0m4.portsandadapters.domain.model

class Phone(phone: String) :
	ValidatedString(
		value = phone,
		name = "phone",
		regExp = "^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,7}\$"
	)
