package com.translateapp.common.utils

import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage

class Utils {
    companion object {
        fun getLanguageCode(language: String): Int {
            return when (language) {
                "Turkish" -> {
                    FirebaseTranslateLanguage.TR
                }
                "English" -> {
                    FirebaseTranslateLanguage.EN
                }
                "Russian" -> {
                    FirebaseTranslateLanguage.RU
                }
                "Spanish" -> {
                    FirebaseTranslateLanguage.ES
                }
                "French" -> {
                    FirebaseTranslateLanguage.FR
                }
                "Italian" -> {
                    FirebaseTranslateLanguage.IT
                }
                "Japanese" -> {
                    FirebaseTranslateLanguage.JA
                }
                "Korean" -> {
                    FirebaseTranslateLanguage.KO
                }
                "Afrikaans" -> {
                    FirebaseTranslateLanguage.AF
                }
                "Belarusian" -> {
                    FirebaseTranslateLanguage.BE
                }
                else -> {
                    0
                }
            }
        }
    }
}