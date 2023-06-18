import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.names.TestNameCase
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCaseOrder
import io.kotest.extensions.spring.SpringExtension

class KotestProjectConfig: AbstractProjectConfig() {
	override val parallelism = 4
	override val testNameRemoveWhitespace = true
	override val testNameCase = TestNameCase.Sentence
	override val testCaseOrder = TestCaseOrder.Lexicographic
	override val specExecutionOrder = SpecExecutionOrder.Lexicographic
	override val assertionMode = AssertionMode.Warn

	override fun extensions() = listOf(SpringExtension)
}
