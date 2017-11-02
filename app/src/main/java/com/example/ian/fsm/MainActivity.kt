package com.example.ian.fsm

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fsm = Fsm()

        val stateWalk = object : State("walk") {
            override fun onEnter() {
                toast("onEnter:stateWalk")
            }

            override fun onUpdate() {
                toast("onUpdate:stateWalk")

                textHello.postDelayed({
                    transition("next")
                }, 1000)

            }

            override fun onExit() {
                toast("onExit:stateWalk")
            }
        }

        fsm.setOnChangeState(object : Fsm.OnChangeStateListener {
            override fun onChangeState(state: State) {
                buttonUpdate?.text = state.name.toString()
            }
        })

        fsm.addState(stateWalk)
        fsm.addState(object :State("run"){
            override fun onEnter() {
                toast("onEnter:run")
            }

            override fun onExit() {
                toast("onEnter:run")
            }

            override fun onUpdate() {
                toast("onEnter:run")
            }
        })

        stateWalk.addTransition("next", "run")

        fsm.startWithInitialState(stateWalk)


        buttonUpdate.setOnClickListener(View.OnClickListener { fsm.update() })
    }

    fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}

class Fsm {

    interface OnChangeStateListener {
        fun onChangeState(state: State)
    }

    private val statePool = Hashtable<Any, State>()
    private val transitions: Hashtable<State, Hashtable<Any, Any>> = Hashtable()
    fun transitionWith(state: State, t: Any): State? {
        var nextState = getStateByTransition(state, t)

        if (nextState != null) {
            changeState(nextState)
            return nextState
        }

        return null
    }

    private fun getStateByTransition(state: State, t: Any): State {
        val s = transitions[state.name]!![t]
        return when {
            s is State -> s
            else -> {
                statePool[s]!!
            }
        }
    }

    private var state: State = object : State("dummy") {
        override fun onEnter() {
        }

        override fun onExit() {
        }

        override fun onUpdate() {
        }
    }
    private var onChangeStateListener: Fsm.OnChangeStateListener = object : Fsm.OnChangeStateListener {
        override fun onChangeState(state: State) {
        }
    }

    private fun changeState(nextState: State) {
        if (state != null) {
            state.onExit()
        }
        state = nextState
        onChangeStateListener.onChangeState(nextState)
        nextState.onEnter()
    }


    fun addState(state: State) {
        state.setFsm(this)
        transitions.put(state, Hashtable())
        statePool.put(state.name, state)
    }

    fun startWithInitialState(initialState: State) {
        changeState(initialState)
    }


    fun setOnChangeState(onChangeStateListener: Fsm.OnChangeStateListener) {
        this.onChangeStateListener = onChangeStateListener
    }

    fun update() {
        state.onUpdate()
    }

    fun addTransition(stateBefore: State, transition: Any, stateTarget: Any) {
        val table = transitions[stateBefore]
        table!!.put(transition, stateTarget)
    }
}

abstract class State(val name: Any) {
    private lateinit var fsm: Fsm

    fun setFsm(fsm: Fsm) {
        this.fsm = fsm
    }

    abstract fun onEnter()
    abstract fun onExit()
    abstract fun onUpdate()

    fun transition(t: Any) = fsm.transitionWith(this, t)
    fun addTransition(transition: Any, state: Any) {
        fsm.addTransition(this, transition, state)
    }

}