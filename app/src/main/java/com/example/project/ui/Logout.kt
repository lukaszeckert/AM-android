package com.example.project.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.project.MainActivity

import com.example.project.R
import com.example.project.loadState
import com.example.project.saveState


class Logout : Fragment() {


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val state = loadState(activity!!.applicationContext)
        state.logedUser = ""
        state.bestScore = 0
        saveState(activity!!.applicationContext, state)
        val intent = Intent(activity!!.applicationContext, MainActivity::class.java)
        startActivity(intent)
        activity!!.finish()
    }
}
