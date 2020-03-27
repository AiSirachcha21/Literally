package com.translate.literally;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "languageTable")
public class Language {

	@PrimaryKey
	@NonNull
	private String langCode;
	private String langDescription;
	private int state;

	public Language(@NonNull String langCode, String langDescription, int state) {
		this.langCode = langCode;
		this.langDescription = langDescription;
		this.state = state;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@NonNull
	public String getLangCode() {
		return langCode;
	}

	public String getLangDescription() {
		return langDescription;
	}
}
