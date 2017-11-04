package com.fsm

import java.util.*

class Fsm<S, T>(val name: String = "no name") {

    private val statePool = Hashtable<Any, State<S, T>>()
    private val transitions = Hashtable<Any, Hashtable<T, Any>>()
    private val globalTransitions = Hashtable<T, Any>()
    private var state: State<S, T> = State("dummy")
    private var functionOnTransition: (State<S, T>, T, State<S, T>) -> Unit = { previous, transition, next -> }

    fun getCurrent() = state

    fun getStates() = statePool.values

    override fun toString(): String {
        return "Fsm -$name- has ${statePool.size} states.\n${statePool.values}"
    }

    fun addState(state: Any): State<S, T> {
        if (state is State<*, *>) {
            return initializeNewState(state as State<S, T>)
        }
        return initializeNewState(State(state))
    }

    fun startWithInitialState(initialState: S) {
        changeState(asState(initialState!!))
    }

    private fun initializeNewState(state: State<S, T>): State<S, T> {
        state.setFsm(this)
        statePool.put(state.name, state)
        addTransitionTableForState(state)
        return state
    }

    private fun addTransitionTableForState(any: Any): Hashtable<T, Any> {
        val name = stateAsName(any)

        var table = transitions[name]

        return when (table) {
            null -> {
                val newTable = Hashtable<T, Any>()
                transitions.put(name, newTable)
                newTable
            }
            else -> table
        }
    }

    private fun transitionWith(state: State<S, T>, transition: T): State<S, T>? {
        var nextState = getStateByTransition(state, transition)

        if (nextState != null) {
            functionOnTransition.invoke(state, transition, nextState)
        }

        if (nextState != null) {
            changeState(nextState)
            return nextState
        }

        return null
    }

    private fun getStateByTransition(state: Any, transition: T): State<S, T>? {
        val table = transitions[stateAsName(state)]!!
        val s = table[transition]

        if (s != null) {
            return asState(s)
        }

        var gs = globalTransitions[transition]
        if (gs != null) {
            return asState(gs)
        }

        return null
    }

    private fun changeState(nextState: State<S, T>) {

        state.exit()
        state = nextState
        nextState.enter()
    }

    private fun stateAsName(state: Any) = when (state) {
        is State<*, *> -> state.name
        else -> state
    }

    private fun asState(any: Any): State<S, T> {
        return when (any) {
            is State<*, *> -> any as State<S, T>
            else -> {
                var state = statePool[any]
                if (state != null) {
                    return state
                }
                throw NullPointerException("State $any not found in state pool")
            }
        }
    }

    fun update() {
        state.update()
    }

    fun addTransition(owner: Any, transition: T, target: Any) {
        val table = addTransitionTableForState(owner)
        table.put(transition, stateAsName(target))
    }

    fun transition(transition: T) {
        transitionWith(state, transition)
    }


    fun onTransition(function: (State<S, T>, T, State<S, T>) -> Unit) {
        this.functionOnTransition = function
    }

    fun addGlobalTransition(transition: T, target: Any): Fsm<S, T> {
        globalTransitions.put(transition, stateAsName(target))
        return this
    }
}