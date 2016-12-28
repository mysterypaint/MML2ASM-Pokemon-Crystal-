
package mml2asm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import java.io.*;

public class MML2ASM
{
	//Initialize some music data variables
	public static String definedLabel = "";
	public static int loopCount = 0;
	public static boolean loopMode = false;
	public static Map<String, Loop> map = new HashMap<String, Loop>();
	public static Channel ChA = new Channel();
	public static Channel ChB = new Channel();
	public static Channel ChC = new Channel();
	public static Channel ChD = new Channel();
    public static List<String> outputBank = new ArrayList<String>();
    public static int currentOctave = 4;	//Default octave starts at 4 (1 through 8)
    public static int previousOctave = 4;	//Keep track of the previously-changed octave
    public static int currentNote = 1;	//96 total notes, starting from 1
    public static int currentFileLine = 0;
	public static String defaultNoteLength = "4";
    public static boolean usingChA = false;
    public static boolean usingChB = false;
    public static boolean usingChC = false;
    public static boolean usingChD = false;
	public static String songName = "Untitled";
	public static boolean loopA = false;
	public static boolean loopB = false;
	public static boolean loopC = false;
	public static boolean loopD = false;
	public static boolean noLoop = false;
	public static int firstChanInHeader = 1;

	public static void main(String [] args)
		{
			
		
			// Create an input scanner for keyboard
			Scanner stdIn = new Scanner(System.in);
			// The name of the file to open.
			String inputFile="";
			
			try
			{
				inputFile = args[0];
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
			System.out.print("Which .txt MML file are we reading?\n(Default file extension is .txt): ");
			inputFile = stdIn.next();
			}
			
			String outputFile = "";
			
			//Is the filename exactly or shorter than 3 characters?
			if (inputFile.length()<=3)
			{
				//If false, add .txt for the default
				outputFile = inputFile + ".asm";
				inputFile = inputFile + ".txt";
				
			}
			//Is the fourth-to-last character of our inputFile a period? (For .txt/.mml detection)
			else if (inputFile.charAt(inputFile.length()-4)!='.')
			{
				//If false, add .txt for the default
				outputFile = inputFile + ".asm";
				inputFile = inputFile + ".txt";
			}
			else
			{
				//If true, get everything before that, store it to "outputFile", then add "_edit.txt".
				for(int i=0; (i<(inputFile.length()-4)); i++)
				{
				outputFile = outputFile + inputFile.charAt(i);
				}
				outputFile = outputFile + ".asm";
			}

			System.out.println("Attempting to open " + inputFile + "...");
			
			
			convert(inputFile, outputFile);

			//Close the scanner
			stdIn.close();
			System.exit(0);
		}


