package eu.lausek.brainfaq;

import java.util.*;

public class Registers {

	private List<Cell> posRegs = null, negRegs = null;
	private int ptr = 0;
	
	
	
	// private static int negPointer(int ptr) {
	// assert (ptr < 0);
	// return (ptr * -1) - 1;
	// }

	public void next() {
		ptr++;
	}

	public void prev() {
		ptr--;
	}

	public Cell get() {
		// TODO: rewrite 
		if (0 <= ptr) {

			if (negRegs == null) {
				negRegs = new ArrayList<>();
			}

			if (posRegs.size() <= ptr) {
				posRegs.add(ptr, new Cell());
			}

			return posRegs.get(ptr);

		} else {

			int normalized = (ptr * -1) - 1;

			if (negRegs == null) {
				negRegs = new ArrayList<>();
			}
			
			if(negRegs.size() <= normalized) {
				negRegs.add(normalized, new Cell());
			}

			return negRegs.get(normalized);

		}
	}
	
	public void increment() {
		get().increment();
	}

	public void decrement() {
		get().decrement();
	}

}
