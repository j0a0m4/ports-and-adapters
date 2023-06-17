package io.j0a0m4.portsandadapters.domain.usecases.otp

import io.j0a0m4.portsandadapters.domain.model.*
import io.j0a0m4.portsandadapters.domain.usecases.contact.ContactUseCases
import org.springframework.stereotype.Service

typealias VerificationCode = Int

@Service
class OtpInteractor(
	val phoneSender: Sender<Phone>,
	val emailSender: Sender<Email>,
	val contactPort: ContactUseCases
) : OtpUseCases {
	val newCode: VerificationCode
		get() = (100000..999999)
			.shuffled()
			.first()

	override fun execute(command: SendOtpCommand) = with(command) {
		contactPort.findBy(id).onSuccess {
			when (sendMethod) {
				SendMethod.Phone -> phoneSender.run { newCode sendTo it.phone }
				SendMethod.Email -> emailSender.run { newCode sendTo it.email }
			}
		}
	}
}
