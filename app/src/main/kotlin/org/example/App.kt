package org.example

import java.io.File
import org.example.lexer.Lexer

fun main(args: Array<String>) {
  val file =
          args.getOrNull(0)?.let { path -> File(path) }
                  ?: run {
                    println("No file path provided")
                    kotlin.system.exitProcess(1)
                  }

  for (token in Lexer(file)) {
    println(token)
  }
}
