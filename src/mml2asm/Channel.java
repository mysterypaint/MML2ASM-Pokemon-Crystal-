package mml2asm;

import java.util.ArrayList;
import java.util.List;

public class Channel {
	private String identifier = "";
	private boolean channelEnabled = false;
	private boolean loopMode = false;
	private int lastOctave = 4;
	private int lastVolume = -1;
	private int lastDutyWave = -1;
	private int lastTempo = -1;
	private String output="";
	private List<String> channelData = new ArrayList<String>();
	private List<String> loopData = new ArrayList<String>();
	
	public Channel(String identifier)
	{
		this.identifier = identifier;
		this.add("\n\toctave 4");
	}

	public void setLastVolume(String input)
	{
		if (channelEnabled)
		{
		lastVolume=Integer.parseInt(input);
		}
	}
	
	public void setLoopMode(boolean input)
	{
		loopMode = input;
	}

	public void setTempo(int inputTempo)
	{
		if (channelEnabled)
		{
			if (inputTempo!=lastTempo)
			{
				if (!loopMode)
				{
					if (get(channelData.size()-1).substring(1, 8).equals("\ttempo "))
					{
						remove(channelData.size()-1);
					}
				}
				else
				{
					if (get(loopData.size()-1).substring(1, 8).equals("\ttempo "))
					{
						remove(loopData.size()-1);
					}
				}
				chanWrite("\n\ttempo " + inputTempo);
				lastTempo=inputTempo;
			}
		}
	}

	public void setVol(String inputVol)
	{
		if (channelEnabled)
		{
			if (Integer.parseInt(inputVol)!=lastVolume)
			{
				if (!loopMode)
				{
					if (get(channelData.size()-1).substring(1, 9).equals("\tvolume "))
					{
						remove(channelData.size()-1);
					}
				}
				else
				{
					if (get(loopData.size()-1).substring(1, 9).equals("\tvolume "))
					{
						remove(loopData.size()-1);
					}
				}
				int outputVol = Integer.parseInt(inputVol);
				this.chanWrite("\n\tvolume " + inputVol);
				lastVolume=outputVol;
			}
		}
	}
	
	public void setDutyWave(String inputDutyWave)
	{
		if (channelEnabled)
		{
			if (Integer.parseInt(inputDutyWave)!=lastDutyWave)
			{
				if (!loopMode)
				{
					if (get(channelData.size()-1).substring(1, 12).equals("\tdutycycle "))
					{
						remove(channelData.size()-1);
					}
				}
				else
				{
					if (get(loopData.size()-1).substring(1, 12).equals("\tdutycycle "))
					{
						remove(loopData.size()-1);
					}
				}
				int outputDutyWave = Integer.parseInt(inputDutyWave);
				this.chanWrite("\n\tdutycycle " + inputDutyWave);
				lastDutyWave=outputDutyWave;
			}
		}
	}
	
	public void setOctave(int inputOctave)
	{
		if (channelEnabled)
		{
			if (inputOctave!=lastOctave)
			{
				boolean doNotWrite=false;
				
				if (!loopMode)
				{
					if (get(channelData.size()-1).substring(1, 9).equals("\toctave "))
					{
						remove(channelData.size()-1);
					}
					else if (get(channelData.size()-1).substring(1, 9).equals("\tnote __"))
					{
						doNotWrite=true;
					}
				}
				else
				{
					if (get(loopData.size()-1).substring(1, 9).equals("\toctave "))
					{
						remove(loopData.size()-1);
					}
					else if (get(loopData.size()-1).substring(1, 9).equals("\tnote __"))
					{
						doNotWrite=true;
					}
				}
				
				if (!doNotWrite)
				{
				this.chanWrite("\n\toctave " + inputOctave);
				lastOctave=inputOctave;
				}
			}
		}
	}

	public void setMacroLoopStart()
	{
		
	}
	
	public void setChannelEnabled(boolean input)
	{
		channelEnabled = input;
	}
	
	public boolean getChannelEnabled()
	{
		return channelEnabled;
	}

	public int getLastVolume()
	{
		return lastVolume;
	}
	
	public int getLastTempo()
	{
		return lastTempo;
	}

	public int getChannelDataSize()
	{
		return channelData.size();
	}

	public int getLoopDataSize()
	{
		return loopData.size();
	}
	
	public int getlastOctave()
	{
		return this.lastOctave;
	}

	public String get(int input)
	{
		return channelData.get(input);
	}

	public void add(String input)
	{
		channelData.add(input); //Add to channel output
	}
	
	public void remove(int input)
	{
		channelData.remove(input);
	}

	public void addLoop(String input)
	{
		loopData.add(input); //Add to loop data output
	}
	
	public void removeLoop(String input)
	{
		loopData.remove(input); //Add to loop data output
	}

	public String loopToString()
	{
		output="";
		for(int i=0; i<loopData.size();i++)
		{
			output += loopData.get(i);
		}
        return output;
    }

	@Override
    public String toString()
	{
		output="";
		for(int i=0; i<channelData.size();i++)
		{
			output += channelData.get(i);
		}
        return output;
    }
	
	public void chanWrite(String input)
	{
		int initialLength = input.length();
		
		if (channelEnabled)
		{
			if (!loopMode) //If we're not inside a loop, write data to the channel as usual
			{
				if (input.substring(0, 9).equals("\n\tnote __"))
					{
						if (channelData.get(channelData.size()-1).substring(1, channelData.get(channelData.size()-1).length()-1).equals("	octave "))
						{
							channelData.remove(channelData.size()-1);
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
				channelData.add(input);
			}
			else //If we are in loop mode, however, we should redirect all data to the loop data
			{
				if (input.substring(0, 9).equals("\n\tnote __"))
				{
					if (channelData.get(channelData.size()-1).substring(1, channelData.get(channelData.size()-1).length()-1).equals("	octave "))
					{
						channelData.remove(channelData.size()-1);
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
			channelData.add(input);
			}
		}
	}
}
