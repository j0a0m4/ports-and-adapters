package io.j0a0m4.portsandadapters.domain.usecases.otp

interface OtpSender<T> {
	infix fun VerificationCode.sendTo(recipient: T)
}