	public static void convert(String inputFile, String outputFile)
		{
			//Loop through the whole file until we have all the data we need.
			File file = new File(inputFile);
			
			try(Scanner reader = new Scanner(file))
			{
				System.out.println("File found! Reading...");
				parseText(reader);
			}
			catch (IOException e)
			{
				//Crash and throw an error if we catch any trouble reading the file
				System.out.println("Couldn't read the file!");
				System.exit(0);
			}
			
			System.out.println("Writing...");
			
			try(FileWriter writer = new FileWriter(outputFile))
			{
				//Merge all the separated channel data into a single text string with a header break in-between
				outputBank.add(";Song: " + songName);
				
				//Handle the music header
				int chanCount = 0;
				if (usingChA){chanCount++; firstChanInHeader=1;}
				if (usingChB){chanCount++; if(!usingChA){firstChanInHeader=2;}}
				if (usingChC){chanCount++; if(!usingChA && !usingChB){firstChanInHeader=3;}}
				if (usingChD){chanCount++; if(!usingChA && !usingChB && !usingChC){firstChanInHeader=4;}}
				outputBank.add("\nMusic_" + songName + ":"); //firstChanInHeader
				
				String tempHeader = "";
				if (usingChA)
				{
					tempHeader+="\n\tmusicheader ";
					if (firstChanInHeader==1)
					{tempHeader += chanCount;}
					else{System.out.println("Impossible???");tempHeader+=1;}
					tempHeader += ", 1, Music_" + songName + "_Ch1";
				}
				if (usingChB)
				{
					tempHeader+="\n\tmusicheader ";
					if (firstChanInHeader==2)
					{tempHeader += chanCount;}
					else{tempHeader+=1;}
					tempHeader += ", 2, Music_" + songName + "_Ch2";
				}
				if (usingChC)
				{
					tempHeader+="\n\tmusicheader ";
					if (firstChanInHeader==3)
					{tempHeader += chanCount;}
					else{tempHeader+=1;}
					tempHeader += ", 3, Music_" + songName + "_Ch3";
				}
				if (usingChD)
				{
					tempHeader+="\n\tmusicheader ";
					if (firstChanInHeader==4)
					{tempHeader += chanCount;}
					else{tempHeader+=1;}
					tempHeader += ", 4, Music_" + songName + "_Ch4";
				}
				
				outputBank.add(tempHeader);
				
				//Start writing channel data
				
				if (usingChA)
				{
					if (ChA.getListSize()>0)
					{
					outputBank.add("\n\nMusic_" + songName + "_Ch1: ; Channel A (Pulse 1)");
					outputBank.add(ChA.toString());
					}
					
					if (loopA)
					{
						outputBank.add("\n\tloopchannel 0, Music_" + songName + "_LoopStart_Ch1");
					}
					else if (noLoop)
					{
						outputBank.add("\n\tendchannel");
					}
					else
					{
						outputBank.add("\n\tloopchannel 0, Music_" + songName + "_Ch1");
					}
				}
				
				if (usingChB)
				{
					if (ChB.getListSize()>0)
					{
					outputBank.add("\n\nMusic_" + songName + "_Ch2: ; Channel B (Pulse 2)");
					outputBank.add(ChB.toString());
					}
					
					if (loopB)
					{
						outputBank.add("\n\tloopchannel 0, Music_" + songName + "_LoopStart_Ch2");
					}
					else if (noLoop)
					{
						outputBank.add("\n\tendchannel");
					}
					else
					{
						outputBank.add("\n\tloopchannel 0, Music_" + songName + "_Ch2");
					}
				}
				
				if (usingChC)
				{
					if (ChC.getListSize()>0)
					{
					outputBank.add("\n\nMusic_" + songName + "_Ch3: ; Channel C (Wave)");
					outputBank.add(ChC.toString());
					}
		
					if (loopC)
					{
						outputBank.add("\n\tloopchannel 0, Music_" + songName + "_LoopStart_Ch3");
					}
					else if (noLoop)
					{
						outputBank.add("\n\tendchannel");
					}
					else
					{
						outputBank.add("\n\tloopchannel 0, Music_" + songName + "_Ch3");
					}
				}
				
				if (usingChD)
				{
					if (ChD.getListSize()>0)
					{
					outputBank.add("\n\nMusic_" + songName + "_Ch4: ; Channel D (White Noise)");
					outputBank.add(ChD.toString());
					}
					
					if (loopD)
					{
						outputBank.add("\n\tloopchannel 0, Music_" + songName + "_LoopStart_Ch4");
					}
					else if (noLoop)
					{
						outputBank.add("\n\tendchannel");
					}
					else
					{
						outputBank.add("\n\tloopchannel 0, Music_" + songName + "_Ch4");
					}
				}
				
				//Now, add all the loops we had to the very bottom of the page
				outputBank.add("\n\n;Loop Defines");
				
				for (String i : map.keySet())
				{
					outputBank.add("\nMusic_" + songName + "_branch_" + map.get(i).getName() + ":");
					outputBank.add(map.get(i).toString());
				}
				
				
				
				//Then, write all the combined data into the actual .txt
				int q=0;
				while (q<outputBank.size())
				{
					String outputTempLine = outputBank.get(q);
					q++;
					writer.write(outputTempLine);	
				}
				System.out.println("Done!");
			}
			catch(IOException e)
			{
				//Crash and throw an error if we catch any trouble writing the file
				System.out.println("Couldn't write to the file!");
				System.exit(0);
			}
		}

