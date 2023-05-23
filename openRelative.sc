/*
Open a file from the same folder as the executing file in SC IDE, by providing a filename
*/
+ String {
	openRelative { arg warn = true, action;
		var path = thisProcess.nowExecutingPath;
		if(path.isNil) { Error("can't open relative to an unsaved file").throw};
		if(path.basename == this) { Error("should not open a file from itself").throw };
		// ^(path.dirname ++ thisProcess.platform.pathSeparator ++ this).loadPaths(warn, action)
		Document.open(path.dirname +/+ this)
	}
}