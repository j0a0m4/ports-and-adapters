package io.j0a0m4.portsandadapters.domain.usecases

fun interface OtpSender {
	infix fun send(record: OtpRecord)
}