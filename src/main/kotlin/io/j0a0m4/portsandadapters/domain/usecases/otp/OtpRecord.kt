package io.j0a0m4.portsandadapters.domain.usecases.otp

import io.j0a0m4.portsandadapters.domain.model.Contact
import io.j0a0m4.portsandadapters.domain.model.SendMethod
import java.util.*

data class OtpRecord(val contact: Contact, val method: SendMethod) {
	val otp = (100000..999999)
		.shuffled()
		.first()

	fun toMap(): Map<Pair<UUID, SendMethod>, VerificationCode> =
		mapOf(contact.id to method to otp)
}