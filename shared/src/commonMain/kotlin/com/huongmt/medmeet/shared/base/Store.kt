package com.huongmt.medmeet.shared.base

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class Store<S : Store.State, A : Store.Action, E : Store.Effect>(
    initialState: S
) : CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.Main) {
    private val _state = MutableStateFlow(initialState)
    private val _effect = Channel<E>(capacity = Channel.CONFLATED)

    private val timeCapsule: TimeCapsule<S> =
        TimeTravelCapsule { storedState ->
            _state.tryEmit(storedState)
        }

    init {
        timeCapsule.addState(initialState)
    }

    abstract val onException: ((Throwable) -> Unit)

    protected var exceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            onException(throwable)
        }

    protected fun coroutineExceptionHandler(
        onError: (Throwable) -> Unit
    ): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, throwable ->
            onError(throwable)
        }
    }

    fun runFlow(exception: CoroutineExceptionHandler = exceptionHandler, block: suspend () -> Unit) {
        launch(exception) {
            block()
        }
    }

    fun sendAction(event: A) {
        dispatch(_state.value, event)
    }

    fun setEffect(newEffect: E) {
        _effect.trySend(newEffect)
    }

    fun setState(newState: S) {
        val oldState = _state.value
        if (oldState == newState) return
        val success = _state.tryEmit(newState)

//        if (success) {
//            if (BuildConfig.DEBUG && success) {
//                // Only log in debug mode
//                timeCapsule.addState(newState)
//            }
//        }
    }

    fun observeState(): StateFlow<S> = _state.asStateFlow()

    fun observeSideEffect(): Flow<E?> = _effect.receiveAsFlow()

    abstract fun dispatch(
        oldState: S,
        action: A
    )

    abstract class State(
        open val loading: Boolean = false
    )

    interface Action

    interface Effect
}
