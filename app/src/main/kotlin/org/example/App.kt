package org.example

import java.io.File
import kotlin.system.exitProcess
import org.example.parser.ParseResult
import org.example.parser.Parser

fun main(args: Array<String>) {
  val filePath = args.firstOrNull()
  if (filePath == null) {
    println("Usage: App <file>")
    return
  }

  val file =
          File(filePath).apply {
            when {
              !isFile -> {
                println("Not a file: $filePath!")
                exitProcess(1)
              }
              !exists() -> {
                println("File not found: $filePath!")
                exitProcess(1)
              }
            }
          }

  val parser = Parser(file)
  val res = parser.parse()

  when (res) {
    is ParseResult.Success -> println("Parsed: ${res.expr.prettyPrint()}")
    is ParseResult.Failure -> println("Failed to parse: ${res.message}")
  }
}
