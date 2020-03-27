package com.translate.literally;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LanguageDAO {

	@Insert
	void add(Language language);

	/**
	 * Used to get a specific language from the DB list
	 * @param langCode Unique identifier for language
	 */
	@Query("SELECT * FROM languageTable WHERE langCode = :langCode ")
	Language getLanguage(String langCode);


	@Query("SELECT * FROM languageTable WHERE state=1 ORDER BY langCode ASC")
	List<Language> getSubscribedLanguages();

	/**
	 * Gets all languages from DB List
	 */
	@Query("SELECT * FROM languageTable ORDER BY langCode ASC")
	LiveData<List<Language>> getAllLanguages();

	/**
	 * Used to update a single Language Object in DB
	 * @param code Language Object passed to update relevent field
	 */
	@Transaction @Query("Update languageTable set state =:state where langCode=:code")
	void updateState(String code, int state);


}
