package com.translate.literally;

import android.util.Log;

import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.IdentifiableLanguage;

import java.util.List;

class TranslatorService {

	LanguageTranslator initService(String API_KEY, String URL, String VERSION_DATE){
		Authenticator authenticator = new IamAuthenticator(API_KEY);
		LanguageTranslator service = new LanguageTranslator(VERSION_DATE, authenticator);
		service.setServiceUrl(URL);

		Log.i("API_CRED", API_KEY + " : "+URL+" : "+VERSION_DATE );

		return service;
	}

}
