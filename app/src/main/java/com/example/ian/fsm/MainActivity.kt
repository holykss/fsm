package com.example.ian.fsm

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.fsm.Fsm
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val fsm = Fsm("Runner")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fsm.addState("walk")
                .addTransition("next", "run")
                .onEnter({ state -> toast("hello state:" + state) })
                .onUpdate({ state ->
                    toast("Update ... walk")
                    textHello.postDelayed({
                        state.transition("next")
                    }, 1000)
                })

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

