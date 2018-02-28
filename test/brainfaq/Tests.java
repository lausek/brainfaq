package brainfaq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import eu.lausek.brainfaq.*;

public class Tests {

	@Before
	public void setUp() {
		// disable debugger
		Logger.setActive(false);
	}

	private void compare(String filename) {
		String is = null;
		String should = null;

		try {
			should = new String(Files.readAllBytes(Paths.get("programs/output/" + filename))).trim();
		} catch (IOException e1) {
			fail();
		}

		try {
			ByteArrayOutputStream bArray = new ByteArrayOutputStream();
			new Program("programs/" + filename + ".bf", new PrintStream(bArray)).execute();
			is = bArray.toString().trim();
		} catch (IOException e) {
			fail();
		}

		is = is.replaceAll("\\r|\\n", "");
		should = should.replaceAll("\\r|\\n", "");

		assertEquals(is, should);
	}

	@Test
	public void helloWorld() {
		compare("helloworld");
	}
	
	@Test
	public void beer() {
		compare("beer");
	}
	
	@Test
	public void mandelbrot() {
		compare("mandelbrot");
	}
	
	@Test
	public void unicode() {
		try {
			new Program("programs/unicode.bf").execute();
		} catch (IOException e) {
			fail();
		}
	}
	
}
