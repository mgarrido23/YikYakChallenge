package com.yikyaktranslate.model

data class TranslationRequest(
    var textToTranslate:String,
    var source:String,
    var target:String,
    var format:String
)
