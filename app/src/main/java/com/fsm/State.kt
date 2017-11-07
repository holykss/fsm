package com.fsm

class State<S, T>(val name: Any) {

    private lateinit var machine: Fsm<S, T>
    private var functionOnEnter: (state: State<S, T>) -> Unit = {}
    private var functionOnUpdate: (state: State<S, T>) -> Unit = {}
    private var functionOnExit: (state: State<S, T>) -> Unit = {}

    fun setFsm(fsm: Fsm<S, T>) {
        this.machine = fsm
    }

    fun getFsm() = machine

    fun transition(transition: T) = machine.transition(transition)

    fun addTransition(transition: T, state: Any): State<S, T> {
        machine.addTransition(this, transition, state)
        return this
    }

    fun onEnter(function: (state: State<S, T>) -> Unit): State<S, T> {
        this.functionOnEnter = function
        return this
    }

    fun onUpdate(function: (state: State<S, T>) -> Unit): State<S, T> {
        this.functionOnUpdate = function
        return this
    }

    fun onExit(function: (state: State<S, T>) -> Unit): State<S, T> {
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