package com.fsm

import org.junit.Test

import org.junit.Assert.*

/**
 * Created by ian on 2017. 11. 4..
 */
class FsmTest {

    enum class S {
        Idle,
        Run,
        Walk,
        KnockDown,
    }

    enum class T {
        Fast,
        Slow,
        Break,
    }

    @Test
    fun fsmName() {
        val fsm = Fsm("fsmName")

        assertEquals("fsmName", fsm.name)
    }

    @Test
    fun fsmCount() {
        val fsm = Fsm("fsmName")

        fsm.onChangeState { previous, state ->
            println("$previous -> $state")
        }

        fsm.addState(S.Idle)
                .addTransition(T.Fast, S.Walk)

        fsm.addState(S.Walk)

        assertEquals(2, fsm.getStates().size)
    }

    @Test
    fun IdleStateShouldBeChangedToWalkStateOnTransitionFast() {
        val fsm = Fsm("fsmName")

        assertEquals("fsmName", fsm.name)

        fsm.addGlobalTransition(T.Break, S.Idle)

        fsm.addState(S.Idle)
                .addTransition(T.Fast, S.Walk)

        fsm.addState(S.Walk)

        fsm.startWithInitialState(S.Idle)

        fsm.transition(T.Fast)

        assertEquals(S.Walk, fsm.getCurrent().name)

    }

}