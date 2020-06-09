package com.example.tp2

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.preference.PreferenceManager
import com.example.tp2.lists.ProfilListeToDo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.io.File

class MainActivity : GenericActivity(), View.OnClickListener {

    private var refBtnOK: Button? = null
    private var refPseudoInput: AutoCompleteTextView? = null
    private var prefs : SharedPreferences ?= null
    private var filename : String? = null

    private val activityScope = CoroutineScope(
        SupervisorJob()
                + Dispatchers.Main
                + CoroutineExceptionHandler { _, throwable ->
            Log.e("PMR", "CoroutineExceptionHandler : ${throwable.message}")
        }
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
        Declarations
         */
        refBtnOK = findViewById(R.id.OKBtnMain)
        refPseudoInput = findViewById(R.id.pseudoInputMain)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        filename = "players"

        refBtnOK?.let { btn -> btn.setOnClickListener(this) }

        /*
        Check if the players file exist
         */
        val file = File(filesDir, filename)
        if (!file.exists()) {
            file.createNewFile()
        }


        /*
        Set the auto-completion
         */
        autoCompletion()


        /*
        Check device connection
         */
        checkConnection()
    }


    override fun onStart() {
        super.onStart()

        val pseudoPref : String? = prefs!!.getString("pseudo", "Pseudo")
        refPseudoInput?.setText(pseudoPref)
        autoCompletion()
    }


    /*
    Check device connection
     */
    private fun checkConnection() {
        activityScope.launch {
            refBtnOK!!.isEnabled = false

            val users = DataProvider.getUsersFromApi()

            refBtnOK!!.isEnabled = true
        }
    }

    /*
    OK Button Listener
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.OKBtnMain -> {
                val pseudo = refPseudoInput!!.text.toString()

                val editor : SharedPreferences.Editor = prefs!!.edit()
                editor.putString("pseudo", pseudo)
                editor.commit()

                val bundle = Bundle()
                bundle.putString("pseudo", pseudo)
                val intent = Intent(this, ChoixListActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }


    /*
    Set the auto-completion
     */
    private fun autoCompletion() {
        val file = File(filesDir, filename)
        var jsonProfiles : String = file.readText()
        val gson = Gson()
        val listPlayerType = object : TypeToken<List<ProfilListeToDo>>() {}.type
        var listPlayer : MutableList<ProfilListeToDo>? = gson.fromJson(jsonProfiles, listPlayerType)
        if (listPlayer == null) {
            listPlayer = mutableListOf()
        }
        var pseudoList : MutableList<String> = mutableListOf<String>()
        listPlayer.forEach { profilListe ->
            pseudoList.add(profilListe.login)
        }
        val adapter : ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, pseudoList)
        refPseudoInput!!.setAdapter(adapter)
    }


    override fun onDestroy() {
        activityScope.cancel()
        super.onDestroy()
    }


}
