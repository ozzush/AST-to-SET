import kotlinVerification.abstractSyntaxTree.*
import kotlinVerification.intermediateRepresentation.IRNode
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestIRNode {
    @Test
    fun propagateEmptyBlock() {
        val node = IRNode(Block())
        assertEquals(node, node.propagate())
    }

    @Test
    fun propagateBlock() {
        val variable = Var("x")
        val constant = Const(0)
        val node = IRNode(Block(variable, constant))
        assertEquals(
            IRNode(
                variable,
                listOf(
                    IRNode(constant)
                )
            ), node.propagate()
        )
    }

    @Test
    fun propagateIf() {
        val variable = Var("x")
        val cond = Eq(variable, Const(1))
        val ifNode = If(cond, Block(variable))
        val expected = IRNode(
            ifNode,
            listOf(
                IRNode(variable),
                IRNode(Block())
            )
        )
        assertEquals(expected, IRNode(ifNode).propagate())
    }

    @Test
    fun propagateIfElse() {
        val variable = Var("x")
        val cond = Eq(variable, Const(1))
        val ifElseNode = If(cond, Block(variable), Block(cond))
        val expected = IRNode(
            ifElseNode,
            listOf(
                IRNode(variable),
                IRNode(cond)
            )
        )
        assertEquals(expected, IRNode(ifElseNode).propagate())
    }
}