package com.zxventures.geladinha.components.common

import com.zxventures.geladinha.components.common.MessageCategory.MessageCategory

case class Message(category: MessageCategory, target: String, message: String, key: String, args: List[Any] = List()) {
  def identifier = List(category.toString, target, message).mkString(".")
}

object MessageCategory extends Enumeration {
  type MessageCategory = Value
  val INFO = Value("INFO")
  val VALIDATION = Value("VALIDATION")
  val ERROR = Value("ERROR")
}

case class Error(message: String)
