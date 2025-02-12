package net.serlith.zluket.utils

private val IP_REGEX = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)".toRegex()

fun filterIps(content: String): String = content.replace(IP_REGEX, "0.0.0.0")
