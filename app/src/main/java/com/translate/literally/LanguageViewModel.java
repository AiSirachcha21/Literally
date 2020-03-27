package com.translate.literally;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class LanguageViewModel extends AndroidViewModel {
	private LanguageRepository repository;
	private LiveData<List<Language>> languages;
	private List<Language> subscribedLanguages;

	public LanguageViewModel(@NonNull Application application) {
		super(application);
		repository = new LanguageRepository(application);
		languages = repository.getLanguages();
		subscribedLanguages = repository.getSubscribedLanguages();
	}

//	public LanguageViewModel(@NonNull Application application) {
//		super(application);
//		repository = new LanguageRepository(application);
//		languages = repository.getLanguages();
//	}

	public void updateLanguage(Language language){repository.update(language);}

	public LiveData<List<Language>> getLanguages() {
		return languages;
	}

	public List<Language> getSubscribedLanguages() {
		return subscribedLanguages;
	}
}
