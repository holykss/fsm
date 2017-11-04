package com.fsm

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

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

    lateinit var fsm : Fsm

    @Before
    fun initializeFsm() {
        fsm = Fsm("fsmName")
        fsm.onChangeState { previous, transition, state ->
            println("$previous -> $state by $transition")
        }

        fsm.addGlobalTransition(T.Break, S.Idle)

        fsm.addState(S.Idle)
                .addTransition(T.Fast, S.Walk)

        fsm.addState(S.Walk)

        fsm.addState(S.Run)

        fsm.addState(S.KnockDown)


    }

    @Test
    fun fsmName() {
        assertEquals("fsmName", fsm.name)
    }

    @Test
    fun fsmCount() {
        assertEquals(S.values().size, fsm.getStates().size)
    }

    @Test
    fun IdleStateShouldBeChangedToWalkStateOnTransitionFast() {

        fsm.startWithInitialState(S.Idle)

        assertEquals(S.Idle, fsm.getCurrent().name)

        fsm.transition(T.Fast)

        assertEquals(S.Walk, fsm.getCurrent().name)

    }

}