		public static void parseText(Scanner reader)
		{
			String parsedLine = null;
			ChA.add("\n\toctave 4");
			ChB.add("\n\toctave 4");
			ChC.add("\n\toctave 4");
			ChD.add("\n\toctave 4");
			boolean checkedTitle = false;
			
			while(reader.hasNextLine())
			{
				//Disable all channels every line before deeming them relevant to the line
				ChA.setChannelEnabled(false);
				ChB.setChannelEnabled(false);
				ChC.setChannelEnabled(false);
				ChD.setChannelEnabled(false);
				
				//Get the current line and start reading it
				parsedLine = reader.nextLine();
				while (parsedLine.equals(""))
				{parsedLine = reader.nextLine();currentFileLine++;}
				
				if (songName.equals("Untitled") && !checkedTitle)
				{
					if (parsedLine.subSequence(0, 7).equals(";title="))
					{
						songName = parsedLine.substring(7, parsedLine.length());
						System.out.println("Parsing data for " + songName + "...");
					}
					else
					{
						System.out.println("Song title not found! Resorting to \"Untitled.\"\n(Please declare your song title using \";title=\" at the top of your txt!)");
					}
					checkedTitle=true;
				}
				currentFileLine++;
				boolean hitAComment = false; //Ignore anything past ; so the parser ignores comments
				
				//Determine which audio channels are relevant
				
				int k=0;
				if (parsedLine.replaceAll("\\s", "").charAt(0)!=';')
				{
					while (parsedLine.charAt(k)!=' ' && k<=parsedLine.length())
					{
						switch(parsedLine.toUpperCase().charAt(k))
						{
						case 'A':
							ChA.setChannelEnabled(true);
							break;
						case 'B':
							ChB.setChannelEnabled(true);
							break;
						case 'C':
							ChC.setChannelEnabled(true);
							break;
						case 'D':
							ChD.setChannelEnabled(true);
							break;
						case ';':
							break; //Ignore all commented lines
						default: System.out.println("Invalid or missing channel declaration on line " + currentFileLine + "Skipping...");
							break;
						}
						k++;
					}
			


				//If we have any channel declaration, find out what it was so we can determine how many characters to skip
				String chanDec = null;
				if (ChA.getChannelEnabled()||ChB.getChannelEnabled()||ChC.getChannelEnabled()||ChD.getChannelEnabled())
				{
					if(parsedLine.contains(" ")){
						chanDec = parsedLine.substring(0, parsedLine.indexOf(" ")); 
					}
				}

				//Remove all spaces from the parsed line before we actually start reading it
				parsedLine = parsedLine.replaceAll("\\s","");

				//Keep track of how far into the line we are.
				int dataStart = chanDec.length();
				int w = dataStart;
				
				//Find data length all the way up to the comment or end of the line
				while (parsedLine.charAt(w)!=';' && w<parsedLine.length()-1)
				{
				w++;
				}
				
				//Loop through every character of the parsed line and amend what to do about each one.
				for (int i=dataStart; i<=w;i++)
				{
					if (!hitAComment) //As long as we haven't hit a comment yet...
					{
						//amend the type of command we hit and choose what to do with it.
						if (parsedLine.charAt(i)=='L')
						{
							//Handle loop point for each channel individually
							handleLoop();
						}
						else if (parsedLine.charAt(i)=='l')
						{
							i += setDefaultNoteLength(parsedLine, i);
						}
						
						switch(parsedLine.toLowerCase().charAt(i))
						{
						case ';':
							hitAComment=true;
							break;
						case 'o':
							i += amendOctave(parsedLine, i);
							break;
						case 'a':
							i += amendNote('a', parsedLine, i);
							break;
						case 'b':
							i += amendNote('b', parsedLine, i);
							break;
						case 'c':
							i += amendNote('c', parsedLine, i);
							break;
						case 'd':
							i += amendNote('d', parsedLine, i);
							break;
						case 'e':
							i += amendNote('e', parsedLine, i);
							break;
						case 'f':
							i += amendNote('f', parsedLine, i);
							break;
						case 'g':
							i += amendNote('g', parsedLine, i);
							break;
						case '^':
							i += amendNote('^', parsedLine, i);
							break;
						case 'y':
							i += amendStereoPan(parsedLine, i);
							break;
						case 't':
							i += amendTempo(parsedLine, i);
							break;
						case 'v':
							i += amendVolume(parsedLine, i);
							break;
						case '@':
							i += amendCommand(parsedLine, i) -1;
							break;
						case '(':
							i = 1 + defineMacro(parsedLine, i, reader, 2);
							break;
						case '[':
							i = defineMacro(parsedLine, i, reader, 1);
							break;
						case ']':
							loopMode=false;
							break;
						case '>':
							shiftOctave('>', parsedLine, i);
							break;
						case '<':
							shiftOctave('<', parsedLine, i);
							break;
						default:
							//The compiler will completely ignore anything else not on this list.
							break;
						}
					}
					else
					{
						//If we hit a comment, just print the rest of the line normally (because we flagged it as a comment in the ASM, too).
						String remainderString = "";
						while (i<parsedLine.length())
						{
							remainderString += parsedLine.charAt(i);
							i++;
						}
						chanWrite("\t; " + remainderString);
					}
				}
			}
		}
		}
		
