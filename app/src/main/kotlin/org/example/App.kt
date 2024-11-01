package org.example

import java.io.File
import kotlin.system.exitProcess
import org.example.interpreter.*

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

  val interpreter = Interpreter(file)
  // interpreter.set("a", 10)
  // interpreter.set("b", 20)
  // interpreter.set("c", 11)
  val res = interpreter.interpret()

  when (res) {
    is InterpretResult.Success -> println("result: ${res.value}")
    is InterpretResult.Failure -> println("Failed to parse: ${res.message}")
  }
}
