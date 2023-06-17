package io.j0a0m4.portsandadapters.domain.usecases.otp

interface Sender<T> {
	infix fun VerificationCode.sendTo(recipient: T)
}