package com.example.project.ui


import android.app.AlertDialog
import android.os.Bundle

import androidx.fragment.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import com.example.project.State

import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.project.R

import com.example.project.loadState
import com.example.project.saveState
import org.jetbrains.anko.doAsync

import kotlin.random.Random


class Game : Fragment() {

    var mbtGame: Button? = null
    var mtvBest: TextView? = null
    var mtvCurrent: TextView? = null
    var metRes: EditText? = null
    var mtvEquation: TextView? = null
    var mtvTime: TextView? = null

    var state: State? = null
    var current: Int = 0
    var time = 0L
    var goal = 0L
    var equation = ""
    var counter: CountDownTimer? = null
    fun startTimer(baseTime: Long = 60000){
        counter = object :CountDownTimer(baseTime, 200) {

            override fun onTick(millisUntilFinished: Long) {
                time = millisUntilFinished
                updateUi()
            }

            override fun onFinish() {
                current = 13
                updateUi()
                AlertDialog.Builder(context)
                    .setTitle("Time ended!")
                    .setMessage("Your score: $current")
                    .show()
                current = 0
                endGame()

            }
        }
        counter?.start()
    }

    fun mod(a:Int, b:Int): Int{
        val res = a%b
        if(res>=0)
            return res
        return res+b
    }
    fun generateEquation():Pair<String, Long>{
        var resString = ""
        var resNumber = 0
        var tmp1 = mod(Random.nextInt(),99)+1
        var tmp2 = mod(Random.nextInt(),99)+1
        var tmp3 = mod(Random.nextInt(),99)+1
        var op1 = mod(Random.nextInt(),2)
        var op2 = mod(Random.nextInt(),2)

        resNumber = tmp1
        resString = tmp1.toString()
        if(op1 == 0) {
            resNumber += tmp2
            resString += " + "+tmp2.toString()
        }else {
            resNumber -= tmp2
            resString += " - "+tmp2.toString()

        }
        if(op2 == 0) {
            resNumber += tmp3
            resString += " + "+tmp3.toString()

        }else {
            resNumber -= tmp3
            resString += " - "+tmp3.toString()
        }
        resString += " = ?"
        return Pair(resString, resNumber.toLong())

    }
    fun startNewGame(){
        mbtGame?.visibility = View.INVISIBLE
        counter?.cancel()
        current = 0
        metRes?.isEnabled = true
        metRes?.text?.clear()
        val tmp = generateEquation()
        goal = tmp.second
        equation = tmp.first
        updateUi()
        startTimer()

    }
    fun endGame(){
        mbtGame?.visibility = View.VISIBLE
        counter?.cancel()
        mtvTime?.setText("Time remaining 60s")
        metRes?.isEnabled = false
        metRes?.text?.clear()
        equation = ""
        goal = 0
        time = 0
        updateUi()
        doAsync {
            saveState(activity!!.applicationContext, state as State)
        }
    }

    fun updateUi(){
        var best = state!!.bestScore
        if(current>best)
        {
            best = current
            state!!.bestScore = current
        }

        mtvEquation?.setText(equation)
        mtvBest?.setText("Best score: $best")
        mtvCurrent?.setText("Current score $current")
        if(time > 0L) {
            mtvTime?.setText("Remaining time: " + time / 1000 +"s")
        }else
            mtvTime?.setText("Time remaining 60s")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(com.example.project.R.layout.fragment_game, container, false)


        metRes = view.findViewById<EditText>(R.id.etRes)
        mbtGame = view.findViewById<Button>(R.id.btGame)
        mtvBest = view.findViewById<TextView>(R.id.tvBest)
        mtvCurrent = view.findViewById<TextView>(R.id.tvCurrent)
        mtvEquation = view.findViewById<TextView>(R.id.tvEqation)
        mtvTime = view.findViewById<TextView>(R.id.tvTime)

        mbtGame?.setOnClickListener{startNewGame()}

        metRes?.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                try {
                    val value = p0.toString().toLong()
                    if(value == goal){
                        val tmp = generateEquation()
                        equation = tmp.first
                        goal = tmp.second
                        current += 1
                        p0?.clear()
                        updateUi()
                    }
                }catch (ex: Exception){
                    Log.println(Log.WARN, null, "Faild to read value "+p0.toString())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        return view
    }


    override fun onResume() {
        super.onResume()
        state = loadState(activity!!.applicationContext)
        current = 0
        mtvBest?.setText(current.toString())
        endGame()
    }



    override fun onDetach() {
        super.onDetach()
        cleanUp()
    }

    fun cleanUp(){
        counter?.cancel()
        if(state != null)
            saveState(activity!!.applicationContext, state as State)
        endGame()
    }


}
