package eu.lausek.brainfaq;

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

	public final int REGISTER_SIZE = 30000;

	private Cell[] regs;
	private int ptr = REGISTER_SIZE / 2; // Start in the middle

	public Registers() {
		regs = new Cell[REGISTER_SIZE];
	}

	public void next() {
		next(1);
	}
	
	public void next(int times) {
		ptr += times;
	}

	public void prev() {
		prev(1);
	}
	
	public void prev(int times) {
		ptr -= times;
	}

	private Cell getCell() {
		if (regs[ptr] == null) {
			regs[ptr] = new Cell();
		}
		return regs[ptr];
	}

	public void set(int value) {
		getCell().setValue(value);
	}

	public int get() {
		return getCell().getValue();
	}

	public void increment(int times) {
		getCell().increment(times);
	}
	
	public void decrement(int times) {
		getCell().decrement(times);
	}

	/**
	 * Dump currently used registers to the output stream.
	 */
	public void dump() {
		System.out.println("\tReg: Val");
		int i = 0;
		for (Cell c : regs) {
			if (c == null) {
				continue;
			}
			System.out.printf("\t%d: %d\t", i, c.getValue());
			if (i % 4 == 0) {
				System.out.println();
			}
			i++;
		}
	}

}

class Cell {
	
	private int value = 0;
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public void increment(int times) {
		value += times;
	}
	
	public void decrement(int times) {
		value -= times;
	}
	
}
