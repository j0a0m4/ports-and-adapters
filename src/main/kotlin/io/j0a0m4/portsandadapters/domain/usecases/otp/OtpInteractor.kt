package io.j0a0m4.portsandadapters.domain.usecases.otp

import io.j0a0m4.portsandadapters.domain.model.SendMethod
import io.j0a0m4.portsandadapters.domain.model.VerifiedStatus
import io.j0a0m4.portsandadapters.domain.usecases.contact.ContactUseCases
import org.springframework.stereotype.Service
import java.util.*

typealias VerificationCode = Int

@Service
class OtpInteractor(
	val sender: OtpSender,
	val storage: OtpStorage,
	val contactPort: ContactUseCases
) : OtpUseCases {
	override fun send(contactId: UUID, method: SendMethod) =
		contactPort.findBy(contactId)
			.map { OtpRecord(it, method) }
			.mapCatching {
				storage.persist(it)
				sender.send(it)
			}

	override fun verify(contactId: UUID, method: SendMethod, otp: VerificationCode) =
		storage.verify(contactId, method, otp)
			.map { it.toStatus() }
			.onSuccess { handleInvalidation(it, contactId, method) }

	private fun handleInvalidation(status: VerifiedStatus, contactId: UUID, method: SendMethod) {
		if (status == VerifiedStatus.Verified) {
			storage.invalidate(contactId, method)
		}
	}
}

private fun Boolean.toStatus() =
	when (this) {
		true -> VerifiedStatus.Verified
		else -> VerifiedStatus.Unverified
	}
