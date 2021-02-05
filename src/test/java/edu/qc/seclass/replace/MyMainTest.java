package edu.qc.seclass.replace;  

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class MyMainTest {

    private ByteArrayOutputStream outStream;
    private ByteArrayOutputStream errStream;
    private PrintStream outOrig;
    private PrintStream errOrig;
    private Charset charset = StandardCharsets.UTF_8;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        outStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outStream);
        errStream = new ByteArrayOutputStream();
        PrintStream err = new PrintStream(errStream);
        outOrig = System.out;
        errOrig = System.err;
        System.setOut(out);
        System.setErr(err);
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(outOrig);
        System.setErr(errOrig);
    }

    // Some utilities

    private File createTmpFile() throws IOException {
        File tmpfile = temporaryFolder.newFile();
        tmpfile.deleteOnExit();
        return tmpfile;
    }

    private File createInputFile1() throws Exception {
        File file1 =  createTmpFile();
        FileWriter fileWriter = new FileWriter(file1);

        fileWriter.write("Howdy Bill,\n" +
                "This is a test file for the replace utility\n" +
                "Let's make sure it has at least a few lines\n" +
                "so that we can create some interesting test cases...\n" +
                "And let's say \"howdy bill\" again!");

        fileWriter.close();
        return file1;
    }

    private File createInputFile2() throws Exception {
        File file1 =  createTmpFile();
        FileWriter fileWriter = new FileWriter(file1);

        fileWriter.write("Howdy Bill,\n" +
                "This is another test file for the replace utility\n" +
                "that contains a list:\n" +
                "-a) Item 1\n" +
                "-b) Item 2\n" +
                "...\n" +
                "and says \"howdy Bill\" twice");

        fileWriter.close();
        return file1;
    }

    private File createInputFile3() throws Exception {
        File file1 =  createTmpFile();
        FileWriter fileWriter = new FileWriter(file1);

        fileWriter.write("Howdy Bill, have you learned your abc and 123?\n" +
                "It is important to know your abc and 123," +
                "so you should study it\n" +
                "and then repeat with me: abc and 123");

        fileWriter.close();
        return file1;
    }

    private String getFileContent(String filename) {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(filename)), charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
	}
	// Supply file content for each test specifically
	private File createInputFileCustom(String s) throws Exception {
        File file1 =  createTmpFile();
        FileWriter fileWriter = new FileWriter(file1);

        fileWriter.write(s);

        fileWriter.close();
        return file1;
    }

	// Actual test cases

	@Test
    public void mainTest1() throws Exception {
        // implementation of test case #85 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
		"This one [tE st] shouldnt.\n"+
		"This [te st] shouldnt.\n"+
		"This [te st] should."
	);
	File inputFile2 = createInputFileCustom("Hi\n"+
		"This [te st] shouldnt, this one [te st] should. \n"+
		"This one shouldnt [TE st]."
	);

	String args[] = {"-l","--","te st", "TE ST", "--", inputFile1.getPath(), inputFile2.getPath()};
	Main.main(args);
	
	String expected1 = "Hi\n"+
	"This one [tE st] shouldnt.\n"+
	"This [te st] shouldnt.\n"+
	"This [TE ST] should.";
	String expected2 = "Hi\n"+
	"This [te st] shouldnt, this one [TE ST] should. \n"+
	"This one shouldnt [TE st].";

	String actual1 = getFileContent(inputFile1.getPath());
	String actual2 = getFileContent(inputFile2.getPath());

	
	assertEquals("The files differ!", expected1, actual1);
	assertEquals("The files differ!", expected2, actual2);
	}

	@Test
	public void mainTest2() throws Exception {
		// implementation of test case #16 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi I am \n"+
			"here to take a [TeSt] today. \n"+
			"This [test] should not be modified.\n"+
			"This [tesT] should."
		);
		File inputFile2 = createInputFileCustom("Hello \n"+
			"This [TeST] should, this one [test] shouldnt. \n"+
			"This one should [test]."
		);

        String args[] = {"-f", "-l", "-i","--", "test", "TEST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi I am \n"+
		"here to take a [TEST] today. \n"+
		"This [test] should not be modified.\n"+
		"This [TEST] should.";
		String expected2 = "Hello \n"+
		"This [TEST] should, this one [test] shouldnt. \n"+
		"This one should [TEST].";
		
		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest3() throws Exception {
		// implementation of test case #17 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi I am \n"+
			"here to take a test today. \n"+
			"This tEst should not be modified.\n"+
			"This test should."
		);
		File inputFile2 = createInputFileCustom("Hello \n"+
			"This test should, this one test shouldnt. \n"+
			"This one should test."
		);

        String args[] = {"-f", "-l","--", "test", "TEST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi I am \n"+
		"here to take a TEST today. \n"+
		"This tEst should not be modified.\n"+
		"This TEST should.";
		String expected2 = "Hello \n"+
		"This TEST should, this one test shouldnt. \n"+
		"This one should TEST.";
		
		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest4() throws Exception {
		// implementation of test case #18 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi I am \n"+
			"here to take a [tEsT] today. \n"+
			"This [test] should not be modified.\n"+
			"This [test] should not."
		);
		File inputFile2 = createInputFileCustom("Hello \n"+
			"This [teST] should, this one [test] shouldnt. \n"+
			"This one shouldn't [test]."
		);

        String args[] = {"-f", "-i","--", "test", "TEST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi I am \n"+
		"here to take a [TEST] today. \n"+
		"This [test] should not be modified.\n"+
		"This [test] should not.";
		String expected2 = "Hello \n"+
		"This [TEST] should, this one [test] shouldnt. \n"+
		"This one shouldn't [test].";
		
		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}

	@Test
	public void mainTest5() throws Exception {
		// implementation of test case #19 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [test] should.\n"+
			"This [teSt] should not be modified.\n"+
			"This [test] should not."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [test] should, this one [test] shouldnt. \n"+
			"This one shouldn't [tEst]."
		);

        String args[] = {"-f","--","test", "TEST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [TEST] should.\n"+
		"This [teSt] should not be modified.\n"+
		"This [test] should not.";
		String expected2 = "Hi\n"+
		"This [TEST] should, this one [test] shouldnt. \n"+
		"This one shouldn't [tEst].";
		
		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest6() throws Exception {
		// implementation of test case #20 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [test] shouldnt.\n"+
			"This [teSt] should not be modified.\n"+
			"This [TEst] should."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [test] shouldnt, this one [test] shouldnt. \n"+
			"This one should [tEst]."
		);

        String args[] = {"-l","-i","--","test", "TEST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [test] shouldnt.\n"+
		"This [teSt] should not be modified.\n"+
		"This [TEST] should.";
		String expected2 = "Hi\n"+
		"This [test] shouldnt, this one [test] shouldnt. \n"+
		"This one should [TEST].";
		
		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest7() throws Exception {
		// implementation of test case #21 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [test] shouldnt.\n"+
			"This [teSt] should not be modified.\n"+
			"This [test] should."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [test] shouldnt, this one [test] shouldnt. \n"+
			"This one should [test]."
		);

        String args[] = {"-l","--","test", "TEST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [test] shouldnt.\n"+
		"This [teSt] should not be modified.\n"+
		"This [TEST] should.";
		String expected2 = "Hi\n"+
		"This [test] shouldnt, this one [test] shouldnt. \n"+
		"This one should [TEST].";
		
		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}

	@Test
	public void mainTest8() throws Exception {
		// implementation of test case #22 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [test] should.\n"+
			"This [teSt] should be modified.\n"+
			"This [TEst] should."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [test] should, this one [Test] should. \n"+
			"This one should [tesT]."
		);

        String args[] = {"-i","--","test", "TEST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [TEST] should.\n"+
		"This [TEST] should be modified.\n"+
		"This [TEST] should.";
		String expected2 = "Hi\n"+
		"This [TEST] should, this one [TEST] should. \n"+
		"This one should [TEST].";
		
		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest9() throws Exception {
		// implementation of test case #23 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [test] should.\n"+
			"This [teSt] shouldnt be modified.\n"+
			"This [TEst] shouldnt."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [test] should, this one [test] should. \n"+
			"This one should [test]."
		);

        String args[] = {"--","test", "TEST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [TEST] should.\n"+
		"This [teSt] shouldnt be modified.\n"+
		"This [TEst] shouldnt.";
		String expected2 = "Hi\n"+
		"This [TEST] should, this one [TEST] should. \n"+
		"This one should [TEST].";
		
		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest10() throws Exception {
		// implementation of test case #32 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [test] should.\n"+
			"This [teSt] shouldnt be modified.\n"+
			"This [TEst] should."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [test] should, this one [test] shouldnt. \n"+
			"This one should [tesT]."
		);

        String args[] = {"-i","-f","-l","--","test", "TE ST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [TE ST] should.\n"+
		"This [teSt] shouldnt be modified.\n"+
		"This [TE ST] should.";
		String expected2 = "Hi\n"+
		"This [TE ST] should, this one [test] shouldnt. \n"+
		"This one should [TE ST].";
		
		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest11() throws Exception {
		// implementation of test case #33 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [test] should.\n"+
			"This [test] should be modified.\n"+
			"This [teSt] shouldnt."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [tEst] shouldnt, this one [test] should. \n"+
			"This one should [test]."
		);

        String args[] = {"-f","-l","--","test", "TE ST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [TE ST] should.\n"+
		"This [TE ST] should be modified.\n"+
		"This [teSt] shouldnt.";
		String expected2 = "Hi\n"+
		"This [tEst] shouldnt, this one [TE ST] should. \n"+
		"This one should [TE ST].";
		
		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest12() throws Exception {
		// implementation of test case #34 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [test] should.\n"+
			"This [test] shouldnt be modified.\n"+
			"This [teSt] shouldnt."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [tEst] should, this one [test] shouldnt. \n"+
			"This one shouldnt [test]."
		);

        String args[] = {"-f","-i","--","test", "TE ST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [TE ST] should.\n"+
		"This [test] shouldnt be modified.\n"+
		"This [teSt] shouldnt.";
		String expected2 = "Hi\n"+
		"This [TE ST] should, this one [test] shouldnt. \n"+
		"This one shouldnt [test].";
		
		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest13() throws Exception {
		// implementation of test case #35 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [tEst] shouldnt.\n"+
			"This [test] should be modified.\n"+
			"This [test] shouldnt."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [test] should, this one [test] shouldnt. \n"+
			"This one shouldnt [Test]."
		);

        String args[] = {"-f","--","test", "TE ST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [tEst] shouldnt.\n"+
		"This [TE ST] should be modified.\n"+
		"This [test] shouldnt.";
		String expected2 = "Hi\n"+
		"This [TE ST] should, this one [test] shouldnt. \n"+
		"This one shouldnt [Test].";
		
		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest14() throws Exception {
		// implementation of test case #36 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [tEst] shouldnt.\n"+
			"This [test] shouldnt be modified.\n"+
			"This [tEst] should."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [test] shouldnt, this one [test] shouldnt. \n"+
			"This one should [test]."
		);

        String args[] = {"-l","-i","--","test", "TE ST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [tEst] shouldnt.\n"+
		"This [test] shouldnt be modified.\n"+
		"This [TE ST] should.";
		String expected2 = "Hi\n"+
		"This [test] shouldnt, this one [test] shouldnt. \n"+
		"This one should [TE ST].";
		
		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest15() throws Exception {
		// implementation of test case #37 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [test] shouldnt.\n"+
			"This [test] should be modified.\n"+
			"This [tEst] shouldnt."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [test] shouldnt, this one [test] shouldnt. \n"+
			"This one should [test]."
		);

        String args[] = {"-l","--","test", "TE ST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [test] shouldnt.\n"+
		"This [TE ST] should be modified.\n"+
		"This [tEst] shouldnt.";
		String expected2 = "Hi\n"+
		"This [test] shouldnt, this one [test] shouldnt. \n"+
		"This one should [TE ST].";

		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest16() throws Exception {
		// implementation of test case #38 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [test] should.\n"+
			"This [test] should.\n"+
			"This [tEst] should."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [test] should, this one [tESt] should. \n"+
			"This one should [test]."
		);

        String args[] = {"-i","--","test", "TE ST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [TE ST] should.\n"+
		"This [TE ST] should.\n"+
		"This [TE ST] should.";
		String expected2 = "Hi\n"+
		"This [TE ST] should, this one [TE ST] should. \n"+
		"This one should [TE ST].";

		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest17() throws Exception {
		// implementation of test case #39 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [test] should.\n"+
			"This [teSt] shouldnt.\n"+
			"This [tEst] shouldnt."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [test] should, this one [tESt] shouldnt. \n"+
			"This one should [test]."
		);

        String args[] = {"--","test", "TE ST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [TE ST] should.\n"+
		"This [teSt] shouldnt.\n"+
		"This [tEst] shouldnt.";
		String expected2 = "Hi\n"+
		"This [TE ST] should, this one [tESt] shouldnt. \n"+
		"This one should [TE ST].";

		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest18() throws Exception {
		// implementation of test case #64 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [te st] should.\n"+
			"This [teSt] shouldnt.\n"+
			"This [tE st] should."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [te st] should, this one [tE St] should. \n"+
			"This one shouldnt [test]."
		);

        String args[] = {"-f","-l","-i","--","te st", "TEST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [TEST] should.\n"+
		"This [teSt] shouldnt.\n"+
		"This [TEST] should.";
		String expected2 = "Hi\n"+
		"This [TEST] should, this one [TEST] should. \n"+
		"This one shouldnt [test].";

		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest19() throws Exception {
		// implementation of test case #65 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [te st] should.\n"+
			"This [te st] shouldnt.\n"+
			"This [te st] should."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [te st] should, this one [te st] should. \n"+
			"This one shouldnt [test]."
		);

        String args[] = {"-f","-l","--","te st", "TEST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [TEST] should.\n"+
		"This [te st] shouldnt.\n"+
		"This [TEST] should.";
		String expected2 = "Hi\n"+
		"This [TEST] should, this one [TEST] should. \n"+
		"This one shouldnt [test].";

		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest20() throws Exception {
		// implementation of test case #66 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [tE st] should.\n"+
			"This [te st] shouldnt.\n"+
			"This [te st] shouldnt."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [TE st] should, this one [te st] shouldnt. \n"+
			"This one shouldnt [test]."
		);

        String args[] = {"-f","-i","--","te st", "TEST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [TEST] should.\n"+
		"This [te st] shouldnt.\n"+
		"This [te st] shouldnt.";
		String expected2 = "Hi\n"+
		"This [TEST] should, this one [te st] shouldnt. \n"+
		"This one shouldnt [test].";

		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest21() throws Exception {
		// implementation of test case #67 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [tE st] shouldnt.\n"+
			"This [te st] should.\n"+
			"This [te st] shouldnt."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [TE st] shouldnt, this one [te St] shouldnt. \n"+
			"This one should [te st]."
		);

        String args[] = {"-f","--","te st", "TEST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [tE st] shouldnt.\n"+
		"This [TEST] should.\n"+
		"This [te st] shouldnt.";
		String expected2 = "Hi\n"+
		"This [TE st] shouldnt, this one [te St] shouldnt. \n"+
		"This one should [TEST].";

		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest22() throws Exception {
		// implementation of test case #68 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [tE st] shouldnt.\n"+
			"This [te st] shouldnt.\n"+
			"This [tE st] should."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [TE st] shouldnt, this one [te St] shouldnt. \n"+
			"This one should [te st]."
		);

        String args[] = {"-l","-i","--","te st", "TEST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [tE st] shouldnt.\n"+
		"This [te st] shouldnt.\n"+
		"This [TEST] should.";
		String expected2 = "Hi\n"+
		"This [TE st] shouldnt, this one [te St] shouldnt. \n"+
		"This one should [TEST].";

		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest23() throws Exception {
		// implementation of test case #69 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [tE st] shouldnt.\n"+
			"This [te st] should.\n"+
			"This [tE st] shouldnt."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [TE st] shouldnt, this one [te st] shouldnt. \n"+
			"This one should [te st]."
		);

        String args[] = {"-l","--","te st", "TEST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [tE st] shouldnt.\n"+
		"This [TEST] should.\n"+
		"This [tE st] shouldnt.";
		String expected2 = "Hi\n"+
		"This [TE st] shouldnt, this one [te st] shouldnt. \n"+
		"This one should [TEST].";

		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest24() throws Exception {
		// implementation of test case #70 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [tE st] should.\n"+
			"This [te st] should.\n"+
			"This [tEst] shouldnt."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [TE st] should, this one [te st] should. \n"+
			"This one should [te ST]."
		);

        String args[] = {"-i","--","te st", "TEST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [TEST] should.\n"+
		"This [TEST] should.\n"+
		"This [tEst] shouldnt.";
		String expected2 = "Hi\n"+
		"This [TEST] should, this one [TEST] should. \n"+
		"This one should [TEST].";

		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest25() throws Exception {
		// implementation of test case #71 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [tE st] shouldnt.\n"+
			"This [te st] should.\n"+
			"This [tEst] shouldnt."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [TE st] shouldnt, this one [te st] should. \n"+
			"This one should [te st]."
		);

        String args[] = {"--","te st", "TEST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [tE st] shouldnt.\n"+
		"This [TEST] should.\n"+
		"This [tEst] shouldnt.";
		String expected2 = "Hi\n"+
		"This [TE st] shouldnt, this one [TEST] should. \n"+
		"This one should [TEST].";

		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest26() throws Exception {
		// implementation of test case #80 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [tE st] should.\n"+
			"This [te st] shouldnt.\n"+
			"This [te st] should."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [TE st] should, this one [te st] should. \n"+
			"This one shouldnt [test]."
		);

        String args[] = {"-i","-l","-f","--","te st", "TE ST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [TE ST] should.\n"+
		"This [te st] shouldnt.\n"+
		"This [TE ST] should.";
		String expected2 = "Hi\n"+
		"This [TE ST] should, this one [TE ST] should. \n"+
		"This one shouldnt [test].";

		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest27() throws Exception {
		// implementation of test case #81 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [tE st] shouldnt.\n"+
			"This [te st] should.\n"+
			"This [test] shouldnt."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [TE st] shouldnt, this one [te st] should. \n"+
			"This one should [te st]."
		);

        String args[] = {"-l","-f","--","te st", "TE ST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [tE st] shouldnt.\n"+
		"This [TE ST] should.\n"+
		"This [test] shouldnt.";
		String expected2 = "Hi\n"+
		"This [TE st] shouldnt, this one [TE ST] should. \n"+
		"This one should [TE ST].";

		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest28() throws Exception {
		// implementation of test case #82 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [tE st] should.\n"+
			"This [te st] shouldnt.\n"+
			"This [test] shouldnt."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [TEst] shouldnt, this one [te St] should. \n"+
			"This one shouldnt [te st]."
		);

        String args[] = {"-f","-i","--","te st", "TE ST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [TE ST] should.\n"+
		"This [te st] shouldnt.\n"+
		"This [test] shouldnt.";
		String expected2 = "Hi\n"+
		"This [TEst] shouldnt, this one [TE ST] should. \n"+
		"This one shouldnt [te st].";

		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest29() throws Exception {
		// implementation of test case #83 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [tE st] shouldnt.\n"+
			"This [te st] should.\n"+
			"This [te st] shouldnt."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [TE st] shouldnt, this one [te St] shouldnt. \n"+
			"This one should [te st]."
		);

        String args[] = {"-f","--","te st", "TE ST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [tE st] shouldnt.\n"+
		"This [TE ST] should.\n"+
		"This [te st] shouldnt.";
		String expected2 = "Hi\n"+
		"This [TE st] shouldnt, this one [te St] shouldnt. \n"+
		"This one should [TE ST].";

		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	@Test
	public void mainTest30() throws Exception {
		// implementation of test case #84 in catpart.txt.tsl
		File inputFile1 = createInputFileCustom("Hi\n"+
			"This one [tE st] shouldnt.\n"+
			"This [te st] shouldnt.\n"+
			"This [te st] should."
		);
		File inputFile2 = createInputFileCustom("Hi\n"+
			"This [TE st] shouldnt, this one [te St] should. \n"+
			"This one shouldnt [test]."
		);

        String args[] = {"-l","-i","--","te st", "TE ST", "--", inputFile1.getPath(), inputFile2.getPath()};
		Main.main(args);
		
		String expected1 = "Hi\n"+
		"This one [tE st] shouldnt.\n"+
		"This [te st] shouldnt.\n"+
		"This [TE ST] should.";
		String expected2 = "Hi\n"+
		"This [TE st] shouldnt, this one [TE ST] should. \n"+
		"This one shouldnt [test].";

		String actual1 = getFileContent(inputFile1.getPath());
		String actual2 = getFileContent(inputFile2.getPath());

		assertEquals("The files differ!", expected1, actual1);
        assertEquals("The files differ!", expected2, actual2);
	}
	
}