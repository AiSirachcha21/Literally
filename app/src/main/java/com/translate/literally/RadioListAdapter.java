package com.translate.literally;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class RadioListAdapter extends RecyclerView.Adapter<RadioListAdapter.ViewHolder> {
	private int lastSelectedPosition = -1;
	private List<TextSample> textSamples = new ArrayList<>();
	private TextSampleViewModel textSampleViewModel;


	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_sample_radio_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		TextSample textSample = textSamples.get(position);
		holder.listItemText.setText(textSample.getDescriptionText());

		//since only one radio button is allowed to be selected,
		// this condition un-checks previous selections
		holder.listItemRadioButton.setChecked(lastSelectedPosition == position);
	}

	@Override
	public int getItemCount() {
		return textSamples.size();
	}

	public void setTextSampleViewModel(TextSampleViewModel textSampleViewModel){
		this.textSampleViewModel = textSampleViewModel;
	}

	public void setTextSamples(List<TextSample> textSamples){
		this.textSamples = textSamples;
		notifyDataSetChanged();
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		TextView listItemText;
		MaterialRadioButton listItemRadioButton;

		ViewHolder(@NonNull View itemView) {
			super(itemView);
			listItemText = itemView.findViewById(R.id.listItemText);
			listItemRadioButton = itemView.findViewById(R.id.listItemRadioButton);

			listItemText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					lastSelectedPosition = getAdapterPosition();
					notifyDataSetChanged();

					AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext());
					alertDialog.setTitle("Edit Text");
					alertDialog.setMessage("Please make changes to your text");

					final EditText input = new EditText(view.getContext());
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT);
					lp.setMargins(10, 5, 10, 5);
					input.setLayoutParams(lp);
					alertDialog.setView(input);

					String oldText = listItemText.getText().toString();
					input.setText(oldText);

					alertDialog.setPositiveButton("YES", (dialog, which) -> {
						textSampleViewModel.updateText(oldText, input.getText().toString());
						Snackbar.make(
								listItemText,
								R.string.snackbarMsg_SuccesfulChange,
								Snackbar.LENGTH_LONG
						).show();
						input.getText().clear();
					});

					alertDialog.setNegativeButton("NO", (dialog, which) -> {
						Snackbar.make(
								listItemText,
								R.string.snackbarMsg_CancelledChange,
								Snackbar.LENGTH_LONG
						).show();
						input.getText().clear();
					});

					alertDialog.show();
				}
			});

		}
	}

}
