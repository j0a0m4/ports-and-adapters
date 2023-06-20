package io.j0a0m4.portsandadapters.domain.usecases

import io.j0a0m4.portsandadapters.domain.OtpMismatch
import io.j0a0m4.portsandadapters.domain.model.SendMethod
import io.j0a0m4.portsandadapters.domain.model.VerificationCode
import io.j0a0m4.portsandadapters.domain.usecases.dependencies.OtpRepository
import io.j0a0m4.portsandadapters.domain.usecases.dependencies.Sender
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.stereotype.Service
import java.util.*

@Service
class OtpService(
	val sender: Sender,
	val storage: OtpRepository,
	val contactPort: ContactUseCases,
	val async: AsyncTaskExecutor,
) : OtpUseCases {
	override fun send(contactId: UUID, sendMethod: SendMethod) {
		async.execute {
			contactPort.findBy(contactId)
				.map { OtpRecord(it, sendMethod) }
				.mapCatching {
					storage persist it
					sender send it
				}.onSuccess { contactPort setToPending contactId }
		}
	}

	override fun verify(contactId: UUID, sendMethod: SendMethod, otp: VerificationCode) =
		storage.retrieveOtp(contactId, sendMethod)
			.mapCatching { if (it != otp) throw OtpMismatch() }
			.onSuccess {
				contactPort setToVerified contactId
				storage.invalidate(contactId, sendMethod)
			}
}
