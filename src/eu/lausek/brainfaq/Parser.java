package eu.lausek.brainfaq;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Parser implements java.io.Closeable {
	
	private BufferedReader reader;
	private char[] program = null;
	
	public Parser(String path) throws FileNotFoundException {
		reader = new BufferedReader(new FileReader(path));
	}
	
	public void resizeIfOverflow(int total) {
		if (program.length <= total) {
			program = Arrays.copyOf(program, program.length * 2);
		}
	}

	/**
	 * Commands, for which inline compression is allowed/supported
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
	 * TODO: this needs some optimization
	 * 
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	public char[] parse() throws IOException {
		final int BUFFER_SIZE_BLOCK = 32;
		final int BUFFER_SIZE_START = 10000;
		final int MORE_TIMES_THAN = 2;

		program = new char[BUFFER_SIZE_START];

		char[] buffer = new char[BUFFER_SIZE_BLOCK];
		int total = 0, times = 1, i = 0;
		char lastChar = ' ';

		// put = how many chars have been transferred
		for (int put = reader.read(buffer); put > 0; put = reader.read(buffer)) {
			for (i = 0; i < put; i++) {
				// TODO: make this dependent on opcodes
				switch (buffer[i]) {
				case 43:
				case 44:
				case 45:
				case 46:
				case 60:
				case 62:
				case 91:
				case 93:
					if (lastChar != ' ') {
						if (lastChar != buffer[i]) {

							// If program array is too short, resize it to the double length
							resizeIfOverflow(total + 2);

							if (times > MORE_TIMES_THAN && codeCompressSupported(lastChar)) {
								program[total++] = (char) (48 + times);
								program[total++] = lastChar;
							} else {
								for (int x = 1; x <= times; x++) {
									program[total++] = lastChar;
								}
							}

							times = 1;
							lastChar = ' ';

						} else {

							// TODO: if times gets bigger than 36, add current char and reset counter
							if (9 < times + 1) {
								// If program array is too short, resize it to the double length
								resizeIfOverflow(total + 2);

								if (times > MORE_TIMES_THAN && codeCompressSupported(lastChar)) {
									program[total++] = (char) (48 + times);
									program[total++] = lastChar;
								} else {
									for (int x = 1; x <= times; x++) {
										program[total++] = lastChar;
									}
								}

								lastChar = ' ';
								times = 1;
							} else {
								times++;
							}

						}
					}

					lastChar = buffer[i];

					break;
				}

			}

			if (lastChar != ' ') {

				// If program array is too short, resize it to the double length
				resizeIfOverflow(total + 2);

				if (times > MORE_TIMES_THAN && codeCompressSupported(lastChar)) {
					program[total++] = (char) (48 + times);
					program[total++] = lastChar;
				} else {
					for (int x = 1; x <= times; x++) {
						program[total++] = lastChar;
					}
				}

				times = 1;
				lastChar = ' ';

			}

			// TODO: add code from change clause here too. lastChar could be skipped
			// eventually

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