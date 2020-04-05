package com.translate.literally;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "textSampleTable")
public class TextSample {

	@PrimaryKey(autoGenerate = true)
	private int id;
	private String descriptionText;
	private int selectedStatus;

	public TextSample(String descriptionText) {
		this.descriptionText = descriptionText;
		selectedStatus = 0;
	}

	public int getSelectedStatus() {
		return selectedStatus;
	}

	public void setSelectedStatus(int selectedStatus) {
		this.selectedStatus = selectedStatus;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getDescriptionText() {
		return descriptionText;
	}

	public void setDescriptionText(String descriptionText) {
		this.descriptionText = descriptionText;
	}
}
