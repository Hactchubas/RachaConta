package com.example.rachaconta

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.DecimalFormat
import java.util.Locale


class MainActivity : AppCompatActivity() , TextToSpeech.OnInitListener{
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        resultSetup()
        shareButtonSetup()

        tts = TextToSpeech(this, this)

    }

    fun resultSetup(){
        val billValue : TextView = findViewById(R.id.billValue)
        val nPeople : TextView = findViewById(R.id.nPeople)
        val result : TextView = findViewById(R.id.result)


        nPeople.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (s.isNotEmpty() && billValue.text.isNotEmpty() && !nPeople.text.equals("0") ) {
                    val billValueDouble = billValue.text.toString().toDouble()
                    val nPeopleNumber = nPeople.text.toString().toDouble()
                    result.text = calcAndFormat(billValueDouble,nPeopleNumber)
                } else{
                    result.text = ""
                }
            }
        })
        billValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (s.isNotEmpty() && nPeople.text.isNotEmpty() && !nPeople.text.equals("0") ) {
                    val billValueDouble = billValue.text.toString().toDouble()
                    val nPeopleNumber = nPeople.text.toString().toDouble()
                    result.text = calcAndFormat(billValueDouble,nPeopleNumber)

                } else{
                    result.text = ""
                }
            }
        })


    }

    fun shareButtonSetup(){
        val shareButton : ImageButton = findViewById(R.id.shareButton)


        shareButton.setOnClickListener{
            val result : TextView = findViewById(R.id.result)


            if(result.text.toString().isNotEmpty()) {

                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, createResultMessage())
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)

                startActivity(shareIntent)
            }
        }
    }

    fun createResultMessage() : String{
        val billValue : TextView = findViewById(R.id.billValue)
        val nPeople : TextView = findViewById(R.id.nPeople)
        val billValueText = billValue.text.toString()
        val nPeopleText = nPeople.text.toString()
        val resultFormatted = calcAndFormat(billValueText.toDouble(),nPeopleText.toDouble())
        return "Olá galera, a conta deu $billValueText reais que dividindo para $nPeopleText pessoas fica $resultFormatted"

    }

    fun calcAndFormat(bill : Double, people : Double) : String{
        val resultValue = bill/people
        val dFormat = DecimalFormat("##.##")
        return "R$${dFormat.format(resultValue)} pra cada"
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.getDefault()
        } else {
            Log.e("My Log", "Failed to initialize TTS engine.")
        }
    }

    fun clickSpeak(v: View){

        tts.speak(createResultMessage(), TextToSpeech.QUEUE_FLUSH, null, null)

    }

}




