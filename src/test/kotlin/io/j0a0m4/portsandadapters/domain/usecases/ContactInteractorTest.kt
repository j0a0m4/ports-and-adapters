package io.j0a0m4.portsandadapters.domain.usecases

import io.j0a0m4.portsandadapters.adapter.NoSuchUUID
import io.j0a0m4.portsandadapters.domain.model.VerifiedStatus
import io.j0a0m4.portsandadapters.domain.model.contact
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.mockk.*
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@SpringBootTest
class ContactInteractorTest : FeatureSpec() {
	@TestConfiguration
	class ContactInteractorTestConfig {
		@Bean
		fun storage() = mockk<ContactStorage>(relaxed = true)
	}

	@Autowired
	private lateinit var interactor: ContactInteractor

	@Autowired
	private lateinit var storage: ContactStorage

	private val contact = contact {
		it.name = "Dustin Kensrue"
		it.email = "dustin@thrice.org"
		it.phone = "+1923543194"
	}

	init {
		afterTest {
			clearMocks(storage)
		}

		feature("add contact") {
			scenario("should persist to storage and return new UUID") {
				assertDoesNotThrow {
					interactor.add(contact) shouldBeSuccess contact.id
				}
				verify(exactly = 1) {
					storage persist contact
				}
			}
		}

		feature("update contact status by id") {
			every { storage.update(any()) } answers { Result.success(contact) }

			scenario("should persist to storage") {
				forAll(
					row(VerifiedStatus.Verified),
					row(VerifiedStatus.Unverified),
					row(VerifiedStatus.Pending),
				) { status ->
					assertDoesNotThrow {
						interactor.updateStatus(contact.id, status)
					}
				}

				verify(exactly = 3) {
					storage update (any())
				}
			}
		}

		feature("find contact by id") {
			scenario("should find contact") {
				every { storage.findBy(contact.id) } answers { Result.success(contact) }

				interactor.findBy(contact.id) shouldBeSuccess contact

				verify(exactly = 1) {
					storage.findBy(contact.id)
				}
			}
			scenario("id is not found") {
				val exception = NoSuchUUID(contact.id)

				every { storage.findBy(contact.id) } answers { Result.failure(exception) }

				interactor.findBy(contact.id) shouldBeFailure exception

				verify(exactly = 1) {
					storage.findBy(contact.id)
				}
			}
		}
	}
}
