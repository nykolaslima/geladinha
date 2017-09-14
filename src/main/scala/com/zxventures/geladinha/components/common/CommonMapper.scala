package com.zxventures.geladinha.components.common

import com.zxventures.geladinha.resources.common.{Messages, Message => MessageResource, MessageCategory => MessageCategoryResource}

object CommonMapper {
  def toMessagesResource(messages: List[Message]) = Messages(messages.map(toMessageResource))

  private def toMessageResource(message: Message) = MessageResource(
    category = MessageCategoryResource.fromName(message.category.toString).getOrElse(MessageCategoryResource.fromValue(-1)),
    target = message.target,
    message = message.message,
    key = message.key,
    args = message.args.map(_.toString)
  )
}
