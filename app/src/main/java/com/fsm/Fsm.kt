package com.fsm

import java.util.*

class Fsm(val name: String = "no name") {

    private val statePool = Hashtable<Any, State>()
    private val transitions = Hashtable<Any, Hashtable<Any, Any>>()
    private val globalTransitions = Hashtable<Any, Any>()
    private var state: State = State("dummy")
    private var functionOnChangeState: (State, State) -> Unit = {previous, state ->  }

    fun getCurrent() = state

    fun getStates() = statePool.values

    override fun toString(): String {
        return "Fsm -$name- has ${statePool.size} states.\n${statePool.values}"
    }

    fun addState(state: Any): State {
        if (state is State) {
            return initializeNewState(state)
        }
        return initializeNewState(State(state))
    }

    fun startWithInitialState(initialState: Any) {
        changeState(asState(initialState))
    }

    private fun initializeNewState(state: State): State {
        state.setFsm(this)
        statePool.put(state.name, state)
        addTransitionTableForState(state)
        return state
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

    private fun transitionWith(state: State, t: Any): State? {
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

        if (s!=null) {
            return asState(s)
        }

        var gs = globalTransitions[t]
        if (gs != null) {
            return asState(gs)
        }

        return null
    }

    private fun changeState(nextState: State) {

        functionOnChangeState.invoke(state, nextState)

        state.exit()
        state = nextState
        nextState.enter()
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

    fun addTransition(owner: Any, transition: Any, target: Any) {
        val table = addTransitionTableForState(owner)
        table.put(transition, stateAsName(target))
    }

    fun transition(transition: Any) {
        transitionWith(state, transition)
    }


    fun onChangeState(function: (State, State) -> Unit) {
        this.functionOnChangeState = function
    }

    fun addGlobalTransition(transition: Any, target: Any): Fsm {
        globalTransitions.put(transition, stateAsName(target))
        return this
    }
}