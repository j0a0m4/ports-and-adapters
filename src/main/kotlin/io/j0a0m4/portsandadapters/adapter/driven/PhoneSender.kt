package io.j0a0m4.portsandadapters.adapter.driven

import io.j0a0m4.portsandadapters.domain.model.Phone
import io.j0a0m4.portsandadapters.domain.usecases.otp.Sender
import io.j0a0m4.portsandadapters.domain.usecases.otp.VerificationCode
import org.springframework.stereotype.Service

@Service
class PhoneSender() : Sender<Phone> {
	override fun VerificationCode.sendTo(recipient: Phone) {
		println("$this sent to phone [ $recipient ]")
	}
}