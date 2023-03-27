import kotlinVerification.abstractSyntaxTree.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestSubstitution {
    private val substitutionStore = listOf(
        Let(Var("a"), SymVal("a")),
        Let(Var("x"), Const(2)),
        Let(Var("y"), Plus(SymVal("a"), Const(1)))
    )

    @Test
    fun substituteVar() {
        assertEquals(SymVal("a"), Var("a").substitute(substitutionStore))
        assertEquals(Const(2)   , Var("x").substitute(substitutionStore))
        assertEquals(Plus(SymVal("a"), Const(1)), Var("y").substitute(substitutionStore))
    }

    @Test
    fun substituteExpression() {
        assertEquals(Const(3), Plus(Const(1), Var("x")).substitute(substitutionStore))
        assertEquals(
            Mul(Plus(Const(1), Plus(SymVal("a"), Const(1))), Const(2)),
            Mul(Plus(Const(1), Var("y")                         ), Var("x")).substitute(substitutionStore)
        )
    }
}