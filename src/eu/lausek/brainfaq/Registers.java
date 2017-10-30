package eu.lausek.brainfaq;

import java.util.*;

public class Registers {

	private List<Cell> posRegs = null, negRegs = null;
	private int ptr = 0;
	
	public Registers() {
		posRegs = new ArrayList<>();
		negRegs = new ArrayList<>();
	}
	
	public void next() {
		ptr++;
	}

	public void prev() {
		ptr--;
	}

	private Cell getCell() {
		// Access on positive index
		int normalized = 0;
		List<Cell> listRef = null;

		if (0 <= ptr) {
			listRef = posRegs;
			normalized = ptr;
		} else {
			listRef = negRegs;
			normalized = (ptr * -1) - 1;
		}

		if (listRef.size() <= normalized) {
			listRef.add(normalized, new Cell());
		}

		return listRef.get(normalized);
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
