package io.j0a0m4.portsandadapters.domain

sealed class DomainException(override val message: String) : Exception()

class OtpMismatch() : DomainException("OTP provided doesn't match OTP sent")
