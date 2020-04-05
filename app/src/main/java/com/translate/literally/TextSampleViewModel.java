package com.translate.literally;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TextSampleViewModel extends AndroidViewModel {
	private TextSampleRepository repository;
	private LiveData<List<TextSample>> textSamples;


	public TextSampleViewModel(@NonNull Application application) {
		super(application);
		repository = new TextSampleRepository(application);
		textSamples = repository.getTextSamples();
	}

	public void addText(TextSample textSample){
		repository.add(textSample);
	}

	public void updateText(String oldText, String newText){
		repository.update(oldText, newText);
	}

	public void deleteText(TextSample textSample){
		repository.delete(textSample);
	}

	public LiveData<List<TextSample>> getTextSamples() {
		return textSamples;
	}



}
