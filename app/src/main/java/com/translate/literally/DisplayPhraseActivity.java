package com.translate.literally;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.snackbar.Snackbar;

public class DisplayPhraseActivity extends AppCompatActivity {

	TextSampleViewModel textSampleViewModel;
	private RecyclerView recyclerView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
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

		final TextSampleAdapter adapter = new TextSampleAdapter();
		recyclerView.setAdapter(adapter);

		//noinspection deprecation
		textSampleViewModel = ViewModelProviders.of(this).get(TextSampleViewModel.class);
		textSampleViewModel.getTextSamples().observe(this, adapter::setTextSamples);



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
