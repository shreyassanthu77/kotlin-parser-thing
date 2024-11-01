package org.example.interpreter

import java.io.File
import org.example.parser.*

sealed interface InterpretResult {
  data class Success(val value: Int) : InterpretResult
  data class Failure(val message: String) : InterpretResult
}

class Interpreter(private val parser: Parser) {
  constructor(file: File) : this(Parser(file))

  fun interpret(): InterpretResult {
    val res = parser.parse()
    return when (res) {
      is ParseResult.Success -> interpretExpr(res.expr)
      is ParseResult.Failure -> InterpretResult.Failure(res.message)
    }
  }

  private fun interpretExpr(expr: Expr): InterpretResult {
    return when (expr) {
      is Expr.IntLiteral -> InterpretResult.Success(expr.value)
      is Expr.Identifier -> InterpretResult.Failure("unexpected identifier: ${expr.name}")
      is Expr.VoidLiteral -> InterpretResult.Failure("unexpected void literal")
      is Expr.Binary -> {
        val lhs = interpretExpr(expr.left)
        val rhs = interpretExpr(expr.right)
        when {
          lhs is InterpretResult.Failure -> lhs
          rhs is InterpretResult.Failure -> rhs
          else -> {
            val left = (lhs as InterpretResult.Success).value
            val right = (rhs as InterpretResult.Success).value
            val result =
                    when (expr.op) {
                      Expr.BinaryOp.ADD -> left + right
                      Expr.BinaryOp.SUB -> left - right
                      Expr.BinaryOp.MUL -> left * right
                      Expr.BinaryOp.DIV -> left / right
                    }
            InterpretResult.Success(result)
          }
        }
      }
    }
  }
}
