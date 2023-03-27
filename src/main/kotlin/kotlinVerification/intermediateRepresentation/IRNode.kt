package kotlinVerification.intermediateRepresentation

import kotlinVerification.abstractSyntaxTree.Block
import kotlinVerification.abstractSyntaxTree.Expr
import kotlinVerification.abstractSyntaxTree.If
import utils.INDENT

class IRNode(
    val expr: Expr,
    val children: List<IRNode> = emptyList()
) {
    fun propagate(): IRNode {
        when (expr) {
            is Block -> {
                var newChildren = children
                for (expr in expr.exprs.reversed()) {
                    newChildren = listOf(IRNode(expr, newChildren).propagate())
                }
                return newChildren.getOrElse(0) { this }
            }

            is If -> {
                val thenNode = IRNode(expr.thenExpr, children).propagate()
                val elseNode = IRNode(expr.elseExpr ?: Block(), children).propagate()
//              !! Order of `thenNode`, `elseNode` is important !!
                return IRNode(expr, listOf(thenNode, elseNode))
            }

            else -> {
                return this
            }
        }
    }

    override fun toString() = expr.toString()

    fun printTree() = printTree(0)

    private fun printTree(level: Int) {
        fun formatLevel(level: Int, obj: Any) = "${INDENT.repeat(level)}$obj"
        println(formatLevel(level, expr))
        when (expr) {
            is If -> {
                children[0].printTree(level + 1)
                println(formatLevel(level, If.elseFormat))
                children[1].printTree(level + 1)
            }

            else -> for (child in children) child.printTree(level)
        }
    }
}