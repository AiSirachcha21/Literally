package com.translate.literally;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TextSampleRepository {

	private TextSampleDAO textSampleDAO;
	private LiveData<List<TextSample>> textSamples;

	public TextSampleRepository(Application application) {
		LiterallyDB literallyDB = LiterallyDB.getInstance(application);
		textSampleDAO = literallyDB.textSampleDAO();
		textSamples = textSampleDAO.getAllSamples();
	}

	public void add(TextSample textSample){
		new addTextSampleTask(textSampleDAO).execute(textSample);
	}

	public void update(String oldText, String newText){
		new updateTextSampleTask(textSampleDAO).execute(oldText, newText);
	}

	public void delete(TextSample textSample){
		new deleteTextSampleTask(textSampleDAO).execute(textSample);
	}

	public void deleteAll(){
		new deleteAllTextSampleTask(textSampleDAO).execute();

	}

	public LiveData<List<TextSample>> getTextSamples() {
		return textSamples;
	}

	private static class addTextSampleTask extends AsyncTask<TextSample, Void, Void>{
		private TextSampleDAO textSampleDAO;

		private addTextSampleTask(TextSampleDAO textSampleDAO) {
			this.textSampleDAO = textSampleDAO;
		}

		@Override
		protected Void doInBackground(TextSample... textSamples) {
			textSampleDAO.add(textSamples[0]);
			return null;
		}
	}

	private static class updateTextSampleTask extends AsyncTask<String, Void, Void>{
		private TextSampleDAO textSampleDAO;

		private updateTextSampleTask(TextSampleDAO textSampleDAO) {
			this.textSampleDAO = textSampleDAO;
		}

		@Override
		protected Void doInBackground(String... strings) {
			textSampleDAO.update(strings[0], strings[1]);
			return null;
		}
	}

	private static class deleteTextSampleTask extends AsyncTask<TextSample, Void, Void>{
		private TextSampleDAO textSampleDAO;

		private deleteTextSampleTask(TextSampleDAO textSampleDAO) {
			this.textSampleDAO = textSampleDAO;
		}

		@Override
		protected Void doInBackground(TextSample... textSamples) {
			textSampleDAO.delete(textSamples[0]);
			return null;
		}
	}

	private static class deleteAllTextSampleTask extends AsyncTask<TextSample, Void, Void>{
		private TextSampleDAO textSampleDAO;

		private deleteAllTextSampleTask(TextSampleDAO textSampleDAO) {
			this.textSampleDAO = textSampleDAO;
		}

		@Override
		protected Void doInBackground(TextSample... textSamples) {
			textSampleDAO.deleteAllSamples();
			return null;
		}
	}
}
