import kotlinVerification.abstractSyntaxTree.*
import kotlinVerification.intermediateRepresentation.buildIR
import kotlinVerification.symbolicExecutionTree.buildExecTree

fun main() {
    val ir = buildIR(
        Block(
            Let(Var("x"), Const(1)),
            Let(Var("y"), Const(0)),
            If(
                NEq(Var("a"), Const(0)),
                Block(
                    Let(Var("y"), Plus(Const(3), Var("x"))),
                    If(
                        Eq(Var("b"), Const(0)),
                        Let(Var("x"), Mul(Const(2), Plus(Var("a"), Var("b")))),
                    )
                )
            ),
            Minus(Var("x"), Var("y"))
        )
    )
//    println("===== Intermediate Representation =====")
//    ir.printTree()
    val input = listOf(
        Let(Var("a"), SymVal("a")),
        Let(Var("b"), SymVal("b")),
    )
    val se = buildExecTree(ir, input)

    println("===== Symbolic Execution Tree =====")
    se.printTree(30, 50)
}