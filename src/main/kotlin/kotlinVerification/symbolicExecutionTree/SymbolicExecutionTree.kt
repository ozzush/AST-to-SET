package kotlinVerification.symbolicExecutionTree

import kotlinVerification.abstractSyntaxTree.*
import kotlinVerification.intermediateRepresentation.IRNode
import utils.negateExpr

fun buildExecTree(
    ir: IRNode,
    symbolicStore: List<Expr>,
    pathConstraints: List<Expr> = emptyList(),
): ExecTreeNode {
    val children = when (val expr = ir.expr) {
        is If -> {
            val cond = expr.cond.substitute(symbolicStore)
            val negCond = negateExpr(cond)
            val thenNode = buildExecTree(ir.children[0], symbolicStore, pathConstraints + cond)
            val elseNode = buildExecTree(ir.children[1], symbolicStore, pathConstraints + negCond)
            listOf(thenNode, elseNode)
        }

        is Let -> {
            val symbolicExpr = expr.substitute(symbolicStore)
            val newSymbolicStore =
                symbolicStore.filter { it !is Let || it.variable.name != expr.variable.name } + symbolicExpr
            val nextNode = ir.children.getOrNull(0)?.let { buildExecTree(it, newSymbolicStore, pathConstraints) }
            listOfNotNull(nextNode)

        }

        else -> {
            val nextNode = ir.children.getOrNull(0)?.let { buildExecTree(it, symbolicStore, pathConstraints) }
            listOfNotNull(nextNode)
        }
    }
    return ExecTreeNode(children, ir.expr, symbolicStore, pathConstraints)
}

