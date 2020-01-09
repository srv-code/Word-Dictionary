# Word Dictionary 
## Synopsis
##### This is the solution to an online challenge (https://www.techgig.com/hackathon/question/TDFsR1JOZEk4ZzJmVkZYTWxPR2hHWVBnR2dvTXl6bnJscWRjTWJoNDltaFZrUVU5MUZJc3RuYWI4cTBRUDhoSA==/1).
A simple dictionary utility program which can upload words from a specified file and store them to a database.  
The database selected here is the operating system's application preferences/settings storage facility for keeping the access easier and independent of the technology being used and also to keep the underlying mechanics lightweight.  
The dictionary data being stored is separate for each user, that means one user accessing the data stored by him/her cannot be accessed by other even in the same machine and using the same application.  
This class takes few defaults if not specified by the user and therefore should be taken care by the calling module using its API.

## Features
- Option to enable debug mode, i.e. to show the information about each major step being carried out.
- Option to initialize database testing.
- Option to show all words in the dictionary.
- Option to specify the file from which the words to be uploaded to the dictionary database.
- Option to select or create a dictionary.
- Option to search for a key from the selected dictionary database.

### Default behavior
- Verbose mode is disabled.
- All words in the dictionary database is not shown.