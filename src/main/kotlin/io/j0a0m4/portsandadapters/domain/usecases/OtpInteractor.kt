package io.j0a0m4.portsandadapters.domain.usecases

import io.j0a0m4.portsandadapters.domain.model.SendMethod
import io.j0a0m4.portsandadapters.domain.model.VerificationCode
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.stereotype.Service
import java.util.*

@Service
class OtpInteractor(
	val sender: OtpSender,
	val storage: OtpStorage,
	val contactPort: ContactUseCases,
	val async: AsyncTaskExecutor,
) : OtpUseCases {
	override fun send(contactId: UUID, method: SendMethod) {
		async.execute {
			contactPort.findBy(contactId)
				.map { OtpRecord(it, method) }
				.mapCatching {
					storage persist it
					sender send it
				}.onSuccess { contactPort.pending(contactId) }
		}
	}

	override fun verify(contactId: UUID, method: SendMethod, otp: VerificationCode) =
		storage.retrieve(contactId, method).mapCatching {
			if (it != otp) {
				throw IllegalArgumentException("Invalid OTP provided")
			}
		}.onSuccess {
			contactPort.verified(contactId)
			storage.invalidate(contactId, method)
		}

}