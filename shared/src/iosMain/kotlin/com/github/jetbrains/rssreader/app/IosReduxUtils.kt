package com.gianghv.kmachat.shared.app

import com.gianghv.kmachat.shared.app.FeedStore
import com.github.jetbrains.rssreader.core.wrap

fun FeedStore.watchState() = observeState().wrap()
fun FeedStore.watchSideEffect() = observeSideEffect().wrap()