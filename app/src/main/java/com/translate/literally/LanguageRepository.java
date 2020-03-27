package com.translate.literally;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class LanguageRepository {

	private LanguageDAO languageDAO;
	private LiveData<List<Language>> languages;
	private List<Language> subscribedLanguages;

	public LanguageRepository(Application application) {
		LiterallyDB literallyDB = LiterallyDB.getInstance(application);
		languageDAO = literallyDB.languageDAO();
		languages = languageDAO.getAllLanguages();
		subscribedLanguages = languageDAO.getSubscribedLanguages();
	}

	public void add(Language language){
		new addTask(languageDAO).execute(language);
	}

	public void update(Language language){
		new updateTask(languageDAO).execute(language);
	}

	public LiveData<List<Language>> getLanguages() {
		return languages;
	}

	public List<Language> getSubscribedLanguages() {
		return subscribedLanguages;
	}

	public static class addTask extends AsyncTask<Language, Void, Void>{

		private LanguageDAO languageDAO;

		public addTask(LanguageDAO languageDAO) {
			this.languageDAO = languageDAO;
		}

		@Override
		protected Void doInBackground(Language... languages) {
			languageDAO.add(languages[0]);
			return null;
		}
	}

	public static class updateTask extends AsyncTask<Language, Void, Void>{
		private LanguageDAO languageDAO;
		public updateTask(LanguageDAO languageDAO)
		{
			this.languageDAO = languageDAO;
		}
		@Override
		protected Void doInBackground(Language... languages) {
			languageDAO.updateState(languages[0].getLangCode(), languages[0].getState());
			return null;
		}
	}
}
