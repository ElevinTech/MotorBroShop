package com.elevintech.motorbroshop.Model

class ChatRoom (
    var lastMessage: ChatMessage = ChatMessage(),
    var participants: Map<String, String> = mapOf()
)