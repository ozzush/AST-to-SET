package kotlinVerification.intermediateRepresentation

import kotlinVerification.abstractSyntaxTree.Expr

fun buildIR(ast: Expr) = IRNode(ast, emptyList()).propagate()