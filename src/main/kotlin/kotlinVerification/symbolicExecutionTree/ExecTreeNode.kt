package kotlinVerification.symbolicExecutionTree

import kotlinVerification.abstractSyntaxTree.Expr
import kotlinVerification.abstractSyntaxTree.If
import utils.INDENT

data class ExecTreeNode(
    val children: List<ExecTreeNode>,
    val nextExpr: Expr,
    val S: List<Expr>,
    val Pi: List<Expr>
) {
    override fun toString() = nextExpr.toString()

    private fun formatForPrintTree(pad1: Int, pad2: Int, level: Int, obj: Any) =
        "${INDENT.repeat(level)}$obj".padEnd(pad1) + "Pi: ${(Pi).joinToString()}".padEnd(pad2) + "S: ${S.joinToString()}"

    fun printTree(pad1: Int = 30, pad2: Int = 50) = printTree(pad1, pad2, 0)

    private fun printTree(pad1: Int, pad2: Int, level: Int) {
        val cond = (nextExpr as? If)?.cond?.substitute(S)
        println(formatForPrintTree(pad1, pad2, level, nextExpr))
        when (nextExpr) {
            is If -> {
                children[0].printTree(pad1, pad2, level + 1)
                println(formatForPrintTree(pad1, pad2, level, If.elseFormat))
                children[1].printTree(pad1, pad2, level + 1)
            }

            else -> {
                for (child in children) {
                    child.printTree(pad1, pad2, level)
                }
            }
        }
    }
}