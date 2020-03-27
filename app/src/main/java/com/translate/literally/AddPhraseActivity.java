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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class AddPhraseActivity extends AppCompatActivity {

	private TextSampleViewModel textSampleViewModel;
	private TextInputEditText textBox;
	private MaterialButton addBtn;


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
		setContentView(R.layout.activity_add_phrase);

		RecyclerView recyclerView = findViewById(R.id.dbItemsList);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		final TextSampleAdapter adapter = new TextSampleAdapter();
		recyclerView.setAdapter(adapter);

		//noinspection deprecation
		textSampleViewModel = ViewModelProviders.of(this).get(TextSampleViewModel.class);
		textSampleViewModel.getTextSamples().observe(this, adapter::setTextSamples);


		textBox = findViewById(R.id.addPhraseText);
		addBtn = findViewById(R.id.addPhraseButton);

		new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
			@Override
			public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
				return false;
			}

			@Override
			public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
				textSampleViewModel.deleteText(adapter.getNoteAt(viewHolder.getAdapterPosition()));
				Snackbar.make(
						addBtn,
						R.string.snackbarMsg_successfulDeletePhrase,
						Snackbar.LENGTH_LONG
				).show();
			}
		}).attachToRecyclerView(recyclerView);

		addBtn.setOnClickListener(view -> {
			saveTextSample(textBox);
		});

	}

	private void saveTextSample(TextInputEditText textBox){
		String text;
		try {
			text = Objects.requireNonNull(textBox.getText()).toString();
			if (text.trim().isEmpty()){
				Snackbar.make(
						addBtn,
						R.string.snackbarMsg_cannotProceedAddPhrase,
						Snackbar.LENGTH_LONG
				).show();
			}else{
				textSampleViewModel.addText(new TextSample(text));
				Snackbar.make(
						addBtn,
						R.string.snackbarMsg_successfulAddPhrase,
						Snackbar.LENGTH_LONG
				).show();
				textBox.setText("");
			}

		}catch (NullPointerException ne){
			ne.printStackTrace();
		}
	}


	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}
}
