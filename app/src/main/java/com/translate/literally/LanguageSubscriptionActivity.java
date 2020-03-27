package com.translate.literally;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class LanguageSubscriptionActivity extends AppCompatActivity{

	private List<Language> languages;
	private List<Language> checkedLanguages = new ArrayList<>();
	private LanguageViewModel languageViewModel;
	private RecyclerView languagesRecycleView;
	private MaterialButton subscribe;
	private CheckboxListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Window window = this.getWindow();
		// clear FLAG_TRANSLUCENT_STATUS flag:
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		// finally change the color
		window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_language_subscription);

		languagesRecycleView = findViewById(R.id.itemRecycleView);
		subscribe = findViewById(R.id.subscribe);
		languagesRecycleView.setLayoutManager(new LinearLayoutManager(this));

		adapter = new CheckboxListAdapter(this, subscribe);
		languagesRecycleView.setAdapter(adapter);
		new languageListAsyncTask(LanguageSubscriptionActivity.this).execute(languageViewModel);
	}

	static class languageListAsyncTask extends AsyncTask<LanguageViewModel, Void, LanguageViewModel>{
		private WeakReference<LanguageSubscriptionActivity> activityWeakReference;

		public languageListAsyncTask(LanguageSubscriptionActivity activity) {
			this.activityWeakReference = new WeakReference<>(activity);
		}

		@Override
		protected LanguageViewModel doInBackground(LanguageViewModel... languageViewModels) {
			LanguageSubscriptionActivity activity = activityWeakReference.get();
			languageViewModels[0] = new ViewModelProvider(activity).get(LanguageViewModel.class);
			return languageViewModels[0];
		}

		@Override
		protected void onPostExecute(LanguageViewModel languageViewModel) {
			LanguageSubscriptionActivity activity = activityWeakReference.get();
			languageViewModel.getLanguages().observe(activity, languageList -> {
				activity.languages = new ArrayList<>(languageList);
				activity.adapter.setLanguages(languageList);
			});
			activity.adapter.setLanguageViewModel(languageViewModel);
		}

	}
}
