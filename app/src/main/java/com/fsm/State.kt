package com.fsm

class State<T>(val name: Any) {

    private lateinit var machine: Fsm<T>
    private var functionOnEnter: (State<T>) -> Unit = {}
    private var functionOnUpdate: (State<T>) -> Unit = {}
    private var functionOnExit: (State<T>) -> Unit = {}

    fun setFsm(fsm: Fsm<T>) {
        this.machine = fsm
    }

    fun getFsm() = machine

    fun transition(transition: T) = machine.transition(transition)
    
    fun addTransition(transition: T, state: Any): State<T> {
        machine.addTransition(this, transition, state)
        return this
    }

    fun onEnter(function: (State<T>) -> Unit): State<T> {
        this.functionOnEnter = function
        return this
    }

    fun onUpdate(function: (State<T>) -> Unit): State<T> {
        this.functionOnUpdate = function
        return this
    }

    fun onExit(function: (State<T>) -> Unit): State<T> {
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