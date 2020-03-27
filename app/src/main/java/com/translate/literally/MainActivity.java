package com.translate.literally;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

	com.google.android.material.card.MaterialCardView addCard,editCard, displayCard,
			translateCard,subscribeCard;
	Snackbar snackbar;

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


		setContentView(R.layout.activity_main);

		addCard = findViewById(R.id.addCard);
		editCard = findViewById(R.id.editCard);
		displayCard = findViewById(R.id.displayCard);
		subscribeCard = findViewById(R.id.languageSubscription);
		translateCard = findViewById(R.id.translateCard);

		addCard.setOnClickListener(this::startAppActivity);
		editCard.setOnClickListener(this::startAppActivity);
		displayCard.setOnClickListener(this::startAppActivity);
		subscribeCard.setOnClickListener(this::startAppActivity);
		translateCard.setOnClickListener(this::startAppActivity);

	}

	public void startAppActivity(View view) {
		String indicator = view.getTag().toString();

		switch (indicator.toLowerCase()){

			case "add":
			startActivity(new Intent(this, AddPhraseActivity.class));
			break;

			case "edit":
			startActivity(new Intent(this, EditPhraseActivity.class));
			break;

			case "display":
			startActivity(new Intent(this, DisplayPhraseActivity.class));
			break;

			case "subscriptions":
			startActivity(new Intent(this, LanguageSubscriptionActivity.class));
			break;

			case "translate":
			startActivity(new Intent(this, TranslateActivity.class));
			break;

			default:
		}
	}

//	@Override
//	public void onBackPressed() {
//		Intent intent = new Intent(this, SplashScreenActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		intent.putExtra("EXIT", true);
//		startActivity(intent);
//	}
}
