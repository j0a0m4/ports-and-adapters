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
		if (storage.verify(contactId, method, otp)) {
			Result.success(VerifiedStatus.Verified)
		} else {
			Result.success(VerifiedStatus.Unverified)
		}
}
