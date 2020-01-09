/* Challenge URL: https://www.techgig.com/hackathon/question/TDFsR1JOZEk4ZzJmVkZYTWxPR2hHWVBnR2dvTXl6bnJscWRjTWJoNDltaFZrUVU5MUZJc3RuYWI4cTBRUDhoSA==/1 */

package util.text.dict;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;


/**
 * <p>A simple dictionary utility program which can upload words from a specified file
 * and store them to a database.</p>
 * <p>The database selected here is the operating system's
 * application preferences/settings storage facility for keeping the access easier
 * and independent of the technology being used and also to keep the underlying mechanics lightweight.
 * The dictionary data being stored is separate for each user, that means one user accessing
 * the data stored by him/her cannot be accessed by other even in the same machine and using the 
 * same application.</p>
 * <p>This class takes few defaults if not specified by the user and therefore should be 
 * taken care by the calling module using its API.</p>
 */
public class WordDictionary {
    public static final float APP_VERSION = 1.00f;

	private final String DEFAULT_DICT_NAME = "default";
	private final Preferences DICT_ROOT_NODE;
	private final String DICT_NODE_PATH = "dicts";
	private final String KEY_VALUE = "word";
	
	private Preferences currentDictNode = null;
	List<String> wordList = new ArrayList<>();
	
	/**
	 * Initializes the system and sets the dictionary name to default initially.
	 */
	public WordDictionary() {
		/* init default dict */
		DICT_ROOT_NODE = Preferences.userNodeForPackage(getClass()).node(DICT_NODE_PATH);
		currentDictNode = DICT_ROOT_NODE.node(DEFAULT_DICT_NAME); /* made default selection */
	}
	
	/**
	 * Clears the underlying dictionary and also updates the changes.
	 * @throws BackingStoreException In case of any error while accessing the database.
	 */
	public void clearDictionary() throws BackingStoreException {
		wordList.clear();
		currentDictNode.clear();
		currentDictNode.flush();
	}
	
	/**
	 * Uploads the words from the specified file to the selected dictionary.
	 * It first loads the words previously stored, if any, into the selected
	 * dictionary. Then fetches the words from the specified file and stores 
	 * them to the dictionary parsing each line at a time and stripping the 
	 * valid words and also refusing entry in case of any duplicity.
	 * @param filePath Path of the file to be uploaded.
	 * @throws IOException In case of error while accessing the upload file.
	 * @throws BackingStoreException In case of error while accessing the database.
	 */
	public void uploadFromFile(final Path filePath) throws IOException, BackingStoreException {
		/* load existing words */
		loadDictionary();
		
		/* load new words from file */
		for(String line: Files.readAllLines(filePath)) {
			for(String word: stripWords(line)) {
				if(word.length() == 0)
					continue;
				if(!wordList.contains(word)) {
					wordList.add(word);
					currentDictNode.put(word, KEY_VALUE);
				}
			}
		}
		currentDictNode.flush();
	}
	
	private List<String> stripWords(final String line) {
		List<String> strippedWordList = new ArrayList<>();
		StringBuilder tmpWord = new StringBuilder();
		boolean isInsideWord = false;
		char ch;
		
		for(int i=0, len=line.length(); i<len; i++) {
			ch = line.charAt(i);
			if(isInsideWord) {
				if(Character.isLetterOrDigit(ch)) {
					tmpWord.append(ch);
				} else {
					isInsideWord = false;
					strippedWordList.add(tmpWord.toString());
					tmpWord.setLength(0);
				}
			} else {
				if(Character.isLetterOrDigit(ch)) {
					isInsideWord = true;
					tmpWord.append(ch);
				}
			}
		}
		if(tmpWord.length() > 0) 
			strippedWordList.add(tmpWord.toString());
		
		return strippedWordList;
	}
	
	/**
	 * Loads the words from the selected dictionary.
	 * @throws BackingStoreException In case of error while accessing the database.
	 */
	public void loadDictionary() throws BackingStoreException {
		for(String word: currentDictNode.keys())
			wordList.add(word);
	}
	
	/**
	 * Searches the word in the selected dictionary and returns the search result.
	 * @param searchKey The key word to be found in the selected dictionary.
	 * @return True if the word is present in the selected dictionary otherwise false.
	 */
	public boolean isWordPresent(final String searchKey) {
		return wordList.contains(searchKey);
	}
	
	/**
	 * Selects the specified dictionary, if present, otherwise creates an 
	 * empty dictionary by the specified name.
	 * @param dictName The name of the dictionary to be selected or created.
	 */
	public void selectOrCreateDictionary(final String dictName) {
		currentDictNode = DICT_ROOT_NODE.node(dictName==null ? DEFAULT_DICT_NAME : dictName);
	}
	
	/**
	 * For fetching the name of the currently selected dictionary.
	 * @return The currently selected dictionary name.
	 */
	public String getSelectedDictionaryName() {
		return currentDictNode.name();
	}
	
	/**
	 * For fetching an array of words from the selected dictionary.
	 * @return An array of words from the selected dictionary.
	 */
	public String[] getWords() {
		return wordList.toArray(new String[0]);
	}
}