		public static int defineMacro(String parsedLine, int i, Scanner reader, int dataType)
		{
			//dataType 1 = standard loop
			//dataType 2 = defined loop
			definedLabel = "";
			i++;
			
			while (parsedLine.charAt(i)!=')')
				{
					definedLabel+=parsedLine.charAt(i);
					if (i<parsedLine.length()-1)
					{i++;}
				}
			
			String numOfLoops="";
			String getLabel = "";
			
			//Check if the define already exists		
			if (!map.containsKey(definedLabel))
			{
				if (!loopMode)
				{
					loopCount++;
					map.put(definedLabel, new Loop(definedLabel));
					chanWrite("\nbranch_" + map.get(definedLabel).getName() + ":");
					chanWrite("\n\tcallchannel " + "branch_" + map.get(definedLabel).getName());
					loopMode=true;
				}
				else
				{
					System.out.println("Aborting to prevent potential memory leak: Make sure your label loops are valid!");
					reader.close();
					System.exit(0);
				}
			}
			else
			{
				//Define the loop if it doesn't exist. Crash if it runs into another loop define.
				
				loopMode=false;
				i++;
				
				if (parsedLine.charAt(i)>='0' && parsedLine.charAt(i)<='9')
				{
					while (parsedLine.charAt(i)>='0' && parsedLine.charAt(i)<='9')
					{
						numOfLoops+=parsedLine.charAt(i);
						if (i<parsedLine.length()-2)
						{i++;}
						else{break;}
					}
				}
				else{numOfLoops="0";}

				if (Integer.parseInt(numOfLoops)>=1)
				{
					map.get(definedLabel).incLoopCount();
					getLabel = "Music_" + songName + "_branch_" + definedLabel + "_Loop_" + map.get(definedLabel).getLoopCount();
					chanWrite("\n" + getLabel + ":");
				}
			}
			
			
			
			
			
			getLabel = "Music_" + songName + "_branch_" + definedLabel + "_Loop_" + map.get(definedLabel).getLoopCount();
			
			chanWrite("\n\tcallchannel Music_" + songName + "_branch_" + map.get(definedLabel).getName());
			chanWrite("\n\tloopchannel " + numOfLoops + ", " + getLabel);
			
			return i;
		}
		
		public static int noiseNote(String parsedLine, int i, String note)
		{
			String nFreq = "";
			String noteLength = "";
			switch(note)
			{
			case "kik": nFreq="D#"; break;
			case "hop": nFreq="F#"; break;
			case "hcl": nFreq="C#"; break;
			case "snr": nFreq="C_"; break;
			default: nFreq="D#"; break;
			}
			
			if (parsedLine.charAt(i) > '9' || parsedLine.charAt(i) < '0' )
			{
				noteLength = defaultNoteLength;
			}
			else
			{
				while(parsedLine.charAt(i) <= '9' && parsedLine.charAt(i) >= '0' )
				{
					noteLength+=parsedLine.charAt(i);
					if (i<parsedLine.length()-1)
					{i++;}
					else
					{break;}
				}
			}
			
			noteLength = getNoteLength(noteLength);
			i--;
			chanWrite("\n\tnote " + nFreq + ", " + noteLength);
			return noteLength.length();
		}
		
		
		public static String getNoteLength(String input)
		{
			//Given the input note length, translate it in terms of pokecrystal's music engine and output the numerical value as a string.
			return input;
		}
		
