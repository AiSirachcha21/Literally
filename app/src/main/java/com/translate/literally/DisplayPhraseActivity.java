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
}
