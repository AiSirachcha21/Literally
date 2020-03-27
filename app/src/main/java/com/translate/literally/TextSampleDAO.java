package com.translate.literally;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TextSampleDAO {

	/**
	 * Inserts text sample into DB
	 * @param textSample Text Sample to be added
	 */
	@Insert
	void add(TextSample textSample);

	/**
	 * Updates Text Sample in DB
	 * @param text Text Sample to be updated
	 */
	@Query("UPDATE textSampleTable SET descriptionText = :newText WHERE descriptionText = :text ")
	void update(String text, String newText);

	/**
	 * Deletes Text sample from DB
	 * @param textSample Text Sample to be Deleted
	 */
	@Delete
	void delete(TextSample textSample);


	/**
	 * Deletes all text samples in DB
	 */
	@Query("DELETE FROM textSampleTable")
	void deleteAllSamples();


	/**
	 * Gets all data in the Text Samples table
	 * @return Live Data List of Text Samples in ascending alphabetical order
	 */
	@Query("SELECT * FROM textSampleTable ORDER BY descriptionText ASC")
	LiveData<List<TextSample>> getAllSamples();



}
