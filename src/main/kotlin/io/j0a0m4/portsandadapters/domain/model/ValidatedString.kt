package io.j0a0m4.portsandadapters.domain.model

abstract class ValidatedString(
	final override val value: String,
	private val name: String,
	regExp: String
) : ValueObject<String> {
	init {
		require(value matches regExp.toRegex()) {
			"[ $value ] is an invalid $name"
		}
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		other as ValidatedString
		return value == other.value
	}

	override fun hashCode() = value.hashCode()

	override fun toString() = "ValidatedString(value='$value', name='$name')"
}