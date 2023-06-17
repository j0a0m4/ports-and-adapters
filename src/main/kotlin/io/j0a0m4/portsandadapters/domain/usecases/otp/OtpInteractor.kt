package io.j0a0m4.portsandadapters.domain.usecases.otp

import io.j0a0m4.portsandadapters.domain.model.*
import io.j0a0m4.portsandadapters.domain.usecases.contact.ContactUseCases
import org.springframework.stereotype.Service
import java.util.*

typealias VerificationCode = Int

@Service
class OtpInteractor(
	val phoneOtpSender: OtpSender<Phone>,
	val emailOtpSender: OtpSender<Email>,
	val contactPort: ContactUseCases
) : OtpUseCases {
	val newCode: VerificationCode
		get() = (100000..999999)
			.shuffled()
			.first()

	override fun send(contactId: UUID, sendMethod: SendMethod) =
		contactPort.findBy(contactId).onSuccess {
			when (sendMethod) {
				SendMethod.Phone -> phoneOtpSender.run { newCode sendTo it.phone }
				SendMethod.Email -> emailOtpSender.run { newCode sendTo it.email }
			}
		}
}