		public static int setDefaultNoteLength(String parsedLine, int i)
		{
			String defaultNoteLength = "";
			i++;
			while(parsedLine.charAt(i) <= '9' && parsedLine.charAt(i) >= '0' )
			{
				defaultNoteLength+=parsedLine.charAt(i);
				i++;
			}
			return defaultNoteLength.length();
		}
		
		public static int amendNote(char inputNotePitch, String parsedLine, int i)
		{
			int charsToSkip = 0;
			boolean hasSharpOrFlat = false;
			boolean tempOctave = false;
			//Calculate where we are on the pitch table
			//Start by re-assinging the note value:
			switch(inputNotePitch)
			{
			case '^':
				currentNote=777;
				break;
			case 'c':
				currentNote=13;
				break;
			case 'd':
				currentNote=3;
				break;
			case 'e':
				currentNote=5;
				break;
			case 'f':
				currentNote=6;
				break;
			case 'g':
				currentNote=8;
				break;
			case 'a':
				currentNote=10;
				break;
			case 'b':
				currentNote=12;
				break;
			default:
				break;		
			}
			
			
			if (parsedLine.charAt(i+1)=='+')
			{
				if (inputNotePitch=='b')
				{
					//If we happen to hit the bounds of the octave, fix it automatically.
					setOctave(currentOctave+1);
					tempOctave=true;
				}
				if (currentNote<96){currentNote++;} //Check all notes to account for b+ and c-
				hasSharpOrFlat=true;
				charsToSkip++;
			}
			else if (parsedLine.charAt(i+1)=='-')
			{
				if (inputNotePitch=='c')
				{
					//If we happen to hit the bounds of the octave, fix it automatically.
					setOctave(currentOctave-1);
					tempOctave=true;
				}
				if (currentNote>1){currentNote--;}
				hasSharpOrFlat=true;
				charsToSkip++;
			}
			
			
			//Let's find our note.
			String outputNote = "";
			
			switch(currentNote)
			{
			case 777: outputNote = "__"; break;
			case 1: outputNote = "C_"; break;
			case 2: outputNote = "C#"; break;
			case 3: outputNote = "D_"; break;
			case 4: outputNote = "D#"; break;
			case 5: outputNote = "E_"; break;
			case 6: outputNote = "F_"; break;
			case 7: outputNote = "F#"; break;
			case 8: outputNote = "G_"; break;
			case 9: outputNote = "G#"; break;
			case 10: outputNote = "A_"; break;
			case 11: outputNote = "A#"; break;
			case 12: outputNote = "B_"; break;
			case 13: outputNote = "C_"; break;
			default: outputNote = "__"; break;
			}
			
			
			//Now, let's amend the length of the note.
			String noteLength = "";
			
			int j = 1;
			if (hasSharpOrFlat){j++;} //Skip one character if we're dealing with a sharp or flat in our note length value
			
			//First, we'll keep track of every number after the sharp or flat until we hit something entirely different.
			
			
			while(parsedLine.charAt(i+j)>='0' && parsedLine.charAt(i+j)<='9')
			{
				noteLength += parsedLine.charAt(i+j);
				if ((i+j)<parsedLine.length()-1)
				{
					j++;
				}
				else
				{
					break;
				}			
			}

			//Store the current line into an arraylist
			chanWrite("\n\tnote " + outputNote + ", " + noteLength);
			
			//Revert the octave if we changed it earlier for getting out of the octave range
			if (tempOctave)
			{
				if (inputNotePitch=='c')
				{
					setOctave(currentOctave+1);
					tempOctave=false;
				}
				else if (inputNotePitch=='b')
				{
					setOctave(currentOctave+1);
					tempOctave=false;
				}
			}
			
			return charsToSkip;
		}

		public static int amendOctave(String parsedLine, int i)
		{
			
			if (parsedLine.charAt(i+1) > '8' ||parsedLine.charAt(i+1) < '1' )
			{
				System.out.println("Found an invalid octave on line " + currentFileLine + " at position " + (i+1) + ": Skipping...");
			}
			else
			{
				currentOctave = parsedLine.charAt(i+1) - '0';	//Default octave starts at 4 (1 through 8)
				setOctave(currentOctave);
			}
			return 1;
		}
		
