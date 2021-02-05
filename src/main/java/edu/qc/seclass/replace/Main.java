package edu.qc.seclass.replace;

public class Main {
	
    public static void main(String[] args) {
		Replace rep = new Replace(args);
    }


	public static void usage() {
        System.err.println("Usage: Replace [-b] [-f] [-l] [-i] <from> <to> -- " + "<filename> [<filename>]*" );
	}
	
	public static void fileError(String f) {
        System.err.println("File " + f + " not found");
    }
	public static void noFilesError() {
		System.err.println("MUST include at minimum one file.");
		usage();
    }
}