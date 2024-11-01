package org.example.lexer

enum class TokenType {
  EOF,
  IDENTIFIER,
  NUMBER,
  STRING,
  KEYWORD,
  COMMENT,
  PLUS,
  MINUS,
  STAR,
  SLASH,
  PERCENT,
  LPAREN,
  RPAREN,
}

data class Location(val line: Int, val column: Int) {
  override fun toString(): String = "$line:$column"
}

data class Token(val type: TokenType, val value: String, val location: Location) {
  override fun toString(): String = "$type: $value at $location"
}
