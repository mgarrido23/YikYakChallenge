package com.yikyaktranslate.repositories

import com.yikyaktranslate.model.Language
import com.yikyaktranslate.model.Translation
import com.yikyaktranslate.model.TranslationRequest
import com.yikyaktranslate.service.face.TranslationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TranslationRepository {
    suspend fun obtainLanguages():List<Language> = withContext(Dispatchers.IO) {
        return@withContext TranslationService.create().getLanguages()
    }
    suspend fun translateText(request: TranslationRequest):Translation = withContext(Dispatchers.IO) {
        return@withContext TranslationService.create().translate(
            request.textToTranslate,
            request.source,
            request.target,
            request.format
        )
    }


}