package com.translate.literally;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.ibm.watson.language_translator.v3.LanguageTranslator;

import java.util.ArrayList;
import java.util.List;

@Database(entities = {TextSample.class, Language.class}, version=3)
public abstract class LiterallyDB extends RoomDatabase {

	private static final String API_KEY = "XkeTb82JTQMnSa2OinGVbBhCVWX5YfhnZUXkP6Blue4s";
	private static final String URL = "https://api.us-south.language-translator.watson.cloud.ibm.com/instances/656fb497-5319-486c-9270-68c6fb32ab8d";
	private static final String VER = "2018-05-01";
	private static LiterallyDB instance;

	public abstract TextSampleDAO textSampleDAO();
	public abstract LanguageDAO languageDAO();

	//Creates singleton instance of DB Object
	//Synchronized to avoid multiple instances of DB being created
	// Will add default text (Hello) to DB on first create
	static synchronized LiterallyDB getInstance(Context context) {
		if (instance == null) {
			instance = Room.databaseBuilder(context.getApplicationContext(),
					LiterallyDB.class, "literally_database")
					.fallbackToDestructiveMigration()
					.addCallback(roomCallback)
					.build();
		}
		return instance;
	}

	private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
		@Override
		public void onCreate(@NonNull SupportSQLiteDatabase db) {
			super.onCreate(db);
			new PopulateDBTask(instance).execute();
		}
	};

	public static class PopulateDBTask extends AsyncTask<Void, Void, Void> {
		private final TextSampleDAO textSampleDAO;
		private final LanguageDAO languageDAO;

		PopulateDBTask(LiterallyDB literallyDB) {
			this.textSampleDAO = literallyDB.textSampleDAO();
			this.languageDAO = literallyDB.languageDAO();
		}

		@Override
		public Void doInBackground(Void... voids) {
			TranslatorService translatorService = new TranslatorService();
			LanguageTranslator languageTranslator = translatorService.initService(API_KEY, URL, VER);
			Log.i("api_cred", API_KEY + " " + URL + " " + VER);

			List<Language> languages = new ArrayList<>();

			languageTranslator.listIdentifiableLanguages().execute()
					.getResult()
					.getLanguages()
					.forEach(item -> {
						languages.add(new Language(item.getLanguage(), item.getName(), 0));
					});

			languages.forEach(item -> Log.i("LanguageName", item.getLangCode() + " " + item.getLangDescription()));

			for (Language language : languages) {
				this.languageDAO.add(language);
			}

			//Needs to be run on separate thread or database will get locked
			Runnable runnable = () -> {
				Language language = languageDAO.getLanguage("en");
				language.setState(1);
				languageDAO.updateState(language.getLangCode(), language.getState());
			};

			runnable.run();
			return null;
		}
	}

}
