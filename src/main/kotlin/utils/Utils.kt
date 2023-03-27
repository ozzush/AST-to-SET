package utils

import kotlinVerification.abstractSyntaxTree.Eq
import kotlinVerification.abstractSyntaxTree.Expr
import kotlinVerification.abstractSyntaxTree.NEq
import java.security.InvalidParameterException
import kotlin.reflect.KProperty1

const val INDENT = "    "

@Suppress("UNCHECKED_CAST")
fun <R> readInstanceProperty(instance: Any, propertyName: String): R {
    val property = instance::class.members
        // don't cast here to <Any, R>, it would succeed silently
        .first { it.name == propertyName } as KProperty1<Any, *>
    // force a invalid cast exception if incorrect type here
    return property.get(instance) as R
}

fun negateExpr(eq: Eq) = NEq(eq.left, eq.right)
fun negateExpr(neq: NEq) = Eq(neq.left, neq.right)
fun negateExpr(expr: Expr): Expr {
    if (expr is Eq) return negateExpr(expr)
    if (expr is NEq) return negateExpr(expr)
    throw InvalidParameterException()
}