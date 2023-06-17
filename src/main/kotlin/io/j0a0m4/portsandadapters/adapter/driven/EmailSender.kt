package io.j0a0m4.portsandadapters.adapter.driven

import io.j0a0m4.portsandadapters.domain.model.Email
import io.j0a0m4.portsandadapters.domain.usecases.otp.Sender
import io.j0a0m4.portsandadapters.domain.usecases.otp.VerificationCode
import org.springframework.stereotype.Service

@Service
class EmailSender : Sender<Email> {
	override fun VerificationCode.sendTo(recipient: Email) {
		println("$this sent to phone [ $recipient ]")
	}
}