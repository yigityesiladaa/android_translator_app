package com.translateapp.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import com.translateapp.R
import com.translateapp.common.extensions.showToast
import com.translateapp.common.utils.Utils
import com.translateapp.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var fromLanguages : Array<String>
    private lateinit var toLanguages : Array<String>
    private lateinit var fromSpinnerAdapter : ArrayAdapter<String>
    private lateinit var toSpinnerAdapter : ArrayAdapter<String>
    private var fromLanguageCode = 0
    private var toLanguageCode = 0

    companion object{
        private const val REQUEST_PERMISSION_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        init()
        registerEvents()
    }

    private fun init(){
        fromLanguages = resources.getStringArray(R.array.from_languages)
        toLanguages = resources.getStringArray(R.array.to_languages)

        //FromSpinner Adapter Process
        fromSpinnerAdapter = ArrayAdapter<String>(this, R.layout.spinner_item,fromLanguages)
        fromSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        //ToSpinner Adapter Process
        toSpinnerAdapter = ArrayAdapter<String>(this, R.layout.spinner_item,toLanguages)
        toSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    private fun registerEvents(){
        //FromSpinner
        binding.fromSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                fromLanguageCode = Utils.getLanguageCode(fromLanguages[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.fromSpinner.adapter = fromSpinnerAdapter


        //ToSpinner
        binding.toSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                toLanguageCode = Utils.getLanguageCode(toLanguages[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.toSpinner.adapter = toSpinnerAdapter

        binding.btnTranslate.setOnClickListener(btnTranslateClickListener)

        binding.btnMicImage.setOnClickListener(btnMicImageClickListener)

    }

    private val btnMicImageClickListener = View.OnClickListener { view->
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,resources.getString(R.string.speak_text))
        try {
            startActivityForResult(intent, REQUEST_PERMISSION_CODE)
        }catch (e : Exception){
            e.printStackTrace()
            showToast(e.localizedMessage)
        }
    }

    private val btnTranslateClickListener = View.OnClickListener {view->
        var sourceText = binding.etSource.text.toString().trim()
        binding.txtTranslatedText.text = ""
        if(sourceText.isEmpty()){
            showToast(resources.getString(R.string.enter_your_text_to_translate_text))
        }else if(fromLanguageCode == 0){
            showToast(resources.getString(R.string.select_source_language_text))
        }else if(toLanguageCode == 0){
            showToast(resources.getString(R.string.select_language_to_translation_text))
        }else{
            translateText(fromLanguageCode,toLanguageCode,sourceText)
        }
    }

    private fun translateText(fromLanguageCode : Int, toLanguageCode : Int, source : String){
        binding.txtTranslatedText.text = resources.getString(R.string.downloading_modal_text)
        val translatorOptions = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(fromLanguageCode)
            .setTargetLanguage(toLanguageCode)
            .build()

        val translator = FirebaseNaturalLanguage.getInstance().getTranslator(translatorOptions)

        val conditions = FirebaseModelDownloadConditions.Builder().build()

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener {
            binding.txtTranslatedText.text = resources.getString(R.string.translating_text)
            translator.translate(source).addOnSuccessListener {translatedText->
                binding.txtTranslatedText.text = translatedText
            }.addOnFailureListener { exception->
                showToast(exception.localizedMessage)
            }
        }.addOnFailureListener { exception->
            showToast(exception.localizedMessage)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_PERMISSION_CODE){
            if(resultCode == RESULT_OK && data != null){
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                result?.let {resultList->
                    binding.etSource.setText(resultList[0])
                }
            }
        }
    }

}