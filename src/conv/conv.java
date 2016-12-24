/* 
 * Available commands:
 * */

package conv;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.*;

public class conv {
	//Initialize some music data variables
    public static List<String> conversionBank = new ArrayList<String>();
    public static int currentOctave = 4;	//Default octave starts at 4 (1 through 8)
    public static int previousOctave = 4;	//Keep track of the previously-changed octave
    public static int currentNote = 1;	//96 total notes, starting from 1
    public static int currentFileLine = 0;
public static void main(String [] args) {
    	// Create an input scanner for keyboard
    	Scanner stdIn = new Scanner(System.in);
        // The name of the file to open.
        String inputFile;
        
        System.out.print("Which file are we reading?\n(Default file extension is .txt): ");
        inputFile = stdIn.next();
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

        convert(inputFile, outputFile);
        
        //Close the scanner
        stdIn.close();
        System.exit(0);
    }


public static void convert(String inputFile, String outputFile)
	{
		//Loop through the whole file until we have all the data we need.
	    File file = new File(inputFile);
	    
	    System.out.println("Reading...");
	    
	    try(Scanner reader = new Scanner(file))
	    {
	    	parseText(reader);
	    }
	    catch (IOException e)
	    {
	    	System.out.println("Couldn't read the file!");
	        System.exit(0);
	    }
	    
	    System.out.println("Writing...");
	    
	    try(FileWriter writer = new FileWriter(outputFile))
	    {
	    	int q=0;
	    	while (q<conversionBank.size())
	    	{
	    		String outputTempLine = conversionBank.get(q);
	    		q++;
		    	writer.write(outputTempLine);	
	    	}
	    	System.out.println("Done!");
	    }
	    catch(IOException e)
	    {
	    	System.out.println("Couldn't write to the file!");
	        System.exit(0);
	    }
	}

	public static void parseText(Scanner reader)
	{
		String parsedLine = null;
		
		while(reader.hasNextLine())
		{
			//Get the current line and start reading it
			parsedLine = reader.nextLine();
			currentFileLine++;
			boolean hitAComment = false; //Ignore anything past ; so the parser ignores comments
			
			int countCharsUntilComment=0;
			
			int w = 0;
			while (parsedLine.charAt(w)!=';' && w<parsedLine.length()-1)
			{
			countCharsUntilComment++;
			w++;
			}
			//Loop through every character of the parsed line and amend what to do about each one.
			for (int i=0; i<w;i++)
			{
				if (!hitAComment) //As long as we haven't hit a comment yet...
				{
					//amend the type of command we hit and choose what to do with it.
					
					switch(parsedLine.toLowerCase().charAt(i))
					{
					case ';':
						hitAComment=true;
						break;
					case 'o':
						amendOctave(parsedLine, i);
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
						i += amendCommand(parsedLine, i);
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
					conversionBank.add("\t; " + remainderString);
				}
			}
		}
	}
	
	//int currentOctave = 4;	//Default octave starts at 4 (1 through 8)
    //int currentNote = 1;	//96 total notes, starting from 1
	
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
				conversionBank.add("\n\toctave " + (currentOctave+1));
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
				conversionBank.add("\n\toctave " + (currentOctave-1));
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
		conversionBank.add("\n\tnote " + outputNote + ", " + noteLength);
		
		//Revert the octave if we changed it earlier for getting out of the octave range
		if (tempOctave)
		{
			if (inputNotePitch=='c')
			{
				if (conversionBank.get(conversionBank.size()-3).substring(1, conversionBank.get(conversionBank.size()-3).length()-1).equals("	octave "))
				{
					conversionBank.remove(conversionBank.size()-3);
				}
				conversionBank.add("\n\toctave " + currentOctave);
				
				tempOctave=false;
			}
			else if (inputNotePitch=='b')
			{
				if (conversionBank.get(conversionBank.size()-3).substring(1, conversionBank.get(conversionBank.size()-3).length()-1).equals("	octave "))
				{
					conversionBank.remove(conversionBank.size()-3);
				}
				conversionBank.add("\n\toctave " + currentOctave);
				tempOctave=false;
			}
		}
		
		return charsToSkip;
	}

	public static void amendOctave(String parsedLine, int i)
	{
		
		if (parsedLine.charAt(i+1) > '8' ||parsedLine.charAt(i+1) < '1' )
		{
			System.out.println("Found an invalid octave on line " + currentFileLine + " at position " + (i+1) + ": Skipping...");
		}
		else
		{
			currentOctave = parsedLine.charAt(i+1) - '0';	//Default octave starts at 4 (1 through 8)
			if (conversionBank.get(conversionBank.size()-1).substring(1, conversionBank.get(conversionBank.size()-1).length()-1).equals("	octave "))
			{
				conversionBank.remove(conversionBank.size()-1);
			}
		conversionBank.add("\n\toctave " + currentOctave);
		}
	}
	
	public static int amendStereoPan(String parsedLine, int i)
	{
		int charsToSkip = 1;
		switch(parsedLine.charAt(i+1))
		{
		case '1': //right
			conversionBank.add("\n\tstereopanning $0f");
			break;
		case '-': //left
			conversionBank.add("\n\tstereopanning $f0");
			charsToSkip++;
			break;
		case '0': //center
			conversionBank.add("\n\tstereopanning $ff");
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
		conversionBank.add("\n\ttempo " + findTempo);
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
		conversionBank.add("\n\tvolume " + findVol);
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
		case "@wd": charsToSkip += 3 + waveDuty(parsedLine, i); break;
		case "@ve": charsToSkip += 3 + volumeEnvelope(parsedLine, i); break;
		case "@v": charsToSkip += 2 + vibChange(); break;
		default: break;
		}
		
		return charsToSkip;
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
		conversionBank.add("\n\tdutycycle " + findVal);
		
		return 1;
	}

	public static int volumeEnvelope(String parsedLine, int i)
	{
		return 1;
	}
	
	public static int vibChange()
	{
		return 1;
	}
	
	public static void shiftOctave(char direction, String parsedLine, int i)
	{
		//Remove the last octave if it's directly on the line above this one to prevent redundancy+save space
		if (conversionBank.get(conversionBank.size()-1).substring(1, conversionBank.get(conversionBank.size()-1).length()-1).equals("	octave "))
		{
			conversionBank.remove(conversionBank.size()-1);
		}
		
		//Shift the octave
		if (direction=='<' && currentOctave>1)
		{
			currentOctave--;
		}
		else if (direction=='>' && currentOctave<9)
		{
			currentOctave++;
		}
		
		//Finally, add the octave to the bank.
		conversionBank.add("\n\toctave " + currentOctave);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}