		public static void setOctave(int currentOctave)
		{
			boolean doNotWrite=false;
			//For each relevant channel, check if we have any redundant octave declarations for output code cleanup. 
			if (ChA.getChannelEnabled())
			{
				try{
					if (ChA.get(ChA.getListSize()-1).substring(1, ChA.get(ChA.getListSize()-1).length()-1).equals("	octave "))
					{
						ChA.remove(ChA.getListSize()-1);
					}
					else if (ChA.get(ChA.getListSize()-1).substring(1, ChA.get(ChA.getListSize()-1).length()-1).equals("	note __"))
					{
						doNotWrite=true;
					}
				}
				catch (ArrayIndexOutOfBoundsException e)
				{
					//Do nothing
				}
			}
			

			if (ChB.getChannelEnabled())
			{
				try{
					if (ChB.get(ChB.getListSize()-1).substring(1, ChB.get(ChB.getListSize()-1).length()-1).equals("	octave "))
					{
						ChB.remove(ChB.getListSize()-1);
					}
					else if (ChB.get(ChB.getListSize()-1).substring(1, ChB.get(ChB.getListSize()-1).length()-1).equals("	note __"))
					{
						doNotWrite=true;
					}
				}
				catch (ArrayIndexOutOfBoundsException e)
				{
					//Do nothing
				}
			}
			

			if (ChC.getChannelEnabled())
			{
				try{
					if (ChC.get(ChC.getListSize()-1).substring(1, ChC.get(ChC.getListSize()-1).length()-1).equals("	octave "))
					{
						ChC.remove(ChC.getListSize()-1);
					}
					else if (ChC.get(ChC.getListSize()-1).substring(1, ChC.get(ChC.getListSize()-1).length()-1).equals("	note __"))
					{
						doNotWrite=true;
					}
				}
				catch (ArrayIndexOutOfBoundsException e)
				{
					//Do nothing
				}
			}
			

			if (ChD.getChannelEnabled())
			{
				try{
					if (ChD.get(ChD.getListSize()-1).substring(1, ChD.get(ChD.getListSize()-1).length()-1).equals("	octave "))
					{
						ChD.remove(ChD.getListSize()-1);
					}
					else if (ChD.get(ChD.getListSize()-1).substring(1, ChD.get(ChD.getListSize()-1).length()-1).equals("	note __"))
					{
						doNotWrite=true;
					}
				}
				catch (ArrayIndexOutOfBoundsException e)
				{
					//Do nothing
				}
			}
			
			if (!doNotWrite)
			{
			chanWrite("\n\toctave " + (currentOctave));
			}
		}
		
		public static int amendStereoPan(String parsedLine, int i)
		{
			int charsToSkip = 1;
			switch(parsedLine.charAt(i+1))
			{
			case '1': //right
				chanWrite("\n\tstereopanning $0f");
				break;
			case '-': //left
				chanWrite("\n\tstereopanning $f0");
				charsToSkip++;
				break;
			case '0': //center
				chanWrite("\n\tstereopanning $ff");
				break;
			}
			return charsToSkip;
		}
		
		public static int amendTempo(String parsedLine, int i)
		{
			int charsToSkip = 0;
			String findTempo = "";
			i++;
			while(parsedLine.charAt(i) >= '0' && parsedLine.charAt(i) <= '9' || parsedLine.charAt(i) == '$')
			{
				findTempo += parsedLine.charAt(i) - '0';
				if (i+1<parsedLine.length())
				{
				i++;
				}
				else
				{
					break;
				}
				charsToSkip++;	
			}

			if (ChA.getLastTempo()!=Integer.parseInt(findTempo) && ChA.getChannelEnabled())
			{
			chanWrite("\n\ttempo " + findTempo);
			ChA.setLastTempo(findTempo);
			}
			if (ChB.getLastTempo()!=Integer.parseInt(findTempo) && ChB.getChannelEnabled())
			{
			chanWrite("\n\ttempo " + findTempo);
			ChB.setLastTempo(findTempo);
			}
			if (ChC.getLastTempo()!=Integer.parseInt(findTempo) && ChC.getChannelEnabled())
			{
			chanWrite("\n\ttempo " + findTempo);
			ChC.setLastTempo(findTempo);
			}
			if (ChD.getLastTempo()!=Integer.parseInt(findTempo) && ChD.getChannelEnabled())
			{
			chanWrite("\n\ttempo " + findTempo);
			ChD.setLastTempo(findTempo);
			}
			
			return charsToSkip;
		}
		
