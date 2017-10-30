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
		// Disable debugger
		Logger.debug = true;
	}
	
	private void compare(String filename) {
		String is = null;
		String should = null;
		
		try {
			should = new String(Files.readAllBytes(Paths.get("programs/output/"+filename))).trim();
		} catch (IOException e1) {
			fail();
		}
		
		try {
			ByteArrayOutputStream bArray = new ByteArrayOutputStream();
			new Program("programs/"+filename+".bf", new PrintStream(bArray)).execute();
			is = bArray.toString().trim();
		} catch (IOException e) {
			fail();
		}
		
		is.replace("\r", "");
		should.replace("\r", "");
		
		is.replace("\n", "");
		should.replace("\n", "");
		
		assertEquals(is, should);
	}
	
	@Test
	public void helloWorld() {
		compare("helloworld");
	}
	
}
