package com.gianghv.kmachat.shared.base

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

abstract class Store<S : Store.State, A : Store.Action, E : Store.Effect>(initialState: S) :
    CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.Main) {
    private val _state = MutableStateFlow(initialState)
    private val _effect = MutableStateFlow<E?>(null)

    private val timeCapsule: TimeCapsule<S> = TimeTravelCapsule { storedState ->
        _state.tryEmit(storedState)
    }



    init {
        timeCapsule.addState(initialState)
    }

    abstract val onException: ((Throwable) -> Unit)

    protected var exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onException(throwable)
    }

    fun launch(block: suspend () -> Unit) {
        launch(exceptionHandler) {
            block.invoke()
        }
    }

    fun sendAction(event: A) {
        dispatch(_state.value, event)
    }

    fun setEffect(newEffect: E) {
        _effect.tryEmit(newEffect)
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
    fun observeSideEffect(): Flow<E?> = _effect.asStateFlow().filterNotNull()

    abstract fun dispatch(oldState: S, action: A)
    abstract class State(open val loading: Boolean = false)
    interface Action
    interface Effect
}
