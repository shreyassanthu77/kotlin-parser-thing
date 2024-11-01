package org.example.lexer

import java.io.File

class Lexer(file: File) : Iterator<Token> {
  private var contents = file.readText()
  private var i = 0
  private var line = 1
  private var column = 1
  private var done = false

  override fun hasNext() = !done

  override fun next(): Token {
    if (done) {
      throw NoSuchElementException()
    }

    skipWhitespace()

    val ch = peekCh() ?: return eof()
    when (ch) {
      in '0'..'9' -> return number()
      in 'a'..'z', in 'A'..'Z', '_' -> return identifier()
      '"', '\'' -> return string(nextCh()!!)
      '+' -> return tok(TokenType.PLUS, "+")
      '-' -> return tok(TokenType.MINUS, "-")
      '*' -> return tok(TokenType.STAR, "*")
      '/' -> {
        nextCh()
        if (peekCh() == '/') {
          return comment()
        }
        return tok(TokenType.SLASH, "/")
      }
      '%' -> return tok(TokenType.PERCENT, "%")
      '(' -> return tok(TokenType.LPAREN, "(")
      ')' -> return tok(TokenType.RPAREN, ")")
    }

    done = true
    return eof()
  }

  private fun number(): Token {
    val start = i
    while (peekCh()?.isDigit() == true) {
      nextCh()
    }
    return tok(TokenType.NUMBER, contents.substring(start, i))
  }

  private fun identifier(): Token {
    val start = i
    while (peekCh()?.let { it.isLetterOrDigit() || it == '_' } == true) {
      nextCh()
    }
    val value = contents.substring(start, i)
    return tok(
            when (value) {
              "if", "else", "while", "for", "return" -> TokenType.KEYWORD
              else -> TokenType.IDENTIFIER
            },
            value,
    )
  }

  private fun string(quote: Char): Token {
    val start = i
    while (peekCh() != quote) {
      nextCh()
    }
    nextCh()
    return tok(TokenType.STRING, contents.substring(start, i))
  }

  private fun comment(): Token {
    val start = i
    while (peekCh() != '\n') {
      nextCh()
    }
    return tok(TokenType.COMMENT, contents.substring(start, i))
  }

  private fun skipWhitespace() {
    while (peekCh()?.isWhitespace() == true) {
      nextCh()
    }
  }

  private fun peekCh(): Char? {
    return contents.getOrNull(i)
  }

  private fun nextCh(): Char? {
    val ch = peekCh()
    if (ch == '\n') {
      line++
      column = 1
    } else {
      column++
    }
    i++
    return ch
  }

  private fun eof(): Token {
    done = true
    return tok(TokenType.EOF, "")
  }

  private fun tok(type: TokenType, value: String): Token {
    val token = Token(type, value, loc())
    i += value.length
    column += value.length
    return token
  }

  private fun loc(): Location {
    return Location(line, column)
  }
}
