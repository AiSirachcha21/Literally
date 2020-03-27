package com.translate.literally;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class TranslateActivity extends AppCompatActivity {

	private Spinner sourceSpinner, targetSpinner;
	private EditText sourceEditText, translationResultEditText;
	private List<Language> languages;
	private LanguageViewModel languageViewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Window window = this.getWindow();
		// clear FLAG_TRANSLUCENT_STATUS flag:
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		// finally change the color
		window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_translate);

		sourceSpinner = findViewById(R.id.sourceLangSpinner);
		targetSpinner = findViewById(R.id.targetLangSpinner);
		sourceEditText = findViewById(R.id.sourceEditText);
		sourceEditText.setOnEditorActionListener(editorActionListener);
		translationResultEditText = findViewById(R.id.translationResultTextView);


		new getDataAsyncTask(TranslateActivity.this).execute();


	}

	private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView textView, int action, KeyEvent keyEvent) {
			if (action == EditorInfo.IME_ACTION_DONE) {
				TranslateTextTask task = new TranslateTextTask(TranslateActivity.this, TextTranslator.getLanguageTranslator());
				task.execute(sourceEditText.getText().toString());
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				try {
					if (imm != null) {
						imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
					}
				} catch (NullPointerException ne) {
					ne.printStackTrace();
				}
			}
			return true;
		}
	};

	public static class getDataAsyncTask extends AsyncTask<Void, Void, List<Language>> {
		private WeakReference<TranslateActivity> translateActivityWeakReference;

		getDataAsyncTask(TranslateActivity translateActivity) {
			this.translateActivityWeakReference = new WeakReference<>(translateActivity);
		}

		@Override
		protected List<Language> doInBackground(Void... voids) {
			TranslateActivity translateActivity = translateActivityWeakReference.get();
			translateActivity.languageViewModel = new ViewModelProvider(translateActivity).get(LanguageViewModel.class);
			translateActivity.languages = translateActivity.languageViewModel.getSubscribedLanguages();
			return translateActivity.languages;
		}

		@Override
		protected void onPostExecute(List<Language> languages) {
			TranslateActivity translateActivity = translateActivityWeakReference.get();
			List<String> languageNames = new ArrayList<>();

			for (Language lang : languages) {
				Log.i("LanguageName", lang.getLangDescription());
				languageNames.add(lang.getLangDescription());
			}
			ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(translateActivity, android.R.layout.simple_spinner_item, languageNames);
			spinnerAdapter.setNotifyOnChange(true);
			spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			translateActivity.sourceSpinner.setAdapter(spinnerAdapter);
			translateActivity.sourceSpinner.setSelection(languageNames.indexOf("English"));

			translateActivity.targetSpinner.setAdapter(spinnerAdapter);
		}
	}

	private static class TranslateTextTask extends AsyncTask<String, Void, String> {

		private WeakReference<TranslateActivity> translateActivityWeakReference;
		private LanguageTranslator translator;
		private String targetLangCode, sourceLangCode;

		TranslateTextTask(TranslateActivity translateActivity, LanguageTranslator translator) {
			this.translateActivityWeakReference = new WeakReference<>(translateActivity);
			this.translator = translator;
		}

		@Override
		protected String doInBackground(String... strings) {

			TranslateActivity translateActivity = translateActivityWeakReference.get();

			translateActivity.languages.forEach(language -> {
				if (language.getLangDescription().equalsIgnoreCase(translateActivity.sourceSpinner.getSelectedItem().toString())) {
					sourceLangCode = language.getLangCode();
				}

				if (language.getLangDescription().equalsIgnoreCase(translateActivity.targetSpinner.getSelectedItem().toString())) {
					targetLangCode = language.getLangCode();
				}
			});

			Log.i("Model", sourceLangCode.concat("-").concat(targetLangCode));

			TranslateOptions translateOptions = new TranslateOptions.Builder()
					.addText(strings[0])
					.modelId(sourceLangCode.concat("-").concat(targetLangCode))
					.build();
			TranslationResult result = translator.translate(translateOptions).execute().getResult();
			return result.getTranslations().get(0).getTranslation();
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			TranslateActivity translateActivity = translateActivityWeakReference.get();
			Log.i("TranslatedText", s);

			if (s.equalsIgnoreCase("")) {
				translateActivity.translationResultEditText.setText(R.string.TranslationError);

			} else {
				translateActivity.translationResultEditText.setText(s);
			}
		}
	}


}
