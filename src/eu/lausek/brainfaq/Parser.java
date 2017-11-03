package eu.lausek.brainfaq;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;

public class Parser implements java.io.Closeable {

	private final char LAST_CHAR_DEFAULT = ' ';
	private final int BUFFER_SIZE_BLOCK = 32;
	private final int BUFFER_SIZE_START = 10000;

	// how many adjacent chars are the minimum for compression?
	private final int MORE_TIMES_THAN = 2;

	private BufferedReader reader;
	private char[] program = null;
	private char lastChar = LAST_CHAR_DEFAULT;

	// variables for parsing run
	private int total = 0, times = 1, i = 0;

	public Parser(String path) throws FileNotFoundException {
		reader = new BufferedReader(new FileReader(path));
	}

	/**
	 * Reallocate the program array, if the next insert would cause an overflow
	 * 
	 * @param total
	 */
	private void resizeIfOverflow(int total) {
		if (program == null) {
			program = new char[BUFFER_SIZE_START];
		} else if (program.length <= total) {
			program = Arrays.copyOf(program, program.length * 2);
		}
	}

	/**
	 * Write a command sequence to our program buffer
	 */
	private void doInsertStep() {
		// if program array is too short, resize it to the double length
		resizeIfOverflow(total + 2);

		// if lastChar has been added at least MORE_TIMES_THAN and lastChar can be
		// compressed
		if (MORE_TIMES_THAN < times && codeCompressSupported(lastChar)) {
			program[total++] = (char) (48 + times);
			program[total++] = lastChar;
		} else {
			for (int x = 1; x <= times; x++) {
				program[total++] = lastChar;
			}
		}

		times = 1;
		lastChar = LAST_CHAR_DEFAULT;
	}

	/**
	 * Check if a Stack contains correct opening and closing brackets. Returns
	 * false if not.
	 * 
	 * @param bracketStack
	 * @return
	 */
	private static boolean isStackEven(Stack<Bracket> bracketStack) {
		Stack<Bracket> bracketsClosing = new Stack<>();

		while (!bracketStack.isEmpty()) {
			Bracket next = bracketStack.pop();

			if (next.symbol == '[') {
				if (bracketsClosing.isEmpty() || bracketsClosing.pop().symbol != ']') {
					return false;
				}
			} else {
				bracketsClosing.push(next);
			}
		}

		return bracketStack.isEmpty() && bracketsClosing.isEmpty();
	}

	/**
	 * Commands, for which inline compression is allowed/supported: + - < >
	 * 
	 * @param op
	 * @return
	 */
	public boolean codeCompressSupported(char op) {
		return op == 43 || op == 45 || op == 60 || op == 62;
	}

	/**
	 * Loads a BufferedReader into a char array for execution.
	 * 
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	public char[] parse() throws IOException {

		Stack<Bracket> bracketStack = new Stack<>();
		char[] buffer = new char[BUFFER_SIZE_BLOCK];

		resizeIfOverflow(0);

		// put = how many chars have been transferred
		for (int put = reader.read(buffer); put > 0; put = reader.read(buffer)) {
			for (i = 0; i < put; i++) {
				// TODO: make this dependent on opcodes
				switch (buffer[i]) {
				case 91:
				case 93:
					bracketStack.push(new Bracket(buffer[i], total));

				case 43:
				case 44:
				case 45:
				case 46:
				case 60:
				case 62:
					if (lastChar != LAST_CHAR_DEFAULT) {

						// the maximum amount we can store inside a char is 9.
						// if times gets bigger than 9, add current char and reset counter
						if (lastChar != buffer[i] || 9 < times + 1) {
							doInsertStep();
						} else {
							times++;
						}

					}

					lastChar = buffer[i];

					break;
				}

			}

			if (lastChar != LAST_CHAR_DEFAULT) {
				doInsertStep();
			}

		}

		if (!isStackEven(bracketStack)) {
			Logger.error("Incorrect nesting of brackets");
		}

		program = Arrays.copyOf(program, total);
		return program;
	}

	@Override
	public void close() throws IOException {
		program = null;
		reader.close();
	}

}

class Bracket {
	public char symbol;
	public int position;

	public Bracket(char symbol, int pos) {
		this.symbol = symbol;
		this.position = pos;
	}
}