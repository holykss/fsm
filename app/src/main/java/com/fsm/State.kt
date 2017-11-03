package com.fsm

class State(val name: Any) {

    private lateinit var machine: Fsm
    private var functionOnEnter: (State) -> Unit = {}
    private var functionOnUpdate: (State) -> Unit = {}
    private var functionOnExit: (State) -> Unit = {}

    fun setFsm(fsm: Fsm) {
        this.machine = fsm
    }

    fun getFsm() = machine

    fun transition(t: Any) = machine.transition(t)
    
    fun addTransition(transition: Any, state: Any): State {
        machine.addTransition(this, transition, state)
        return this
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

    override fun toString(): String {
        return name.toString()
    }
}