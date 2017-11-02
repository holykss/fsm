package com.example.ian.fsm

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fsm = Fsm()

        fsm.addState("walk")
                .addTransition("next", "run")
                .onEnter({ state -> toast("hello state:" + state) })
                .onUpdate { state ->
                    toast("Update ... walk")
                    textHello.postDelayed({
                        state.transition("next")
                    }, 1000)
                }

        fsm.addState("run")
                .addTransition("next", "walk")
                .onEnter { s -> toast("onEnter:" + s.name) }
                .onExit { s -> toast("bye bye $s") }

        fsm.startWithInitialState("walk")

        buttonUpdate.setOnClickListener({ fsm.update() })
        buttonNext.setOnClickListener({ fsm.transition("next") })
    }

    fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

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

class State(val name: Any) {

    private lateinit var fsm: Fsm
    private var functionOnEnter: (State) -> Unit = {}
    private var functionOnUpdate: (State) -> Unit = {}
    private var functionOnExit: (State) -> Unit = {}

    fun setFsm(fsm: Fsm) {
        this.fsm = fsm
    }

    fun onEnter(function: (State) -> Unit): State {
        this.functionOnEnter = function
        return this
    }

    fun onUpdate(function: (State) -> Unit): State {
        this.functionOnUpdate = function
        return this
    }

    fun onExit(function: (State) -> Unit): State {
        this.functionOnExit = function
        return this
    }

    fun enter() {
        functionOnEnter.invoke(this)
    }

    fun update() {
        functionOnUpdate.invoke(this)
    }

    fun exit() {
        functionOnExit.invoke(this)
    }

    fun transition(t: Any) = fsm.transitionWith(this, t)
    fun addTransition(transition: Any, state: Any): State {
        fsm.addTransition(this, transition, state)
        return this
    }

    override fun toString(): String {
        return name.toString()
    }
}