package io.j0a0m4.portsandadapters.domain.usecases.otp

import io.j0a0m4.portsandadapters.domain.model.SendMethod
import io.j0a0m4.portsandadapters.domain.model.VerifiedStatus
import io.j0a0m4.portsandadapters.domain.usecases.contact.ContactUseCases
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.stereotype.Service
import java.util.*

typealias VerificationCode = Int

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
		storage.retrieve(contactId, method).map {
			when (it == otp) {
				true -> handleVerified(contactId, method)
				else -> VerifiedStatus.Unverified
			}
		}.map { status -> contactPort.updateStatus(contactId, status) }

	private fun handleVerified(contactId: UUID, method: SendMethod) =
		VerifiedStatus.Verified.also {
			storage.invalidate(contactId, method)
		}
}
