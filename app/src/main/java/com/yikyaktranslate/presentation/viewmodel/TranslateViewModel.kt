package com.yikyaktranslate.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.*
import com.yikyaktranslate.R
import com.yikyaktranslate.model.FormatTranslationResponseEnum
import com.yikyaktranslate.model.Language
import com.yikyaktranslate.model.TranslationRequest
import com.yikyaktranslate.repositories.TranslationRepository
import com.yikyaktranslate.service.face.TranslationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TranslateViewModel(application: Application) : AndroidViewModel(application) {
    private val translationRepository:TranslationRepository = TranslationRepository()
    // Code for the source language that we are translating from; currently hardcoded to English
    private val sourceLanguageCode: String = application.getString(R.string.source_language_code)

    // List of Languages that we get from the back end
    private val languages: MutableStateFlow<List<Language>> by lazy {
        MutableStateFlow<List<Language>>(listOf())
    }

    // List of names of languages to display to user
    val languagesToDisplay = languages.map { it.map { language ->  language.name } }.asLiveData()

    // Index within languages/languagesToDisplay that the user has selected
    val targetLanguageIndex = mutableStateOf(0)

    // Text that the user has input to be translated
    private val _textToTranslate = MutableStateFlow(TextFieldValue(""))
    val textToTranslate = _textToTranslate.asLiveData()

    private val _textTranslated =  MutableStateFlow("")
    val textTranslated = _textTranslated.asLiveData()

    init {
        getLanguages()
    }


    /**
     * Loads the languages from our service
     */
    private suspend fun loadLanguages() {
        try {
            val languagesList = translationRepository.obtainLanguages()
            languages.value = languagesList
        }catch (e:Exception){
            Log.e("Error in loadLanguages", e.message.toString())
        }
    }

    private suspend fun translate(){
        try {
            val request:TranslationRequest = TranslationRequest(
                textToTranslate.value?.text?:"",
                sourceLanguageCode,
                languages.value[targetLanguageIndex.value].code,
                FormatTranslationResponseEnum.TEXT.value
            )
            val textTranslated = translationRepository.translateText(request)
            _textTranslated.value = textTranslated.translatedText
        }catch (e:Exception){
            Log.e("Error in translation", e.message.toString())
        }
    }



    /**
     * Updates the data when there's new text from the user
     *
     * @param newText TextFieldValue that contains user input we want to keep track of
     */
    fun onInputTextChange(newText: TextFieldValue) {
        _textToTranslate.value = newText
    }

    /**
     * Updates the selected target language when the user selects a new language
     *
     * @param newLanguageIndex Represents the index for the chosen language in the list of languages
     */
    fun onTargetLanguageChange(newLanguageIndex: Int) {
        targetLanguageIndex.value = newLanguageIndex
    }

    fun translateText(){
        viewModelScope.launch {
            try {
                translate()
            }catch (e:Exception){
                Log.e("Error in translation suspend fun call", e.message.toString())
            }
        }
    }

    fun getLanguages(){
        viewModelScope.launch {
            try {
                loadLanguages()
            }catch (e:Exception){
                Log.e("Error in get languages suspend fun call", e.message.toString())
            }
        }
    }

}