/*
Same as .order, but using quickSort instead of sort
*/
+SequenceableCollection {
    quickOrder { arg function;
            var array, orderFunc;
            // returns an array of indices that would sort the collection into order.
            if(this.isEmpty) { ^[] };
            if (function.isNil) { function = { arg a, b; a <= b }; };
            array = [this, (0..this.lastIndex)].flop;
            orderFunc = {|a,b| function.value(a[0], b[0]) };
            ^array.quickSort(orderFunc).flop[1]
    }
}
