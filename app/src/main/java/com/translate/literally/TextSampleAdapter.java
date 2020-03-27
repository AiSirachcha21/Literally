package com.translate.literally;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.util.ArrayList;
import java.util.List;

public class TextSampleAdapter extends Adapter<TextSampleAdapter.ViewHolder> {
	private List<TextSample> textSamples = new ArrayList<>();

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.text_sample_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		TextSample textSample = textSamples.get(position);
		holder.textView.setText(textSample.getDescriptionText());
	}

	@Override
	public int getItemCount() {
		return textSamples.size();
	}

	void setTextSamples(List<TextSample> textSamples){
		this.textSamples = textSamples;
		notifyDataSetChanged();
	}

	TextSample getNoteAt(int position){
		return textSamples.get(position);
	}

	static class ViewHolder extends RecyclerView.ViewHolder{
		private TextView textView;

		ViewHolder(@NonNull View itemView) {
			super(itemView);
			textView = itemView.findViewById(R.id.textItem);
		}
	}

}
