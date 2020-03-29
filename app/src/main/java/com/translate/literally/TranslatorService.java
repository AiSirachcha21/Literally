package com.translate.literally;

import android.util.Log;

import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.IdentifiedLanguages;
import com.ibm.watson.language_translator.v3.model.IdentifyOptions;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.text_to_speech.v1.model.GetVoiceOptions;
import com.ibm.watson.text_to_speech.v1.model.Voice;

class TranslatorService {
	LanguageTranslator initService(String API_KEY, String URL, String VERSION_DATE){
		Authenticator authenticator = new IamAuthenticator(API_KEY);
		LanguageTranslator service = new LanguageTranslator(VERSION_DATE, authenticator);
		service.setServiceUrl(URL);

		Log.i("API_CRED", API_KEY + " : "+URL+" : "+VERSION_DATE );
		return service;
	}

	TextToSpeech initService(String API_KEY, String URL){
		IamAuthenticator authenticator = new IamAuthenticator(API_KEY);
		TextToSpeech textToSpeech = new TextToSpeech(authenticator);
		textToSpeech.setServiceUrl(URL);

		Log.i("API_CRED", "TTS/"+API_KEY + " : "+URL);
		return textToSpeech;
	}

	static Voice identifyLang(TextToSpeech textToSpeech, LanguageTranslator translator, String text){
		IdentifyOptions identifyOptions = new IdentifyOptions.Builder()
				.text(text)
				.build();
		IdentifiedLanguages languages = translator.identify(identifyOptions)
				.execute().getResult();

		GetVoiceOptions getVoiceOptions = new GetVoiceOptions.Builder()
				.voice(getVoiceModel(languages.getLanguages().get(0).getLanguage()))
				.build();

		Voice voice = textToSpeech.getVoice(getVoiceOptions).execute().getResult();

		return voice;
	}


	private static String getVoiceModel(String langCode){
		String voiceOption;

		switch (langCode){
			case "en":
			default:
				voiceOption = GetVoiceOptions.Voice.EN_GB_KATEV3VOICE;
				break;

			case "de":
				voiceOption =  GetVoiceOptions.Voice.DE_DE_DIETERV3VOICE;
				break;

			case "es":
				voiceOption =  GetVoiceOptions.Voice.ES_LA_SOFIAV3VOICE;
				break;

			case "fr":
				voiceOption =  GetVoiceOptions.Voice.FR_FR_RENEEV3VOICE;
				break;

			case "it":
				voiceOption =  GetVoiceOptions.Voice.IT_IT_FRANCESCAV3VOICE;
				break;

			case "ja":
				voiceOption =  GetVoiceOptions.Voice.JA_JP_EMIV3VOICE;
				break;

			case "pt":
				voiceOption =  GetVoiceOptions.Voice.PT_BR_ISABELAV3VOICE;
				break;
		}

		return voiceOption;
	}

}
