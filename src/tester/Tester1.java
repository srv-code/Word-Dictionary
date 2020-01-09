package tester;

import util.text.dict.WordDictionary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.BackingStoreException;


/**
 * <p>A simple tester of the APIs of the WordDictionary class.</p>
 * <p>It works by parsing the options and arguments from the 
 * command line to demonstrate different features of the application.</p>
 * In case of help regarding the usage of this class specify the --help or -h
 * option in the command line which will show the different options and their 
 * respective meanings.
 */
public class Tester1 {	
	private static final long MAX_UPLOAD_BYTES = 200*1024*1024L; // 200MB
	
	/* option switches initialised with the default values */
	private static boolean verboseEnabled = false;
	private static boolean initTest = false;
	private static boolean showWords = false;
	private static Path uploadFilePath = null;
	private static String dictName = null;
	private static String searchKey = null;

	/**
	 * Main entry point of this application.
	 * @param args The option and arguments to this 
	 * tester application.
	 */
	public static void main(String[] args) {
		Exception exceptionCaught = null;
		try {
			if(args.length == 0) {
				showHelpMenu();
				System.exit(StandardExitCodes.NORMAL);
			}

			setOptions(args);

			verboseWrite("Initialising WordDictionary...");			
			WordDictionary dict = new WordDictionary();

			if(initTest) 
				System.exit(StandardExitCodes.NORMAL);

			if(dictName != null) {
				verboseWrite("Selecting dictionary: '%s' ...", dictName);
				dict.selectOrCreateDictionary(dictName);
			}
			
			System.out.printf("Selected dictionary name='%s'\n", dict.getSelectedDictionaryName());
			
			if(uploadFilePath == null) {
				verboseWrite("Loading dictionary...");
				dict.loadDictionary();				
			} else {
				verboseWrite("Uploading from file (%s) ...", uploadFilePath);
				dict.uploadFromFile(uploadFilePath);
			}
			
			if(searchKey != null) {
				verboseWrite("Searching for word='%s' ...", searchKey);
				System.out.println("Key present: " + dict.isWordPresent(searchKey));
			}
			if(showWords) {
				System.out.println("\nWords in dictionary:");
				int count=0;
				for(String word: dict.getWords())
					System.out.printf("[%5d]  %s\n", ++count, word);
				System.out.println("Total: " + count + "\n");
			}
		} catch(IllegalArgumentException e) {
			exceptionCaught = e;
			System.err.println("Error: Invalid option: '" + e.getMessage() + "'");
		} catch(ArrayIndexOutOfBoundsException e) {
			exceptionCaught = e;
			System.err.println("Error: Argument not provided for the last option");
		} catch(BackingStoreException e) {
			exceptionCaught = e;
			System.err.println("Error: Cannot accessing database: " + e.getMessage());
		} catch(IOException e) {  
			exceptionCaught = e;
			System.err.println("Error: Cannot accessing upload file: " + e.getMessage());
		} catch(Exception e) {
			exceptionCaught = e;
			System.err.printf("Error: %s\n", e.getMessage());
		} finally {
			if(verboseEnabled && exceptionCaught != null)
				exceptionCaught.printStackTrace(System.err);
			System.exit(StandardExitCodes.ERROR);
		}
	}
	
	private static void verboseWrite(final String formatString, final Object... args) {
		if(verboseEnabled)
			System.out.printf(formatString + "\n", args);
	}
	
	private static void setOptions(final String[] args) throws IOException {
		for(int i=0, len=args.length; i<len; i++) {
			switch(args[i]) {
				case "-h":
				case "--help":
					showHelpMenu();
					System.exit(StandardExitCodes.NORMAL);
				
				case "-v":
				case "--verbose":
					verboseEnabled = true;
					break;
					
				case "-i":
				case "--init":
					initTest = true;
					break;
				
				case "-f":
				case "--file":
					uploadFilePath = Paths.get(args[++i]);
					try {
						if(Files.size(uploadFilePath) > MAX_UPLOAD_BYTES)
							throw new IOException("Upload size limit exceeded ("+ MAX_UPLOAD_BYTES +" bytes)");
					} catch(IOException e) {
						throw e;
					} 
					break;
					
				case "-d":
				case "--dict":
					dictName = args[++i];
					break;
					
				case "-k":
				case "--key":
					searchKey = args[++i];
					break;
					
				case "-w":
				case "--words":
					showWords = true;
					break;
					
				default: 
					throw new IllegalArgumentException(args[i]);
			} 
		} 
	}
	
	private static void showHelpMenu() {
        System.out.println("Word Dictionary");
		System.out.println("Purpose: Shows demo for the WordDictionary utility program.");
		System.out.printf ("Version: %.2f\n", WordDictionary.APP_VERSION);
        System.out.println();
		System.out.println("Options:");
		System.out.println("    --help,    -h                Show this help menu");
		System.out.println("    --verbose, -v                Enable verbose mode");
		System.out.println("    --init,    -i                Database initialisation testing");
		System.out.println("    --words,   -w                Show all words in dictionary");
		System.out.println("    --file,    -f <file-name>    Upload from file 'file-name' to the selected dictionary");
		System.out.println("    --dict,    -d <dict-name>    Select or create dictionary named as 'dict-name'");
		System.out.println("    --key,     -k <key-string>   Search for 'key-string' from the selected dictionary");
        System.out.println();
        StandardExitCodes.showMessage();
	}
}