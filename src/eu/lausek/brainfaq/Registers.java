package eu.lausek.brainfaq;

import java.util.*;

/**
 * If a register pointer is advanced over more than one cell at a time, it could
 * be the case that a call to `add` skips several indices making them
 * effectively inconsistent. (If ArrayList was used)
 * 
 * We only want to initialize a Cell if it is actively used. This can be ensured
 * when the Cell is requested inside the Registers `get` method.
 * 
 * Our target is to have a list, that is also capable of managing negative and
 * skipped indices.
 * 
 * @author lausek
 *
 */

public class Registers {

	private HashMap<Integer, Cell> regs;
	private int ptr = 0;

	public Registers() {
		regs = new HashMap<>();
	}

	public void next() {
		ptr++;
	}

	public void prev() {
		ptr--;
	}

	private Cell getCell() {
		Cell selected = regs.get(ptr);
		if(selected == null) {
			regs.put(ptr, new Cell());
			return regs.get(ptr);
		}
		return selected;
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
