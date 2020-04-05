package com.translate.literally;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.translate.literally.GetTextPhraseActivity.EXTRA_TEXT;

public class TextSampleAdapter extends Adapter<TextSampleAdapter.ViewHolder> {
	private List<TextSample> textSamples = new ArrayList<>();
	private OnItemClickListener listener;
	private boolean getPhraseMode;
	private GetTextPhraseActivity getTextPhraseActivity;

	public TextSampleAdapter() {
	}

	public TextSampleAdapter(GetTextPhraseActivity getTextPhraseActivity, Boolean getPhraseMode) {
		this.getPhraseMode = getPhraseMode;
		this.getTextPhraseActivity = getTextPhraseActivity;
	}


	public interface OnItemClickListener {
		void onItemClick(TextSample textSample);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.listener = listener;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_sample_item, parent, false);
		return new ViewHolder(view, listener, textSamples);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		TextSample textSample = textSamples.get(position);
		holder.textView.setText(textSample.getDescriptionText());
		holder.textView.setTag(textSample.getDescriptionText());

		if (getPhraseMode) {
			holder.textView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent returnIntent = new Intent();
					returnIntent.putExtra(EXTRA_TEXT, textSample.getDescriptionText());
					getTextPhraseActivity.setResult(RESULT_OK, returnIntent);
					getTextPhraseActivity.finish();
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return textSamples.size();
	}

	void setTextSamples(List<TextSample> textSamples) {
		this.textSamples = textSamples;
		notifyDataSetChanged();
	}

	TextSample getNoteAt(int position) {
		return textSamples.get(position);
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		private TextView textView;
		private List<TextSample> textSamples = new ArrayList<>();

		ViewHolder(@NonNull View itemView, OnItemClickListener listener, List<TextSample> textSamples) {
			super(itemView);
			textView = itemView.findViewById(R.id.textItem);

			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					int position = getAdapterPosition();
					Log.i("Position", String.valueOf(position));
					if (listener != null && position == RecyclerView.NO_POSITION) {
						listener.onItemClick(textSamples.get(position));
					}
				}
			});
		}
	}


}
