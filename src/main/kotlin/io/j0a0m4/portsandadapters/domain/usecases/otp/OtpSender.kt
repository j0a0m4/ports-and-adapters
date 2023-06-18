package io.j0a0m4.portsandadapters.domain.usecases.otp

fun interface OtpSender {
	infix fun send(record: OtpRecord)
}