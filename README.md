# Finite State Machine for Kotlin

- Sample

<pre>
        val fsm = Fsm<String, String>("Runner")
<br>
        fsm.addState("walk")
                .addTransition("next", "run")
                .onEnter({ state -> toast("hello state:" + state) })
                .onUpdate({ state ->
                    toast("Update ... walk")
                    textHello.postDelayed({
                        state.transition("next")
                    }, 1000)
                })
<br>

        fsm.addState("run")
                .addTransition("next", "walk")
                .onEnter { s -> toast("onEnter:" + s.name) }
                .onExit { s -> toast("bye bye $s") }
<br>

        fsm.startWithInitialState("walk")

<br>
        buttonUpdate.setOnClickListener({ fsm.update() })
        buttonNext.setOnClickListener({ fsm.transition("next") })
</pre>




# ToDo List
- <S>onChangeState</S>
- <S>isActive</S>
- <S>globalTransition</S>
- <S>generic Transition, genericState</S>
- 중복 State / Transition 생성시 예외 처리