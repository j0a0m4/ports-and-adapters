package io.j0a0m4.portsandadapters.adapter.driver.http.response

import io.j0a0m4.portsandadapters.domain.model.Contact
import io.j0a0m4.portsandadapters.domain.model.VerifiedStatus

data class ContactResponse(
	val status: VerifiedStatus,
	val firstName: String,
	val lastName: String,
	val phone: String,
	val email: String
)

val Contact.toResponse: ContactResponse
	get() = ContactResponse(verifiedStatus, name.first, name.last, phone.value, email.value)
