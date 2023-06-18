package io.j0a0m4.portsandadapters.adapter.driven

import io.j0a0m4.portsandadapters.domain.model.Contact
import io.j0a0m4.portsandadapters.domain.model.SendMethod
import io.j0a0m4.portsandadapters.domain.usecases.otp.OtpRecord
import io.j0a0m4.portsandadapters.domain.usecases.otp.OtpSender
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class OtpSender() : OtpSender {
	private val logger = LoggerFactory.getLogger(javaClass)
	override fun send(record: OtpRecord) {
		logger.info("otp:${record.otp} method:${record.method}")
	}
}

operator fun Contact.get(method: SendMethod) = when (method) {
	SendMethod.Email -> email
	SendMethod.Phone -> phone
}