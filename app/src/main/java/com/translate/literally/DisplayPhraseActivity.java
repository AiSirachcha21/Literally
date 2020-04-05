package com.translate.literally;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class DisplayPhraseActivity extends AppCompatActivity {

	TextSampleViewModel textSampleViewModel;
	private RecyclerView recyclerView;
	private Spinner languageSpinner;
	private List<Language> languages;
	private List<TextSample> textSamples;
	private List<TextSample> originalTextSamples;
	private LanguageViewModel languageViewModel;
	private TextSampleAdapter adapter;
	private boolean isDefaultListSet;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		isDefaultListSet = false;
		Window window = this.getWindow();
		// clear FLAG_TRANSLUCENT_STATUS flag:
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		// finally change the color
		window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_phrase);

		languageSpinner = findViewById(R.id.languageSpinner);
		new getDataAsyncTask(DisplayPhraseActivity.this).execute();

		recyclerView = findViewById(R.id.dbItemsList);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));



		adapter = new TextSampleAdapter();
		recyclerView.setAdapter(adapter);

		//noinspection deprecation
		textSampleViewModel = ViewModelProviders.of(this).get(TextSampleViewModel.class);
		textSampleViewModel.getTextSamples().observe(this, textSamples1 -> {
			textSamples = new ArrayList<>(textSamples1);
			adapter.setTextSamples(textSamples);
		});



		//Required for change in spinner language
		languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				if (languageSpinner.getSelectedItem().toString().equalsIgnoreCase("English")){
					adapter.setTextSamples(textSamples);
					onRestart();
				}else {
					adapter.setTextSamples(textSamples);
					new TranslateTextTask(
							DisplayPhraseActivity.this,
							new TranslatorService().initService(
									getString(R.string.TRANSLATOR_API_KEY),
									getString(R.string.TRANSLATOR_URL),
									getString(R.string.TRANSLATOR_VERSION_DATE)
							),
							textSamples,
							languages).execute();
				}


			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {}
		});

		new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
			@Override
			public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
				return false;
			}

			@Override
			public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
				textSampleViewModel.deleteText(adapter.getNoteAt(viewHolder.getAdapterPosition()));
				Snackbar.make(
						recyclerView,
						R.string.snackbarMsg_successfulDeletePhrase,
						Snackbar.LENGTH_LONG
				).show();
			}
		}).attachToRecyclerView(recyclerView);

	}


	/**
	 * This Async Task is used to populate the spinners in the UI
	 */
	public static class getDataAsyncTask extends AsyncTask<Void, Void, List<Language>> {
		private WeakReference<DisplayPhraseActivity> displayPhraseActivityWeakReference;

		getDataAsyncTask(DisplayPhraseActivity displayPhraseActivity) {
			this.displayPhraseActivityWeakReference = new WeakReference<>(displayPhraseActivity);
		}

		@SuppressLint("WrongThread")
		@Override
		protected List<Language> doInBackground(Void... voids) {
			DisplayPhraseActivity displayPhraseActivity = displayPhraseActivityWeakReference.get();
			displayPhraseActivity.languageViewModel = new ViewModelProvider(displayPhraseActivity).get(LanguageViewModel.class);
			displayPhraseActivity.languages = displayPhraseActivity.languageViewModel.getSubscribedLanguages();
			return displayPhraseActivity.languages;
		}

		@Override
		protected void onPostExecute(List<Language> languages) {
			DisplayPhraseActivity displayPhraseActivity = displayPhraseActivityWeakReference.get();
			List<String> languageNames = new ArrayList<>();

			for (Language lang : languages) {
				Log.i("LanguageName", "Spinner/"+lang.getLangDescription());
				languageNames.add(lang.getLangDescription());
			}
			ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(displayPhraseActivity, android.R.layout.simple_spinner_item, languageNames);
			spinnerAdapter.setNotifyOnChange(true);
			spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			displayPhraseActivity.languageSpinner.setAdapter(spinnerAdapter);
			displayPhraseActivity.languageSpinner.setSelection(languageNames.indexOf("English"));
		}
	}

	/**
	 * Async Task used to translate text
	 */
	private static class TranslateTextTask extends AsyncTask<Void, Void, List<TextSample>> {

		private WeakReference<DisplayPhraseActivity> displayPhraseActivityWeakReference;
		private LanguageTranslator translator;
		private String sourceLangCode;
		private List<TextSample> textSamples;
		private List<TextSample> translatedStrings = new ArrayList<>();


		TranslateTextTask(DisplayPhraseActivity displayPhraseActivity,
						  LanguageTranslator translator,
						  List<TextSample> textSamples,
						  List<Language> languages) {
			this.displayPhraseActivityWeakReference = new WeakReference<>(displayPhraseActivity);
			this.translator = translator;
			this.textSamples = new ArrayList<>(textSamples);
		}

		@Override
		protected List<TextSample> doInBackground(Void... voids) {

			DisplayPhraseActivity displayPhraseActivity = displayPhraseActivityWeakReference.get();

			displayPhraseActivity.languages.forEach(language -> {
				if (language.getLangDescription().equalsIgnoreCase(displayPhraseActivity.languageSpinner.getSelectedItem().toString())) {
					sourceLangCode = language.getLangCode();
				}
			});


			if (sourceLangCode.equalsIgnoreCase("en")){
				return displayPhraseActivity.textSampleViewModel.getTextSamples().getValue();
			}

			TranslateOptions.Builder translateOptionsBuilder = new TranslateOptions.Builder()
					.modelId("en".concat("-").concat(sourceLangCode));

			textSamples.forEach(textSample -> {
				translateOptionsBuilder.addText(textSample.getDescriptionText());
				TranslateOptions translateOptions = translateOptionsBuilder.build();
				try{
					textSample.setDescriptionText(translator
							.translate(translateOptions)
							.execute()
							.getResult()
							.getTranslations()
							.get(0)
							.getTranslation());
					translatedStrings.add(textSample);

				}catch (com.ibm.cloud.sdk.core.service.exception.NotFoundException nfe){
					cancel(true);
				}
			});



			return translatedStrings;
		}

		@Override
		protected void onPostExecute(List<TextSample> s) {
			super.onPostExecute(s);
			DisplayPhraseActivity displayPhraseActivity = displayPhraseActivityWeakReference.get();

			displayPhraseActivity.adapter.setTextSamples(s);
			s.forEach(textSample->Log.i("TranslatedText",textSample.getDescriptionText() ));


		}



	}
}
