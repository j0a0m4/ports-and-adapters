package io.j0a0m4.portsandadapters.domain.usecases.otp

fun interface OtpSender {
	fun send(record: OtpRecord)
}