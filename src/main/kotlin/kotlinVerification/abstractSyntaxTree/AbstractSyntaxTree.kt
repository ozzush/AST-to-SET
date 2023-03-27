package kotlinVerification.abstractSyntaxTree

import utils.readInstanceProperty
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

    override fun equals(other: Any?): Boolean {
        if (other !is Block) return false
        return exprs.contentEquals(other.exprs)
    }

    override fun hashCode(): Int {
        return exprs.contentHashCode()
    }
}

data class Const(val value: Int) : Expr() {
    override fun toString() = value.toString()

    override fun substitute(symbolicStore: List<Expr>) = this
}

data class Var(val name: String) : Expr() {
    override fun toString() = name

    override fun substitute(symbolicStore: List<Expr>) =
        (symbolicStore.first { it is Let && it.variable.name == name } as Let).value
}

data class Let(val variable: Var, val value: Expr) : Expr() {
    override fun toString() = "$variable = $value"

    override fun substitute(symbolicStore: List<Expr>) =
        Let(variable, value.substitute(symbolicStore))
}

data class Eq(val left: Expr, val right: Expr) : Expr() {
    override fun toString() = "$left == $right"

    override fun substitute(symbolicStore: List<Expr>) =
        Eq(left.substitute(symbolicStore), right.substitute(symbolicStore))
}

data class NEq(val left: Expr, val right: Expr) : Expr() {
    override fun toString() = "$left != $right"

    override fun substitute(symbolicStore: List<Expr>) =
        NEq(left.substitute(symbolicStore), right.substitute(symbolicStore))
}

data class If(val cond: Expr, val thenExpr: Expr, val elseExpr: Expr? = null) : Expr() {
    override fun toString() = "if ($cond):"

    companion object {
        const val elseFormat = "else:"
    }
}

data class Plus(val left: Expr, val right: Expr) : Expr() {
    override fun toString() = "($left + $right)"

    override fun substitute(symbolicStore: List<Expr>): Expr {
        val subLeft = left.substitute(symbolicStore)
        val subRight = right.substitute(symbolicStore)
        return if (subLeft is Const && subRight is Const) Const(subLeft.value + subRight.value)
        else Plus(subLeft, subRight)
    }
}

data class Minus(val left: Expr, val right: Expr) : Expr() {
    override fun toString() = "($left - $right)"

    override fun substitute(symbolicStore: List<Expr>): Expr {
        val subLeft = left.substitute(symbolicStore)
        val subRight = right.substitute(symbolicStore)
        return if (subLeft is Const && subRight is Const) Const(subLeft.value - subRight.value)
        else Minus(subLeft, subRight)
    }
}

data class Mul(val left: Expr, val right: Expr) : Expr() {
    override fun toString() = "$left * $right"

    override fun substitute(symbolicStore: List<Expr>): Expr {
        val subLeft = left.substitute(symbolicStore)
        val subRight = right.substitute(symbolicStore)
        return if (subLeft is Const && subRight is Const) Const(subLeft.value * subRight.value)
        else Mul(subLeft, subRight)
    }
}

data class SymVal(val name: String) : Expr() {
    override fun toString() = "SymVal($name)"

    override fun substitute(symbolicStore: List<Expr>) = this
}
