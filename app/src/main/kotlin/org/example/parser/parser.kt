package org.example.parser

import java.io.File
import org.example.lexer.*

sealed interface ParseResult {
  data class Success(val expr: Expr) : ParseResult
  data class Failure(val message: String) : ParseResult
}

class Parser(val lexer: Lexer) {
  private var peek: Token = lexer.next()

  constructor(file: File) : this(Lexer(file))

  fun parse(): ParseResult {
    if (peek.type == TokenType.EOF) {
      return ParseResult.Success(Expr.VoidLiteral(Unit))
    }

    return parseExpr()
  }

  private fun parseExpr(): ParseResult {
    return parseAdditive()
  }

  private fun parseAdditive(): ParseResult {
    var lhs =
            when (val lhs = parseMultiplicative()) {
              is ParseResult.Success -> lhs.expr
              is ParseResult.Failure -> return lhs
            }
    while (peek.type == TokenType.PLUS || peek.type == TokenType.MINUS) {
      val op =
              when (consume().type) {
                TokenType.PLUS -> Expr.BinaryOp.ADD
                TokenType.MINUS -> Expr.BinaryOp.SUB
                else -> return failure("unexpected token: $peek")
              }

      val rhs = parseMultiplicative()
      when (rhs) {
        is ParseResult.Success -> lhs = Expr.Binary(lhs, op, rhs.expr)
        is ParseResult.Failure -> return rhs
      }
    }
    return ParseResult.Success(lhs)
  }

  private fun parseMultiplicative(): ParseResult {
    var lhs =
            when (val lhs = parsePrimary()) {
              is ParseResult.Success -> lhs.expr
              is ParseResult.Failure -> return lhs
            }
    while (peek.type == TokenType.STAR || peek.type == TokenType.SLASH) {
      val op =
              when (consume().type) {
                TokenType.STAR -> Expr.BinaryOp.MUL
                TokenType.SLASH -> Expr.BinaryOp.DIV
                else -> return failure("unexpected token: $peek")
              }

      val rhs = parsePrimary()
      when (rhs) {
        is ParseResult.Success -> lhs = Expr.Binary(lhs, op, rhs.expr)
        is ParseResult.Failure -> return rhs
      }
    }
    return ParseResult.Success(lhs)
  }

  private fun parsePrimary(): ParseResult {
    return when (peek.type) {
      TokenType.NUMBER -> {
        val value = consume().value.toInt()
        success(Expr.IntLiteral(value))
      }
      TokenType.IDENTIFIER -> {
        val name = consume().value
        success(Expr.Identifier(name))
      }
      TokenType.LPAREN -> {
        consume()
        val expr = parseExpr()
        if (peek.type != TokenType.RPAREN) {
          failure("expected ')', but got: $peek")
        } else {
          consume()
          expr
        }
      }
      else -> {
        failure("unexpected token: $peek")
      }
    }
  }

  private fun consume(): Token {
    val token = peek
    peek = lexer.next()
    return token
  }

  private fun success(expr: Expr): ParseResult = ParseResult.Success(expr)
  private fun failure(message: String): ParseResult = ParseResult.Failure(message)
}
