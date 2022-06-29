
Load the fxml through a new instance (eg. new FXMLLoader) instead of statically, because if you don’t you have no way to dynamically change the GUI. Eg. If you want to change what a label says when a button is pressed

Make sure to be clear on the fully qualified path when referencing files in the bash script
The directory that the script is run is usually in the project root directory.

When trying to debug Process Builder make sure to read the output of the bash script, this isn’t automatically written to stdout in eclipse. 


