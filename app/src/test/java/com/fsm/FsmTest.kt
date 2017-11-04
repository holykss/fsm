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

    lateinit var fsm : Fsm<S, T>

    @Before
    fun initializeFsm() {
        fsm = Fsm("fsmName")
        fsm.onTransition { previous, transition, state ->
            println("$previous -> $state by $transition")
        }

        fsm.addGlobalTransition(T.Break, S.Idle)

        fsm.addState(S.Idle)
                .addTransition(T.Fast, S.Walk)

        fsm.addState(S.Walk)
                .addTransition(T.Fast, S.Run)

        fsm.addState(S.Run)
                .addTransition(T.Fast, S.KnockDown)
                .addTransition(T.Break, S.Walk)

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

    @Test
    fun globalTransition() {
        fsm.startWithInitialState(S.Idle)
        fsm.transition(T.Fast)
        assertEquals(S.Walk, fsm.getCurrent().name)
        fsm.transition(T.Break)

        assertEquals(S.Idle, fsm.getCurrent().name)
    }

    @Test
    fun globalTransitionPriority() {
        fsm.startWithInitialState(S.Idle)
        fsm.transition(T.Fast)
        fsm.transition(T.Fast)
        assertEquals(S.Run, fsm.getCurrent().name)
        fsm.transition(T.Break)
        assertEquals(S.Walk, fsm.getCurrent().name)
    }

    @Test
    fun missingTransition() {
        fsm.startWithInitialState(S.Idle)
        fsm.transition(T.Slow)
        assertEquals(S.Idle, fsm.getCurrent().name)
    }

}