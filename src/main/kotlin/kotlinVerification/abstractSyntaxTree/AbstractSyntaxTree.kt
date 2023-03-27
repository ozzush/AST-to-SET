package kotlinVerification.abstractSyntaxTree

import utils.readInstanceProperty
import java.security.InvalidParameterException
import kotlin.reflect.full.declaredMemberProperties

sealed class Expr {
    override fun toString(): String {
        val keyValuePairsFromProperties = this::class.declaredMemberProperties.map {
            "${it.name}: ${readInstanceProperty<Any>(this, it.name)}"
        }
        return "${this::class.simpleName}(${keyValuePairsFromProperties.joinToString()})"
    }

    open fun substitute(symbolicStore: List<Expr>): Expr =
        throw RuntimeException("Substitution is impossible")
}

class Block(vararg val exprs: Expr) : Expr() {
    override fun toString() = "{ ${exprs.joinToString("; ")} }"
}

class Const(val value: Int) : Expr() {
    override fun toString() = value.toString()

    override fun substitute(symbolicStore: List<Expr>) = this
}

class Var(val name: String) : Expr() {
    override fun toString() = name

    override fun substitute(symbolicStore: List<Expr>) =
        (symbolicStore.first { it is Let && it.variable.name == name } as Let).value
}

class Let(val variable: Var, val value: Expr) : Expr() {
    override fun toString() = "$variable = $value"

    override fun substitute(symbolicStore: List<Expr>) =
        Let(variable, value.substitute(symbolicStore))
}

class Eq(val left: Expr, val right: Expr) : Expr() {
    override fun toString() = "$left == $right"

    override fun substitute(symbolicStore: List<Expr>) =
        Eq(left.substitute(symbolicStore), right.substitute(symbolicStore))
}

class NEq(val left: Expr, val right: Expr) : Expr() {
    override fun toString() = "$left != $right"

    override fun substitute(symbolicStore: List<Expr>) =
        NEq(left.substitute(symbolicStore), right.substitute(symbolicStore))
}

class If(val cond: Expr, val thenExpr: Expr, val elseExpr: Expr? = null) : Expr() {
    override fun toString() = "if ($cond):"

    companion object {
        const val elseFormat = "else:"
    }
}

class Plus(val left: Expr, val right: Expr) : Expr() {
    override fun toString() = "($left + $right)"

    override fun substitute(symbolicStore: List<Expr>): Expr {
        val subLeft = left.substitute(symbolicStore)
        val subRight = right.substitute(symbolicStore)
        return if (subLeft is Const && subRight is Const) Const(subLeft.value + subRight.value)
        else Plus(subLeft, subRight)
    }
}

class Minus(val left: Expr, val right: Expr) : Expr() {
    override fun toString() = "($left - $right)"

    override fun substitute(symbolicStore: List<Expr>): Expr {
        val subLeft = left.substitute(symbolicStore)
        val subRight = right.substitute(symbolicStore)
        return if (subLeft is Const && subRight is Const) Const(subLeft.value - subRight.value)
        else Minus(subLeft, subRight)
    }
}

class Mul(val left: Expr, val right: Expr) : Expr() {
    override fun toString() = "$left * $right"

    override fun substitute(symbolicStore: List<Expr>): Expr {
        val subLeft = left.substitute(symbolicStore)
        val subRight = right.substitute(symbolicStore)
        return if (subLeft is Const && subRight is Const) Const(subLeft.value * subRight.value)
        else Mul(subLeft, subRight)
    }
}

class SymVal(val name: String) : Expr() {
    override fun toString() = "SymVal($name)"

    override fun substitute(symbolicStore: List<Expr>) = this
}