		public static int amendVolume(String parsedLine, int i)
		{
			int charsToSkip = 0;
			String findVol = "";
			i++;
			while(parsedLine.charAt(i) >= '0' && parsedLine.charAt(i) <= '9' || parsedLine.charAt(i) == '$')
			{
				findVol += parsedLine.charAt(i) - '0';
				if (i+1<parsedLine.length())
				{
				i++;
				}
				else
				{
					break;
				}
				charsToSkip++;	
			}

			if (ChA.getLastVolume()!=Integer.parseInt(findVol) && ChA.getChannelEnabled())
			{
			chanWrite("\n\tvolume " + findVol);
			ChA.setLastVolume(findVol);
			}
			if (ChB.getLastVolume()!=Integer.parseInt(findVol) && ChB.getChannelEnabled())
			{
			chanWrite("\n\tvolume " + findVol);
			ChB.setLastVolume(findVol);
			}
			if (ChC.getLastVolume()!=Integer.parseInt(findVol) && ChC.getChannelEnabled())
			{
			chanWrite("\n\tvolume " + findVol);
			ChC.setLastVolume(findVol);
			}
			if (ChD.getLastVolume()!=Integer.parseInt(findVol) && ChD.getChannelEnabled())
			{
			chanWrite("\n\tvolume " + findVol);
			ChD.setLastVolume(findVol);
			}
			return charsToSkip;
		}
		
		public static int amendCommand(String parsedLine, int i)
		{
			String findCommand = "";
			int charsToSkip = 0;
			while(!(parsedLine.charAt(i) >= '0' && parsedLine.charAt(i) <= '9' && parsedLine.charAt(i) != '$'))
			{
				if (parsedLine.charAt(i) != '$')
				{
				findCommand += parsedLine.charAt(i);
				}
				if (i+1<parsedLine.length())
				{
				i++;
				}
				else
				{
					break;
				}
				charsToSkip++;	
			}
			//Determine the command in question
			switch(findCommand)
			{
			case "@wd": charsToSkip += waveDuty(parsedLine, i); break;
			case "@ve": charsToSkip += volumeEnvelope(parsedLine, i); break;
			case "@v": charsToSkip += vibChange(); break;
			case "@k": charsToSkip += noiseNote(parsedLine, i, "kik"); break;
			case "@s": charsToSkip += noiseNote(parsedLine, i, "snr"); break;
			case "@hc": charsToSkip += noiseNote(parsedLine, i, "hcl"); break;
			case "@ho": charsToSkip += noiseNote(parsedLine, i, "hop"); break;
			case "@tn": charsToSkip += toggleNoise(parsedLine, i); break;
			default: break;
			}
			
			return charsToSkip;
		}

		public static int toggleNoise(String parsedLine, int i)
		{
			String findVal = "";
			while(parsedLine.charAt(i) >= '0' && parsedLine.charAt(i) <= '9' || parsedLine.charAt(i) == '$')
			{
				findVal += parsedLine.charAt(i) - '0';
				if (i+1<parsedLine.length())
				{
				i++;
				}
				else
				{
					break;
				}
			}
			chanWrite("\n\ttogglenoise $" + findVal);
			
			return 1;
		}

		public static int waveDuty(String parsedLine, int i)
		{
			String findVal = "";
			while(parsedLine.charAt(i) >= '0' && parsedLine.charAt(i) <= '9' || parsedLine.charAt(i) == '$')
			{
				findVal += parsedLine.charAt(i) - '0';
				if (i+1<parsedLine.length())
				{
				i++;
				}
				else
				{
					break;
				}
			}
			chanWrite("\n\tdutycycle " + findVal);
			
			return 1;
		}

		public static int volumeEnvelope(String parsedLine, int i)
		{
			boolean envelopeUp = true;
			int charsToSkip = 1;
			
			if (parsedLine.charAt(i)=='-')
			{
				envelopeUp = false;
				charsToSkip++;
				i++;
			}
			
			if (envelopeUp)
			{
				
			}
			
			
			return charsToSkip;
		}
		
		public static int vibChange()
		{
			return 1;
		}
		
