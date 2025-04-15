package com.gianghv.kmachat.shared.app

import com.gianghv.kmachat.shared.base.Store
import com.gianghv.kmachat.shared.core.RssReader
import com.gianghv.kmachat.shared.core.entity.Feed
import io.github.aakira.napier.Napier

data class FeedState(
    val progress: Boolean,
    val feeds: List<Feed>,
    val selectedFeed: Feed? = null // null means selected all
) : Store.State(progress)

fun FeedState.mainFeedPosts() = (selectedFeed?.posts ?: feeds.flatMap { it.posts }).sortedByDescending { it.date }

sealed class FeedAction : Store.Action {
    data class Refresh(
        val forceLoad: Boolean
    ) : FeedAction()

    data class Add(
        val url: String
    ) : FeedAction()

    data class Delete(
        val url: String
    ) : FeedAction()

    data class SelectFeed(
        val feed: Feed?
    ) : FeedAction()

    data class Data(
        val feeds: List<Feed>
    ) : FeedAction()

    data class Error(
        val error: Exception
    ) : FeedAction()
}

sealed class FeedSideEffect : Store.Effect {
    data class Error(
        val error: Throwable
    ) : FeedSideEffect()
}

class FeedStore(
    private val rssReader: RssReader
) : Store<FeedState, FeedAction, FeedSideEffect>(FeedState(false, emptyList())) {
    override val onException: (Throwable) -> Unit
        get() = {
            Napier.e(tag = "FeedStore", message = "Exception: $it")
            setEffect(FeedSideEffect.Error(it))
        }

    override fun dispatch(
        oldState: FeedState,
        action: FeedAction
    ) {
        Napier.d(tag = "FeedStore", message = "Action: $action")
        val newState =
            when (action) {
                is FeedAction.Add -> {
                    if (oldState.progress) {
                        setEffect(FeedSideEffect.Error(Throwable("In progress")))
                        oldState
                    } else {
                        launch { addFeed(action.url) }
                        FeedState(true, oldState.feeds)
                    }
                }

                is FeedAction.Data -> {
                    if (oldState.progress) {
                        val selected =
                            oldState.selectedFeed?.let {
                                if (action.feeds.contains(it)) it else null
                            }
                        FeedState(false, action.feeds, selected)
                    } else {
                        setEffect(FeedSideEffect.Error(Throwable("Unexpected action")))
                        oldState
                    }
                }

                is FeedAction.Delete -> {
                    if (oldState.progress) {
                        setEffect(FeedSideEffect.Error(Throwable("In progress")))
                        oldState
                    } else {
                        launch { deleteFeed(action.url) }
                        FeedState(true, oldState.feeds)
                    }
                }

                is FeedAction.Error -> {
                    if (oldState.progress) {
                        setEffect(FeedSideEffect.Error(action.error))
                        FeedState(false, oldState.feeds)
                    } else {
                        setEffect(FeedSideEffect.Error(Throwable("Unexpected action")))
                        oldState
                    }
                }

                is FeedAction.Refresh -> {
                    if (oldState.progress) {
                        setEffect(FeedSideEffect.Error(Throwable("In progress")))
                        oldState
                    } else {
                        launch { loadAllFeeds(action.forceLoad) }
                        FeedState(true, oldState.feeds)
                    }
                }

                is FeedAction.SelectFeed -> {
                    if (action.feed == null || oldState.feeds.contains(action.feed)) {
                        oldState.copy(selectedFeed = action.feed)
                    } else {
                        setEffect(FeedSideEffect.Error(Throwable("Unknown feed")))
                        oldState
                    }
                }
            }

        if (newState != oldState) {
            Napier.d(tag = "FeedStore", message = "NewState: $newState")
            setState(newState)
        }
    }

    private suspend fun loadAllFeeds(forceLoad: Boolean) {
        try {
            val allFeeds = rssReader.getAllFeeds(forceLoad)
            sendAction(FeedAction.Data(allFeeds))
        } catch (e: Exception) {
            sendAction(FeedAction.Error(e))
        }
    }

    private suspend fun addFeed(url: String) {
        try {
            rssReader.addFeed(url)
            val allFeeds = rssReader.getAllFeeds(false)
            sendAction(FeedAction.Data(allFeeds))
        } catch (e: Exception) {
            sendAction(FeedAction.Error(e))
        }
    }

    private suspend fun deleteFeed(url: String) {
        try {
            rssReader.deleteFeed(url)
            val allFeeds = rssReader.getAllFeeds(false)
            sendAction(FeedAction.Data(allFeeds))
        } catch (e: Exception) {
            sendAction(FeedAction.Error(e))
        }
    }
}
