package com.example.project;

import android.content.Context
import com.google.gson.annotations.SerializedName
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Serializable;
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.doAsync
import java.io.OutputStreamWriter
import java.lang.reflect.Type
import java.net.URLEncoder


val PREFS_FILENAME = "com.project.am.le"


fun toJson(state: State): String{
    return Gson().toJson(state)
}
fun fromJson(state: String): State{
    return Gson().fromJson(state, State::class.java)
}
fun saveState(context: Context, state: State) {
    val editor = context.getSharedPreferences(PREFS_FILENAME, 0).edit()
    editor.putString("STATE", toJson(state)).apply()


    var reqParam = URLEncoder.encode("login", "UTF-8") + "=" + URLEncoder.encode(state.logedUser, "UTF-8")
    reqParam += "&" + URLEncoder.encode("score", "UTF-8") + "=" + URLEncoder.encode(state.bestScore.toString(), "UTF-8")
    val mURL = URL("http://10.0.2.2:8000/update")

    try {


        with(mURL.openConnection() as HttpURLConnection) {
            requestMethod = "POST"

            val wr = OutputStreamWriter(getOutputStream());
            wr.write(reqParam);
            wr.flush();

            println("URL : $url")
            println("Response Code : $responseCode")

            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()
                val res = response.toString()
                println(res)

            }
        }
    } catch (ex: java.lang.Exception){
    }


}
fun getRanking(): ArrayList<State.UserScore>? {
    var res: ArrayList<State.UserScore>? = null
    val mURL = URL("http://10.0.2.2:8000/ranking")
    try {

        with(mURL.openConnection() as HttpURLConnection) {
            requestMethod = "GET"



            println("URL : $url")
            println("Response Code : $responseCode")
            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()
                println(response.toString())
                val listType = object : TypeToken<ArrayList<State.UserScore>>(){}.type
                res = Gson().fromJson<ArrayList<State.UserScore>>(response.toString(), listType)

            }
        }
    }catch (ex: Exception){
        println(ex)
    }
    return res
}

fun loadState(context: Context): State{
    val savedState = context.getSharedPreferences(PREFS_FILENAME, 0).getString("STATE", null)
    if(savedState!=null)
        return fromJson(savedState)
    return State()
}


class State : Serializable {

    class UserScore(
        @SerializedName("user") val user: String,
        @SerializedName("score") val score: Int)

    @SerializedName("bestScore") var bestScore: Int = 0
    @SerializedName("logedUser") var logedUser: String = ""
    @SerializedName("ranking") var ranking: ArrayList<UserScore> = ArrayList<UserScore>()



}
