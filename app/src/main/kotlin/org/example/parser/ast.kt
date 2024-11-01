package org.example.parser

sealed interface Expr {
  data class IntLiteral(val value: Int) : Expr
  data class Identifier(val name: String) : Expr
  data class VoidLiteral(val value: Unit) : Expr
  enum class BinaryOp {
    ADD,
    SUB,
    MUL,
    DIV
  }

  data class Binary(val left: Expr, val op: BinaryOp, val right: Expr) : Expr

  fun prettyPrint(): String {
    return when (this) {
      is IntLiteral -> value.toString()
      is Identifier -> name
      is VoidLiteral -> "Unit"
      is Binary -> {
        val left = left.prettyPrint()
        val op =
                when (op) {
                  BinaryOp.ADD -> "+"
                  BinaryOp.SUB -> "-"
                  BinaryOp.MUL -> "*"
                  BinaryOp.DIV -> "/"
                }
        val right = right.prettyPrint()
        "($left $op $right)"
      }
    }
  }
}
