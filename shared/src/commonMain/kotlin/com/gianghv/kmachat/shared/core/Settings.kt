package com.gianghv.kmachat.shared.core

class Settings(
    val defaultFeedUrls: Set<String>
) {
    fun isDefault(feedUrl: String) = defaultFeedUrls.contains(feedUrl)
}
