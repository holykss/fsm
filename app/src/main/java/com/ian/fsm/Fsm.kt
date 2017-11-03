package com.ian.fsm

import java.util.*

class Fsm {

    private val statePool = Hashtable<Any, State>()
    private val transitions: Hashtable<Any, Hashtable<Any, Any>> = Hashtable()
    fun transitionWith(state: State, t: Any): State? {
        var nextState = getStateByTransition(state, t)

        if (nextState != null) {
            changeState(nextState)
            return nextState
        }

        return null
    }

    private fun getStateByTransition(state: Any, t: Any): State? {
        val table = transitions[stateAsName(state)]!!
        val s = table[t]
        return when (s) {
            null -> null
            is State -> s
            else -> {
                statePool[s]!!
            }
        }
    }

    private var state: State = State("dummy")

    private fun changeState(nextState: State) {
        state.exit()
        state = nextState
        nextState.enter()
    }

    fun addState(state: Any): State {
        if (state is State) {
            return initializeNewState(state)
        }
        return initializeNewState(buildNewStateWithName(state))
    }

    private fun initializeNewState(state: State): State {
        state.setFsm(this)
        statePool.put(state.name, state)
        addTransitionTableForState(state)
        return state
    }

    private fun buildNewStateWithName(name: Any): State {
        val newState = State(name)
        return newState
    }

    private fun addTransitionTableForState(any: Any): Hashtable<Any, Any> {
        val name = stateAsName(any)

        var table = transitions[name]

        return when (table) {
            null -> {
                val newTable = Hashtable<Any, Any>()
                transitions.put(name, newTable)
                newTable
            }
            else -> table
        }
    }

    fun startWithInitialState(initialState: Any) {
        changeState(asState(initialState))
    }

    private fun stateAsName(state: Any) = when (state) {
        is State -> state.name
        else -> state
    }

    private fun asState(any: Any): State {
        return when (any) {
            is State -> any
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

    fun addTransition(state: Any, transition: Any, target: Any) {
        val table = addTransitionTableForState(state)
        table.put(transition, stateAsName(target))
    }

    fun transition(transition: Any) {
        transitionWith(state, transition)
    }
}