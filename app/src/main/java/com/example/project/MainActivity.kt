package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {

    var state: State? = null
    fun sendPostRequest(userName:String, password:String, mode:String): String {

        var reqParam = URLEncoder.encode("login", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8")
        reqParam += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8")
        val mURL = URL("http://10.0.2.2:8000/$mode")

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
                    return res
                }
            }
        } catch (ex: java.lang.Exception){
            return ""
        }

    }

    fun displayMenu():Unit{
        if(state != null) //Always true
            saveState(applicationContext, state as State)
        val intent = Intent(applicationContext, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun showToast(msg: String){
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT)
            .show()
    }

    fun validateFields(): Boolean{
        var status = true
        println(etLogin.text)
        if(etLogin.text.toString() == "") {
            showToast("Login must not be empty")
            status = false
        } else if(etPassword.text.toString() == "") {
            showToast("Password must not be empty")
            status = false
        }
        return status
    }


    fun tryLogin(){
        doAsync {
            var resoult = false
            try {
                if(validateFields()){
                val response = sendPostRequest(etLogin.text.toString(), etPassword.text.toString(),
                    "login2")
                    if(response == "OK")
                        resoult = true
                }
            }catch (ex: Exception){
                Log.println(Log.WARN,null, "Faild to login. "+ex.message.toString())

            }

            uiThread {
                if(resoult) {
                    showToast("Successfully sign in ")
                    state?.logedUser = etLogin.text.toString()
                    displayMenu()

                }else
                    showToast("Failed to sign in")
            }

        }

    }

    fun tryRegistry(){
        doAsync {
            var resoult = false
            val user = etLogin.text.toString()
            val password = etPassword.text.toString()
            try {
                if(validateFields()){
                    val response = sendPostRequest(user, password,
                        "signup2")

                    if(response == "OK")
                        resoult = true
                }
            }catch (ex: Exception){
                Log.println(Log.WARN,null, "Faild to registry. "+ex.message.toString())
            }

            uiThread {
                if(resoult) {
                    showToast("Successfully registered")
                    state?.logedUser = user
                    displayMenu()

                }else
                    showToast("Failed to registry")
            }

        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        btLogin.setOnClickListener{tryLogin()}
        btSign.setOnClickListener {tryRegistry()}
        state = loadState(applicationContext)
        if((state as State).logedUser != "")
            displayMenu()

    }
}
