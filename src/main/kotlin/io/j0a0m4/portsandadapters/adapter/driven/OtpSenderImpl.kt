package io.j0a0m4.portsandadapters.adapter.driven

import io.j0a0m4.portsandadapters.domain.usecases.OtpRecord
import io.j0a0m4.portsandadapters.domain.usecases.OtpSender
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class OtpSenderImpl() : OtpSender {
	private val logger = LoggerFactory.getLogger(javaClass)
	override fun send(record: OtpRecord) {
		// TODO: Implement integration
		logger.info("[ ${javaClass.simpleName} ] sent ${record.otp} to ${record.method}")
	}
}
