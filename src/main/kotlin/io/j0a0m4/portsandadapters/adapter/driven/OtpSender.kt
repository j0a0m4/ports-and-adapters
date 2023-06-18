package io.j0a0m4.portsandadapters.adapter.driven

import io.j0a0m4.portsandadapters.domain.usecases.otp.OtpRecord
import io.j0a0m4.portsandadapters.domain.usecases.otp.OtpSender
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class OtpSender() : OtpSender {
	private val logger = LoggerFactory.getLogger(javaClass)
	override fun send(record: OtpRecord) {
		// TODO: Implement integration
		logger.info("[ Handle ${javaClass.name} ] otp(${record.otp}) method(${record.method})")
	}
}
