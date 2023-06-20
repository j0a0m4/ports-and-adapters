package io.j0a0m4.portsandadapters.domain.model

import java.util.*

data class Contact(
	val name: Name,
	val email: Email,
	val phone: Phone,
	val verifiedStatus: VerifiedStatus = VerifiedStatus.Unverified,
	override val id: UUID = UUID.randomUUID()
) : Entity

interface ContactBuilder {
	var name: String
	var email: String
	var phone: String
	var verifiedStatus: VerifiedStatus
}

fun Contact.update(block: ContactBuilder.() -> Unit) =
	object : ContactBuilder {
		override var name = this@update.name.full
		override var email = this@update.email.value
		override var phone = this@update.phone.value
		override var verifiedStatus = this@update.verifiedStatus
	}.apply(block).run {
		Contact(name.splitName, Email(email), Phone(phone), verifiedStatus, id)
	}

fun contact(block: (ContactBuilder) -> Unit) =
	object : ContactBuilder {
		override lateinit var name: String
		override lateinit var email: String
		override lateinit var phone: String
		override var verifiedStatus = VerifiedStatus.Unverified
	}.apply(block).run {
		Contact(name.splitName, Email(email), Phone(phone), verifiedStatus)
	}

val String.splitName: Name
	get() = split(" ").run {
		Name(first(), last())
	}

operator fun Contact.get(method: SendMethod) = when (method) {
	SendMethod.Email -> email
	SendMethod.Phone -> phone
}
