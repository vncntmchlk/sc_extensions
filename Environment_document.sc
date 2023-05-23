+ Environment {
	document {
			var txt = "";
			this.asSortedArray.do { |arr|
				txt = txt ++ "~% = %;\n".format(arr[0], arr[1].asCompileString)
			};
			Document.new(this.envirKey.asString ++ "_temp", txt, this);
	}
}