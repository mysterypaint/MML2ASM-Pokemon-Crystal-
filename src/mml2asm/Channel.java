package mml2asm;

import java.util.ArrayList;
import java.util.List;

public class Channel {
	private boolean channelEnabled = false;
	private int lastOctave = 4;
	private int lastVolume = 9;
	private int lastDutyWave = 0;
	private int lastTempo = 120;
	private String output="";
	private List<String> channelData = new ArrayList<String>();
	
	public Channel()
	{
	}

	public void setLastVolume(String input)
	{
		if (this.channelEnabled)
		{
		this.lastVolume=Integer.parseInt(input);
		}
	}
	
	public void setLastTempo(String input)
	{
		if (this.channelEnabled)
		{
		this.lastTempo=Integer.parseInt(input);
		}
	}
	
	public void setChannelEnabled(boolean input)
	{
		this.channelEnabled = input;
	}
	
	public boolean getChannelEnabled()
	{
		return this.channelEnabled;
	}

	public int getLastVolume()
	{
		return this.lastVolume;
	}
	
	public int getLastTempo()
	{
		return this.lastTempo;
	}

	public int getListSize()
	{
		return channelData.size();
	}
	
	public int getlastOctave()
	{
		return this.lastOctave;
	}

	public int getLastDutyWave()
	{
		return this.lastDutyWave;
	}

	public String get(int input)
	{
		return channelData.get(input);
	}

	public void add(String input)
	{
		channelData.add(input);
	}
	
	public void remove(int input)
	{
		channelData.remove(input);
	}

	@Override
    public String toString() {
		
		for(int i=0; i<channelData.size();i++)
		{
			output += channelData.get(i);
		}
		
        return output;
    }
	
}
