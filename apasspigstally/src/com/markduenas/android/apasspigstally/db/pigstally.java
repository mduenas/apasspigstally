package com.markduenas.android.apasspigstally.db;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class pigstally implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private int tempScore;
	@DatabaseField
	private int bankScore;
	@DatabaseField
	private int firstRoll;
	@DatabaseField
	private String firstRollString;
	@DatabaseField
	private String secondRollString;
	@DatabaseField
	private boolean pigout;
	@DatabaseField
	private String playerName;

	public pigstally()
	{
		this.playerName = "Player1";
	}

	public pigstally(String name)
	{
		this.playerName = name;
	}

	public boolean isPigout()
	{
		return pigout;
	}

	public String getFirstRoll()
	{
		return firstRollString;
	}

	public String getSecondRoll()
	{
		return secondRollString;
	}

	public int getTempScore()
	{
		return tempScore;
	}

	public int getBankScore()
	{
		return bankScore;
	}

	public boolean roll(String msg)
	{
		pigout = false;
		String currentRoll = msg.trim();
		if (!currentRoll.equals("Bank It") && !currentRoll.equals("Total Loss (Pigs touching)"))
		{
			int roll = 0;
			if (currentRoll.contentEquals("Sider"))
			{
				roll = 1;
			}
			if (currentRoll.contentEquals("Sider dot"))
			{
				roll = 1;
			}
			if (currentRoll.contentEquals("Trotter"))
			{
				roll = 5;
			}
			if (currentRoll.contentEquals("Razorback"))
			{
				roll = 5;
			}
			if (currentRoll.contentEquals("Snouter"))
			{
				roll = 10;
			}
			if (currentRoll.contentEquals("Leaning Jowler"))
			{
				roll = 15;
			}
			if (firstRoll > 0)
			{
				// This is the second roll
				if (firstRoll == 1)
				{
					// need to check for "pig out"
					if (firstRollString.equals(currentRoll))
					{
						// for a double sider we only give 1 point
						tempScore = tempScore + firstRoll;
						// set pig2 roll
						secondRollString = currentRoll;
						// reset first roll
						firstRoll = 0;
					}
					else
					{
						if (currentRoll.contains("Sider"))
						{
							// pig out (no score added to total)
							tempScore = 0;
							firstRoll = 0;
							// reset Pig1 Roll
							firstRollString = "ready";
							// reset Pig2 Roll
							secondRollString = "ready";
							pigout = true;
						}
						else
						{
							// for any roll combination with a sider
							// only the non-sider roll is counted (i.e. Trotter + Sider = Trotter (5))
							tempScore = tempScore + roll;
							// set text
							secondRollString = currentRoll;
							// reset values
							firstRoll = 0;
						}
					}
				}
				else
				{
					// add additional points for doubles
					if (firstRollString.equals(currentRoll))
					{
						if (currentRoll.equals("Trotter"))
						{
							roll = roll + 10;
						}
						if (currentRoll.equals("Razorback"))
						{
							roll = roll + 10;
						}
						if (currentRoll.equals("Snouter"))
						{
							roll = roll + 20;
						}
						if (currentRoll.equals("Leaning Jowler"))
						{
							roll = roll + 30;
						}
					}
					// add the previous roll and the current roll to the temporary total
					// unless it's a sider, then just leave the 1 out
					if (roll == 1)
					{
						tempScore = tempScore + firstRoll;
					}
					else
					{
						tempScore = tempScore + firstRoll + roll;
					}
					// set text
					secondRollString = currentRoll;
					// reset values
					firstRoll = 0;
				}
			}
			else
			{
				// This is the first roll
				firstRollString = currentRoll;
				firstRoll = roll;
				secondRollString = "ready";
			}
		}
		else
		{
			// Allow the user to bank the current running tally
			if (currentRoll.contentEquals("Bank It"))
			{
				bankScore = bankScore + tempScore;
				tempScore = 0;
				firstRollString = "ready";
				secondRollString = "ready";
			}
			// Pigs touching is total loss of current tally and all banked total
			if (currentRoll.contentEquals("Total Loss (Pigs touching)"))
			{
				tempScore = 0;
				bankScore = 0;
				firstRollString = "ready";
				secondRollString = "ready";
			}
		}
		// tvScoreThusFar.setText("Score thus far: " + tempScore);
		// tvScore.setText("Bank Score: " + bankScore);
		return true;
	}

	public void setPlayerName(String name)
	{
		this.playerName = name;
	}

	public CharSequence getPlayerName()
	{
		return playerName;
	}
}
