package com.markduenas.android.apasspigstally;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.markduenas.android.PigsTallyActivity.R;

public class PigsTallyActivity extends Activity implements View.OnClickListener
{

	Button buttonSider;
	Button buttonSiderDot;
	Button buttonSnouter;
	Button buttonTrotter;
	Button buttonLeaningJowler;
	Button buttonRazorback;
	Button buttonTotalLoss;
	Button buttonBankIt;
	TextView tvPig1Label;
	TextView tvPig2Label;
	TextView tvScore;
	TextView tvScoreThusFar;
	TextView tvPlayerName;
	Button msg;
	TableLayout tl1;
	private pigstally tally1 = new pigstally();
	private static List<pigstally> listPigsTally = new ArrayList<pigstally>();
	private static long current_id = 0;
	private static long next_id = 0;
	private static long previous_id = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Set up the table views
		tl1 = (TableLayout) findViewById(R.id.TableLayout01);

		// Set up the text views
		tvPig1Label = (TextView) tl1.findViewById(R.id.tvPig1Label);
		tvPig2Label = (TextView) tl1.findViewById(R.id.tvPig2Label);
		tvScoreThusFar = (TextView) tl1.findViewById(R.id.tvScoreThusfarLabel);
		tvScore = (TextView) tl1.findViewById(R.id.tvScore);
		tvPlayerName = (TextView) tl1.findViewById(R.id.tvPlayerName);

		// Set up the buttons
		setButtonListener(tl1, R.id.buttonSider);
		setButtonListener(tl1, R.id.buttonSiderDot);
		setButtonListener(tl1, R.id.buttonSnouter);
		setButtonListener(tl1, R.id.buttonRazorback);
		setButtonListener(tl1, R.id.buttonLeaningJowler);
		setButtonListener(tl1, R.id.buttonTrotter);
		setButtonListener(tl1, R.id.buttonBankIt);
		setButtonListener(tl1, R.id.buttonTotalLoss);

		if (savedInstanceState != null)
			onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		super.onSaveInstanceState(savedInstanceState);
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		savedInstanceState.putSerializable("tally1", tally1);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.
		tally1 = (pigstally) savedInstanceState.getSerializable("tally1");
		setUIValues(tally1);
	}

	/**
	 * Handle the pause by ??
	 */
	public void onPause()
	{
		super.onPause();
	}

	/**
	 * Handle the resume by restoring the settings
	 */
	public void onResume()
	{
		super.onResume();
	}

	/**
	 * Handle the click for each button
	 * 
	 * @param view
	 */
	public void onClick(View view)
	{
		String msg = "default msg";
		msg = getButtonMessage(view, msg);
		String currentRoll = msg.trim();
		tally1.roll(currentRoll);
		setUIValues(tally1);
		if (tally1.isPigout() == true)
			toastMsg("Pigout!!!", 3000);
	}

	/**
	 * set the UI values on the form
	 * 
	 * @param pigstally
	 */
	private void setUIValues(pigstally tally1)
	{
		tvPig1Label.setText("Pig1: " + tally1.getFirstRoll());
		tvPig2Label.setText("Pig2: " + tally1.getSecondRoll());
		tvScoreThusFar.setText("Score thus far: " + tally1.getTempScore());
		tvScore.setText("Bank Score: " + tally1.getBankScore());
	}

	/**
	 * Set the clicked buttons message
	 * 
	 * @param view
	 * @param msg
	 * @return
	 */
	private String getButtonMessage(View view, String defaultMsg)
	{
		String returnMsg = ((Button) view).getText().toString();
		if (returnMsg.length() == 0)
			returnMsg = defaultMsg;
		return returnMsg;
	}

	/**
	 * setButtonListener sets all the buttons on the form to listen via the same handler
	 * 
	 * @param l
	 * @param id
	 */
	private void setButtonListener(TableLayout l, int id)
	{
		msg = (Button) l.findViewById(id);
		msg.setOnClickListener(this);
	}

	/**
	 * show a toast message to the user
	 * 
	 * @param msg
	 * @param toastLength
	 */
	private void toastMsg(String msg, int toastLength)
	{
		Toast.makeText(this, msg, toastLength).show();
	}

	/**
	 * set the next Piggy as the current piggy
	 */
	public void setNextPiggy()
	{

	}

	/**
	 * set the previous piggy as the current piggy
	 */
	public void setPreviousPiggy()
	{

	}

	/**
	 * allows a swipe right if the previous_id > 0
	 * 
	 * @return
	 */
	public boolean canFlipLeft()
	{
		return (previous_id > 0);
	}

	/**
	 * allows a swipe left if the next_id > 0
	 * 
	 * @return
	 */
	public boolean canFlipRight()
	{
		return (next_id > 0);
	}
}