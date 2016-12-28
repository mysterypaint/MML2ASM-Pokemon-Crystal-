package mml2asm;

public class Loop {
	private String output="";
	private String name;
	private int personalLoopCount=0;
	
	// Loop Constructor
	public Loop(String identifier) {  this.name = "Loop_" + identifier; }
	
	public String getName()
	{
		return this.name;
	}

	public int getLoopCount()
	{
		return this.personalLoopCount;
	}
	
	public void incLoopCount()
	{
		personalLoopCount++;
	}
	
	public void amend(String input)
	{
		output += input;
	}
	
	@Override
    public String toString() {
        return output + "\n\tendchannel";
    }

}
