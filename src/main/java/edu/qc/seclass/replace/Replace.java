package edu.qc.seclass.replace;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class Replace {
	boolean[] flags;
	String[] args;
	ArrayList<String> files;
	ArrayList<String> filesContent;
	HashMap<String, ArrayList<Integer> > allOccurrences;
	Charset charset;
	String from;
	String to;
	boolean err;
	public Replace(String[] args){
		flags = new boolean[4]; // Stores whether a flag argument was passed [0: -b] [1: -f] [2: -l] [3: -i]
		this.args = args;
		files = new ArrayList<String>();
		filesContent = new ArrayList<String>();
		allOccurrences = new HashMap<String, ArrayList<Integer>>();
		charset = StandardCharsets.UTF_8;
		err = false;
		parseArgs();
		if(err)
			return;
		readAllFiles();
		backupFiles();
		trackAllOccurrences();
		replaceOccurrences();
		writeToFiles();
		
	}

	private void writeToFiles() {
		int index = 0;
		String content;
		File file;
		FileWriter writer;
		while(index < files.size()){
			try{
				file = new File(files.get(index));
				writer = new FileWriter(file);
				content = filesContent.get(index);
				writer.write(content);
				writer.close();
			}catch(Exception e){
				System.out.println("Error writing to file: "+files.get(index)+" .");
			}
			index++;
		}
	}

	private void replaceOccurrences() {
		int fileIndex = 0;
		String content ="";
		StringBuilder newContent;
		ArrayList<Integer> all;
		int occurrIndex;
		while(fileIndex < files.size()){
			try{
				all = allOccurrences.get(files.get(fileIndex));
				content = filesContent.get(fileIndex);
				newContent = new StringBuilder();
				boolean modified = false;
				if(!flags[1] && !flags[2] && all.size() > 0){ // replace all occurrences
					modified = true;
					int j = 0;
					int start = 0;
					while(j < all.size()){
						occurrIndex = all.get(j);
						newContent.append(content.substring(start, occurrIndex)); //  append from current start to occurrence
						newContent.append(to); // to
						start = occurrIndex + from.length();
						j++;
					}
					// append remainder
					if(start < content.length()){
						newContent.append(content.substring(start, content.length()));
					}	
				}
				else if( (flags[1] || flags[2]) && all.size() > 0){ // -f OR -l was flagged
					int start = 0;
					modified = true;
					if(all.size() == 1 && flags[1] && flags[2]){ // -f & -l affect the same occurrence
						occurrIndex = all.get(0);
						newContent.append(content.substring(start, occurrIndex)); // everything until occurrence
						newContent.append(to);
						start = occurrIndex + from.length(); 
						// append remainder
						if(start < content.length()){
							newContent.append(content.substring(start, content.length()));
						}
					}else if(flags[1] && !flags[2]){ // only -f
				
						occurrIndex = all.get(0);
						newContent.append(content.substring(start, occurrIndex)); // everything before 
						newContent.append(to);
						start = occurrIndex + from.length(); 
						// append remainder
						if(start < content.length()){
							newContent.append(content.substring(start, content.length()));
						}

					}else if(!flags[1] && flags[2]){ // only -l

						occurrIndex = all.get(all.size()-1);
						newContent.append(content.substring(start, occurrIndex)); // everything before 
						newContent.append(to);
						start = occurrIndex + from.length(); 
						// append remainder
						if(start < content.length()){
							newContent.append(content.substring(start, content.length()));
						}

					}else if(flags[1] && flags[2] && all.size() > 1){ // both -f -l , affect different occurrence

						occurrIndex = all.get(0);
						newContent.append(content.substring(start, occurrIndex));
						newContent.append(to);
						start = occurrIndex + from.length();
						// stuff between first and last
						occurrIndex = all.get(all.size()-1);
						newContent.append(content.substring(start, occurrIndex));
						newContent.append(to);

						start = occurrIndex + from.length(); 
						// append remainder
						if(start < content.length()){
							newContent.append(content.substring(start, content.length()));
						}

					}
				}
				if(modified){
					filesContent.set(fileIndex, newContent.toString());
				}
			}catch(Exception e){
				System.out.println("Error replacing occurrences in file: "+files.get(fileIndex)+" . Ignoring this file...");
				files.remove(fileIndex);
				filesContent.remove(fileIndex);
				fileIndex--;// new file took spot, negate index increment
			}	
			fileIndex++;
		}
	}

	private void trackAllOccurrences() {
		// takes note of all the indexs where the substring occured. -i ignores case in from string
		int index = 0;
		ArrayList<Integer> spots;
		String content;
		while(index < filesContent.size()){
			try{ // uses file path as key in hashmap
				spots = new ArrayList<Integer>();
				content = filesContent.get(index);
				for(int i = 0; i < content.length(); i++){
					if(content.regionMatches(flags[3], i, from, 0, from.length())  ){
						spots.add(i); // found an occurrence, mark where it happened
					}
				}
				allOccurrences.put(files.get(index), spots);

			}catch(Exception e){
				System.out.println("Error tracking occurrences in file: "+files.get(index)+" . Ignoring this file...");
				files.remove(index);
				filesContent.remove(index);
				index--;// new file took spot, negate index increment
			}
			index++;
		}
		
	}

	private void backupFiles() {
		if(flags[0]){ // only executes if flag was set to true
			int index = 0;
			File backUp;
			FileWriter writeBackup;
			while(index < files.size()){
				try{
					backUp = new File(files.get(index)+".bck");
					writeBackup = new FileWriter(backUp);
					writeBackup.write(filesContent.get(index));
					writeBackup.close();
				}catch(Exception e){
					// cant create backup, DONT attempt to replace, if -b is flagged, files are important
					System.out.println("Could not create backup for file: "+files.get(index)+" . Ignoring this file..");
					files.remove(index);
					filesContent.remove(index); // new file took spot, negate index increment
					index--;
				}
				index++;
			}
		}
	}

	private void readAllFiles() {
		// goes through every String supplied as a file in args, ignores a file if it throws an error
		int index = 0;
		String content;
		while(index < files.size() ){
			try{
				content = new String(Files.readAllBytes(Paths.get(files.get(index))), charset);
				filesContent.add(content);
			}catch(Exception e){
				Main.fileError(files.get(index));
				files.remove(index);
				index--; // next file now occupates same index so negate index increment. 
			}
			index++;
		}
	}

	private void parseArgs() {
		// syntax:    [OPT FLAGS] -- from to -- [FILES]
		// parse potential OPT flags first
		// Stores whether a flag argument was passed in flags array [0: -b] [1: -f] [2: -l] [3: -i]
		int index = 0;
		boolean stillFlag = true;
		try{ // parse flags
			while(index < args.length && stillFlag){
				switch(args[index]){
					case "-b":
						flags[0] = true;
						break;
					case "-f":
						flags[1] = true;
						break;
					case "-l":
						flags[2] = true;
						break;
					case "-i":
						flags[3] = true;
						break;
					case "--":
						stillFlag = false;
						break;
					default:
						stillFlag = false;
						throw new IllegalArgumentException();
				}
				index++;
			}

		}catch(IllegalArgumentException e){
			Main.usage();
			err = true;
			return;
		}

		try{
			from = args[index++];
			to = args[index++];
		}catch(IndexOutOfBoundsException e){
			Main.usage();
			err = true;
			return;
		}

		if(index < args.length && args[index] == "--"){
			index++;
		}else{
			Main.usage();
			err = true;
			return;
		}
		int numOfFiles = 0;
		while(index < args.length){
			files.add(args[index++]);
			numOfFiles++;
		}
		if(numOfFiles == 0){
			Main.noFilesError();
			err = true;
			return;
		}	
	}	
}
