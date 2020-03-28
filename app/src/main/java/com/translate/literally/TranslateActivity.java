package com.translate.literally;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class TranslateActivity extends AppCompatActivity {
	private static final String API_KEY = "XkeTb82JTQMnSa2OinGVbBhCVWX5YfhnZUXkP6Blue4s";
	private static final String URL = "https://api.us-south.language-translator.watson.cloud.ibm.com/instances/656fb497-5319-486c-9270-68c6fb32ab8d";
	private static final String VER = "2018-05-01";
	public static final String EXTRA_TEXT = "com.translate.literally.EXTRA_TEXT";
	private static final int EXTRA_SUBTITLE_REQUEST = 1;
	private ProgressBar progressBar;
	private ImageView placeholderIcon;
	private Spinner sourceSpinner, targetSpinner;
	private EditText sourceEditText, translationResultEditText;
	private MaterialButton savedPhraseBtn;
	private List<Language> languages;
	private LanguageViewModel languageViewModel;
	private TextTranslator textTranslator = new TextTranslator(new TranslatorService().initService(API_KEY,URL,VER ));
	RotateAnimation rotateAnimation = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);


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
		progressBar = findViewById(R.id.progress_horizontal);
		placeholderIcon = findViewById(R.id.placeholderIcon);
		savedPhraseBtn= findViewById(R.id.savedPhraseBtn);

		rotateAnimation.setDuration(1500);
		rotateAnimation.setRepeatCount(2);

		Snackbar.make(placeholderIcon,
				"You can click on the translate icon in order to translate",
				5000)
				.setBackgroundTint(getColor(R.color.colorSecondary))
				.setTextColor(getColor(R.color.colorPrimary))
				.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
				.setAction("Ok", View::cancelPendingInputEvents)
				.setActionTextColor(getColor(R.color.colorPrimary))
				.show();


		new getDataAsyncTask(TranslateActivity.this).execute();
		savedPhraseBtn.setOnClickListener(view -> {
			Intent intent = new Intent(this, GetTextPhraseActivity.class);
			intent.putExtra(EXTRA_TEXT, "Select a Phrase");
			startActivityForResult(intent, EXTRA_SUBTITLE_REQUEST);
		});

		placeholderIcon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				new TranslateTextTask(TranslateActivity.this, textTranslator.getLanguageTranslator())
						.execute(sourceEditText.getText().toString());
				placeholderIcon.startAnimation(rotateAnimation);
			}
		});
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == EXTRA_SUBTITLE_REQUEST && resultCode == RESULT_OK && !data.getStringExtra(EXTRA_TEXT).equalsIgnoreCase("")) {
			String text = data.getStringExtra(EXTRA_TEXT);
			sourceEditText.setText(text);
			placeholderIcon.performClick();
		}else{
			Snackbar.make(sourceEditText, "Trouble retrieving text. Please try again", Snackbar.LENGTH_SHORT);
		}

	}

	private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {


		@Override
		public boolean onEditorAction(TextView textView, int action, KeyEvent keyEvent) {
			if (action == EditorInfo.IME_ACTION_DONE) {
//				TranslateTextTask task = new TranslateTextTask(TranslateActivity.this, textTranslator.getLanguageTranslator());
//				task.execute(sourceEditText.getText().toString());
				placeholderIcon.performClick();
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
		private CountDownTimer countDownTimer;

		TranslateTextTask(TranslateActivity translateActivity, LanguageTranslator translator) {
			this.translateActivityWeakReference = new WeakReference<>(translateActivity);
			this.translator = translator;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			TranslateActivity translateActivity = translateActivityWeakReference.get();
			translateActivity.translationResultEditText.setVisibility(View.INVISIBLE);
			translateActivity.progressBar.setVisibility(View.VISIBLE);
			countDownTimer = new CountDownTimer(5000,1000) {
				@Override
				public void onTick(long millisTillEnd) {
					if (millisTillEnd > 0 && (5000-millisTillEnd)/100 != 0){
						translateActivity.progressBar.setProgress((int)(8000-millisTillEnd)/100, true);
					}
				}

				@Override
				public void onFinish() {
					countDownTimer = null;
				}
			};

			countDownTimer.start();
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
			TranslationResult result;
			try{
				result = translator.translate(translateOptions).execute().getResult();
			}catch (com.ibm.cloud.sdk.core.service.exception.NotFoundException nfe){
				return "";
			}
			return result.getTranslations().get(0).getTranslation();
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			TranslateActivity translateActivity = translateActivityWeakReference.get();

			if (countDownTimer != null){
				translateActivity.progressBar.setProgress(100);
				countDownTimer.cancel();
			}

			translateActivity.translationResultEditText.setVisibility(View.VISIBLE);
			translateActivity.progressBar.setVisibility(View.INVISIBLE);
			Log.i("TranslatedText", s);

			if (s.equalsIgnoreCase("")) {
				translateActivity.translationResultEditText.setText(R.string.TranslationError);
			} else {
				translateActivity.translationResultEditText.setText(s);
			}
		}
	}


}