		public static void shiftOctave(char direction, String parsedLine, int i)
		{
			//Shift the octave
			if (direction=='<' && currentOctave>1)
			{
				currentOctave--;
				setOctave(currentOctave);
			}
			else if (direction=='>' && currentOctave<9)
			{
				currentOctave++;
				setOctave(currentOctave);
			}
		}
		
		public static void handleLoop()
		{
			//Handle loop point for each channel individually
			if (ChA.getChannelEnabled())
			{
				ChA.add("\nMusic_" + songName + "_LoopStart_Ch1: ;The loop point for this channel");
				loopA=true;
			}
			if (ChB.getChannelEnabled())
			{
				ChB.add("\nMusic_" + songName + "_LoopStart_Ch2: ;The loop point for this channel");
				loopB=true;
			}
			if (ChC.getChannelEnabled())
			{
				ChC.add("\nMusic_" + songName + "_LoopStart_Ch3: ;The loop point for this channel");
				loopC=true;
			}
			if (ChD.getChannelEnabled())
			{
				ChD.add("\nMusic_" + songName + "_LoopStart_Ch4: ;The loop point for this channel");
				loopD=true;
			}
		}
		
		public static void chanWrite(String input)
		{
			int initialLength = input.length();
			
			if (loopMode)
			{
				map.get(definedLabel).amend(input);
			}
			else
			{
			if (ChA.getChannelEnabled())
				{
				if (input.substring(0, 9).equals("\n\tnote __"))
					{
						if (ChA.get(ChA.getListSize()-1).substring(1, ChA.get(ChA.getListSize()-1).length()-1).equals("	octave "))
						{
							ChA.remove(ChA.getListSize()-1);
						}
					}
				if (input.contains("_Loop_"))
				{
					if (!input.contains("callchannel"))
					{
						if (input.charAt(input.length()-1)==':')
						{
							input = input.substring(0,initialLength-1) + "_Ch1:";
						}
					}
					else
					{
						input = input.substring(0,initialLength) + "_Ch1";
					}
				}
				ChA.add(input);
				usingChA=true;
				}
			if (ChB.getChannelEnabled())
			{
				if (input.substring(0, 9).equals("\n\tnote __"))
					{
						if (ChB.get(ChB.getListSize()-1).substring(1, ChB.get(ChB.getListSize()-1).length()-1).equals("	octave "))
						{
							ChB.remove(ChB.getListSize()-1);
						}
					}
				if (input.contains("_Loop_"))
				{
					if (!input.contains("callChannel"))
					{
						if (input.charAt(input.length()-1)==':')
						{
							input = input.substring(0,initialLength-1) + "_Ch2:";
						}
					}
					else
					{
						input = input.substring(0,initialLength) + "_Ch2";
					}
				}
				ChB.add(input);
				usingChB=true;
			}

			if (ChC.getChannelEnabled())
			{
				if (input.substring(0, 9).equals("\n\tnote __"))
				{
					if (ChC.get(ChC.getListSize()-1).substring(1, ChC.get(ChC.getListSize()-1).length()-1).equals("	octave "))
					{
						ChC.remove(ChC.getListSize()-1);
					}
				}
				if (input.contains("_Loop_"))
				{
					if (!input.contains("callChannel"))
					{
						if (input.charAt(input.length()-1)==':')
						{
							input = input.substring(0,initialLength-1) + "_Ch3:";
						}
					}
					else
					{
						input = input.substring(0,initialLength) + "_Ch3";
					}
				}
				ChC.add(input);
				usingChC=true;
			}
						
		if (ChD.getChannelEnabled())
			{
				if (input.substring(0, 9).equals("\n\tnote __"))
				{
					if (ChD.get(ChD.getListSize()-1).substring(1, ChD.get(ChD.getListSize()-1).length()-1).equals("	octave "))
					{
						ChD.remove(ChD.getListSize()-1);
					}
				}
				if (input.contains("_Loop_"))
				{
					if (!input.contains("callChannel"))
					{
						if (input.charAt(input.length()-1)==':')
						{
							input = input.substring(0,initialLength-1) + "_Ch4:";
						}
					}
					else
					{
						input = input.substring(0,initialLength) + "_Ch4";
					}
				}
				ChD.add(input);
				usingChD=true;
			}
		}
	}
}