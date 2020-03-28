package com.translate.literally;

import android.os.AsyncTask;
import android.util.Log;


import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;

public class TextTranslator {
	/**
	 * Made for Translate Activity
	 * Requires TextView to change text.
	 */
	private LanguageTranslator translator;


	public TextTranslator(LanguageTranslator translator) {
		this.translator = translator;
	}

	public LanguageTranslator getLanguageTranslator() {
		return translator;
	}







}
