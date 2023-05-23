+ Document {
	getCurrentLineStartEnd {
		var start, end, str, max;
		str = this.string;
		max = str.size;
		start = this.selectionStart;
		end = start;
		while {
			str[start] !== Char.nl and: { start >= 0 }
		} { start = start - 1 };
		while {
			str[end] !== Char.nl and: { end < max }
		} { end = end + 1 };

		// stumpf aber tuts
		if(start == end){
			start = this.selectionStart - 1;
			end = start;
			while {
				str[start] !== Char.nl and: { start >= 0 }
			} { start = start - 1 };
			while {
				str[end] !== Char.nl and: { end < max }
			} { end = end + 1 };
		};

		^[start + 1, end];
	}
}
