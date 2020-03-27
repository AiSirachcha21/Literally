package com.translate.literally;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;

class CheckboxListAdapter extends RecyclerView.Adapter<CheckboxListAdapter.ViewHolder> {
	private List<Language> languages = new ArrayList<>();
	private List<TempCheckedItem> checkedLanguages = new ArrayList<>();
	private LanguageViewModel languageViewModel;
	private MaterialButton subscribeButton;
	private Context context;

	CheckboxListAdapter(Context context, MaterialButton subscribeButton) {
		this.context = context;
		this.subscribeButton = subscribeButton;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_sample_checkbox_item, parent, false);
		return new ViewHolder(view,languageViewModel, languages,checkedLanguages,subscribeButton);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		holder.langCodeTextView.setText(languages.get(position).getLangCode());
		holder.langDescriptionTextView.setText(languages.get(position).getLangDescription());

		int state;

		if (languages.get(position).getState() == 0){
			holder.materialCheckBox.setChecked(false);
			state = 0;
		}else{
			holder.materialCheckBox.setChecked(true);
			state = 1;
		}

		holder.materialCheckBox.setTag(new TempCheckedItem(languages.get(position).getLangCode(), state));

		if (getCheckedLanguages().size() > 0){
			subscribeButton.setVisibility(View.VISIBLE);
		}else{
			subscribeButton.setVisibility(View.INVISIBLE);
		}

	}

	public void setLanguageViewModel(LanguageViewModel languageViewModel){
		this.languageViewModel = languageViewModel;
	}

	public void setLanguages(List<Language> languages){
		this.languages = languages;
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return languages.size();
	}

	public List<TempCheckedItem> getCheckedLanguages() {
		return checkedLanguages;
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		private TextView langCodeTextView;
		private TextView langDescriptionTextView;
		private MaterialCheckBox materialCheckBox;
		private MaterialButton subscribeBtn;
		private LanguageViewModel languageViewModel;

		public ViewHolder(@NonNull View itemView, LanguageViewModel languageViewModel,
						  List<Language> languages,List<TempCheckedItem> checkedLanguages, MaterialButton subscribeBtn) {
			super(itemView);
			langCodeTextView = itemView.findViewById(R.id.languageCode);
			langDescriptionTextView = itemView.findViewById(R.id.language);
			materialCheckBox = itemView.findViewById(R.id.subscriptionCheckbox);
			this.subscribeBtn = subscribeBtn;
			this.languageViewModel = languageViewModel;

			materialCheckBox.setOnClickListener(view -> {
				TempCheckedItem item = (TempCheckedItem)view.getTag();

				// If state is 0 make 1 else keep 0;
				int newState = (item.getState() == 0) ? 1 : 0;
				checkedLanguages.add(new TempCheckedItem(item.langCode, newState));
				setIsRecyclable(!isRecyclable());
				this.subscribeBtn.setVisibility(View.VISIBLE);
			});

			subscribeBtn.setOnClickListener(view -> {
				view.setVisibility(View.INVISIBLE);
				//Checks each checked Language
				for (TempCheckedItem item : checkedLanguages) {
					//Loops inside language list to find the Language Object corresponding to code
					for (int i = 0; i < languages.size(); i++) {

						//If found get the language Object, change state and update
						if (languages.get(i).getLangCode().equalsIgnoreCase(item.langCode)){
							Language updatedLanguage = languages.get(i);
							updatedLanguage.setState(item.state);
							languageViewModel.updateLanguage(updatedLanguage);
						}
					}
				}
			});
		}
	}

	static class TempCheckedItem{
		String langCode;
		int state;

		public TempCheckedItem(String langCode, int state) {
			this.langCode = langCode;
			this.state = state;
		}

		public String getLangCode() {
			return langCode;
		}

		public int getState() {
			return state;
		}

		public void setLangCode(String langCode) {
			this.langCode = langCode;
		}

		public void setState(int state) {
			this.state = state;
		}
	}
}
