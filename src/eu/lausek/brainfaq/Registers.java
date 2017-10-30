package eu.lausek.brainfaq;

import java.util.*;

public class Registers {

	private List<Cell> posRegs = null, negRegs = null;
	private int ptr = 0;

	public void next() {
		ptr++;
	}

	public void prev() {
		ptr--;
	}

	private Cell getCell() {
		// TODO: rewrite 
		if (0 <= ptr) {

			if (posRegs == null) {
				posRegs = new ArrayList<>();
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
	
	public void set(int value) {
		getCell().setValue(value);
	}
	
	public int get() {
		return getCell().getValue();
	}
	
	public void increment() {
		getCell().increment();
	}

	public void decrement() {
		getCell().decrement();
	}

}
