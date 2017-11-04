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
    fun fsm작동법() {

        val fsm = Fsm<S, T>("fsmName")

        assertEquals("fsmName", fsm.name)

    }

}