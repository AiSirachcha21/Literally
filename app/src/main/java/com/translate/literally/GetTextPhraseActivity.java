package com.translate.literally;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GetTextPhraseActivity extends AppCompatActivity {
	TextSampleViewModel textSampleViewModel;
	private TextView subheading, textItem;
	private RecyclerView recyclerView;
	public static final String EXTRA_TEXT = "com.translate.literally.EXTRA_TEXT";

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
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

		String subheadingTxt;
		Intent intent = getIntent();

		subheading = findViewById(R.id.subheading);
		if (intent.hasExtra(TranslateActivity.EXTRA_TEXT)){
			subheadingTxt = intent.getStringExtra(TranslateActivity.EXTRA_TEXT);
			subheading.setText(subheadingTxt);
		}

		recyclerView = findViewById(R.id.dbItemsList);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		TextSampleAdapter adapter = new TextSampleAdapter(GetTextPhraseActivity.this,true);
		recyclerView.setAdapter(adapter);

		adapter.setOnItemClickListener(new TextSampleAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(TextSample textSample) {
				Log.i("TranslatedText", "PRE/"+textSample.getDescriptionText());

				Intent returnIntent = new Intent();
				if (EXTRA_TEXT != null){
					returnIntent.putExtra(EXTRA_TEXT, textSample.getDescriptionText());
					setResult(RESULT_OK, returnIntent);
				}else{
					returnIntent.putExtra(EXTRA_TEXT, "");
					setResult(RESULT_OK, returnIntent);
				}
				finish();
			}
		});


		//noinspection deprecation
		textSampleViewModel = ViewModelProviders.of(this).get(TextSampleViewModel.class);
		textSampleViewModel.getTextSamples().observe(this, adapter::setTextSamples);


	}
}
