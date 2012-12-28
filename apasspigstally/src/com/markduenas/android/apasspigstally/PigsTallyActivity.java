package com.markduenas.android.apasspigstally;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.markduenas.android.PigsTallyActivity.R;

public class PigsTallyActivity extends Activity implements View.OnClickListener
{

	// The constants for the two menu items
	private static final int ADD_ID = Menu.FIRST + 2;
	private static final int DELETE_ID = Menu.FIRST + 4;
	private static final int CLOSE_ID = Menu.FIRST + 6;

	Button buttonSider;
	Button buttonSiderDot;
	Button buttonSnouter;
	Button buttonTrotter;
	Button buttonLeaningJowler;
	Button buttonRazorback;
	Button buttonTotalLoss;
	Button buttonBankIt;
	TextView tvPig1Label1;
	TextView tvPig2Label1;
	TextView tvScore1;
	TextView tvScoreThusFar1;
	TextView tvPlayerName1;
	TextView tvPig1Label2;
	TextView tvPig2Label2;
	TextView tvScore2;
	TextView tvScoreThusFar2;
	TextView tvPlayerName2;
	TextView tvPig1Label3;
	TextView tvPig2Label3;
	TextView tvScore3;
	TextView tvScoreThusFar3;
	TextView tvPlayerName3;
	Button msg;
	TableLayout tl1;
	private static List<pigstally> listPigsTally = new ArrayList<pigstally>();
	private static int current_id = -1;
	private static int next_id = -1;
	private static int previous_id = -1;

	LinearLayout pageCurrent;
	LinearLayout pagePrevious;
	LinearLayout pageNext;

	private GenericDBHelper dbHelper;

	private static GestureDetector gestureDetector = null;
	View.OnTouchListener gestureListener;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Set up the linear layout views
		pagePrevious = (LinearLayout) findViewById(R.id.llViewFlip01);
		pageCurrent = (LinearLayout) findViewById(R.id.llViewFlip02);
		pageNext = (LinearLayout) findViewById(R.id.llViewFlip03);

		// Set up the text views
		tvPig1Label1 = (TextView) pagePrevious.findViewById(R.id.tvPig1Label);
		tvPig2Label1 = (TextView) pagePrevious.findViewById(R.id.tvPig2Label);
		tvScoreThusFar1 = (TextView) pagePrevious.findViewById(R.id.tvScoreThusfarLabel);
		tvScore1 = (TextView) pagePrevious.findViewById(R.id.tvScore);
		tvPlayerName1 = (TextView) pagePrevious.findViewById(R.id.tvPlayerName);
		// set up the text views
		tvPig1Label2 = (TextView) pageCurrent.findViewById(R.id.tvPig1Label);
		tvPig2Label2 = (TextView) pageCurrent.findViewById(R.id.tvPig2Label);
		tvScoreThusFar2 = (TextView) pageCurrent.findViewById(R.id.tvScoreThusfarLabel);
		tvScore2 = (TextView) pageCurrent.findViewById(R.id.tvScore);
		tvPlayerName2 = (TextView) pageCurrent.findViewById(R.id.tvPlayerName);
		// set up the text views
		tvPig1Label3 = (TextView) pageNext.findViewById(R.id.tvPig1Label);
		tvPig2Label3 = (TextView) pageNext.findViewById(R.id.tvPig2Label);
		tvScoreThusFar3 = (TextView) pageNext.findViewById(R.id.tvScoreThusfarLabel);
		tvScore3 = (TextView) pageNext.findViewById(R.id.tvScore);
		tvPlayerName3 = (TextView) pageNext.findViewById(R.id.tvPlayerName);

		// Set up the buttons
		setButtonListener(pagePrevious, R.id.buttonSider);
		setButtonListener(pagePrevious, R.id.buttonSiderDot);
		setButtonListener(pagePrevious, R.id.buttonSnouter);
		setButtonListener(pagePrevious, R.id.buttonRazorback);
		setButtonListener(pagePrevious, R.id.buttonLeaningJowler);
		setButtonListener(pagePrevious, R.id.buttonTrotter);
		setButtonListener(pagePrevious, R.id.buttonBankIt);
		setButtonListener(pagePrevious, R.id.buttonTotalLoss);
		// Set up the buttons
		setButtonListener(pageCurrent, R.id.buttonSider);
		setButtonListener(pageCurrent, R.id.buttonSiderDot);
		setButtonListener(pageCurrent, R.id.buttonSnouter);
		setButtonListener(pageCurrent, R.id.buttonRazorback);
		setButtonListener(pageCurrent, R.id.buttonLeaningJowler);
		setButtonListener(pageCurrent, R.id.buttonTrotter);
		setButtonListener(pageCurrent, R.id.buttonBankIt);
		setButtonListener(pageCurrent, R.id.buttonTotalLoss);
		// Set up the buttons
		setButtonListener(pageNext, R.id.buttonSider);
		setButtonListener(pageNext, R.id.buttonSiderDot);
		setButtonListener(pageNext, R.id.buttonSnouter);
		setButtonListener(pageNext, R.id.buttonRazorback);
		setButtonListener(pageNext, R.id.buttonLeaningJowler);
		setButtonListener(pageNext, R.id.buttonTrotter);
		setButtonListener(pageNext, R.id.buttonBankIt);
		setButtonListener(pageNext, R.id.buttonTotalLoss);

		// now listen for gestures
		ViewFlipper viewFlip = (ViewFlipper) findViewById(R.id.viewFlipper);
		gestureDetector = new GestureDetector(new MyGestureDetector(this, viewFlip));
		gestureListener = new View.OnTouchListener()
		{
			public boolean onTouch(View v, MotionEvent event)
			{
				if (gestureDetector.onTouchEvent(event))
				{
					return true;
				}
				return false;
			}
		};

		if (savedInstanceState != null)
			onRestoreInstanceState(savedInstanceState);

		dbHelper = new ScanDBHelper(getBaseContext());

		refreshViews();
	}

	private void refreshViews()
	{
		listPigsTally = getPlayers();
		current_id = 0;
		setUIValues(listPigsTally.get(0), pageCurrent);
		if (listPigsTally.size() > 1)
		{
			setUIValues(listPigsTally.get(1), pageNext);
		}
	}

	private void refreshViewsNewPlayer()
	{
		listPigsTally = getPlayers();
		current_id = listPigsTally.size() - 1;
		setUIValues(listPigsTally.get(current_id), pageCurrent);
		if (listPigsTally.size() > 1)
		{
			previous_id = current_id - 1;
			setUIValues(listPigsTally.get(previous_id), pagePrevious);
			if (previous_id != 0)
			{
				next_id = 0;
				setUIValues(listPigsTally.get(next_id), pageNext);
			}
		}
	}

	private List<pigstally> getPlayers()
	{
		List<pigstally> list = dbHelper.getDatabaseList(pigstally.class);
		if (list == null)
		{
			list = new ArrayList<pigstally>();
			list.add(new pigstally("Player1"));
			dbHelper.create(pigstally.class, list.get(0));
		}
		return list;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (gestureDetector.onTouchEvent(event))
			return true;
		else
			return false;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		super.onSaveInstanceState(savedInstanceState);
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		if (previous_id > -1)
			savedInstanceState.putSerializable("tallyPrevious", listPigsTally.get(previous_id));
		if (current_id > -1)
			savedInstanceState.putSerializable("tallyCurrent", listPigsTally.get(current_id));
		if (next_id > -1)
			savedInstanceState.putSerializable("tallyNext", listPigsTally.get(next_id));
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.
		pigstally tallyPrevious = (pigstally) savedInstanceState.getSerializable("tallyPrevious");
		pigstally tallyCurrent = (pigstally) savedInstanceState.getSerializable("tallyCurrent");
		pigstally tallyNext = (pigstally) savedInstanceState.getSerializable("tallyNext");
		if (tallyPrevious != null)
			setUIValues(tallyPrevious, pagePrevious);
		if (tallyCurrent != null)
			setUIValues(tallyCurrent, pageCurrent);
		if (tallyNext != null)
			setUIValues(tallyNext, pageCurrent);
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
		pigstally tally1 = listPigsTally.get(current_id);
		String msg = "default msg";
		msg = getButtonMessage(view, msg);
		String currentRoll = msg.trim();
		tally1.roll(currentRoll);
		setUIValues(tally1, pageCurrent);
		if (tally1.isPigout() == true)
			toastMsg("Pigout!!!", 3000);
	}

	/**
	 * set the UI values on the form
	 * 
	 * @param pigstally
	 */
	private void setUIValues(pigstally tally1, LinearLayout llCurrent)
	{
		TextView tvPig1Label = (TextView) llCurrent.findViewById(R.id.tvPig1Label);
		TextView tvPig2Label = (TextView) llCurrent.findViewById(R.id.tvPig2Label);
		TextView tvScoreThusFar = (TextView) llCurrent.findViewById(R.id.tvScoreThusfarLabel);
		TextView tvScore = (TextView) llCurrent.findViewById(R.id.tvScore);
		TextView tvPlayerName = (TextView) llCurrent.findViewById(R.id.tvPlayerName);
		tvPig1Label.setText("Pig1: " + tally1.getFirstRoll());
		tvPig2Label.setText("Pig2: " + tally1.getSecondRoll());
		tvScoreThusFar.setText("Score thus far: " + tally1.getTempScore());
		tvScore.setText("Bank Score: " + tally1.getBankScore());
		tvPlayerName.setText(tally1.getPlayerName());
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
	private void setButtonListener(LinearLayout l, int id)
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
		Log.v("setNextPiggy", String.format("setNextPiggy before current_id is: %d", current_id));
		if (listPigsTally.size() == 1)
		{
			current_id = 0;
			next_id = -1;
			previous_id = -1;
			return;
		}
		previous_id++;
		current_id++;
		next_id++;

		if (next_id > listPigsTally.size() - 1)
			next_id = 0;
		if (current_id > listPigsTally.size() - 1)
			current_id = 0;
		if (previous_id > listPigsTally.size() - 1)
			previous_id = 0;
		Log.v("setNextPiggy", String.format("setNextPiggy after current_id is: %d", current_id));
	}

	/**
	 * set the previous piggy as the current piggy
	 */
	public void setPreviousPiggy()
	{
		Log.v("setPreviousPiggy", String.format("setPreviousPiggy before current_id is: %d", current_id));
		current_id--;
		next_id--;
		previous_id--;
		if (previous_id < 0)
			previous_id = listPigsTally.size() - 1;
		if (current_id < 0)
			current_id = listPigsTally.size() - 1;
		if (next_id < 0)
			next_id = listPigsTally.size() - 1;
		Log.v("setPreviousPiggy", String.format("setPreviousPiggy after current_id is: %d", current_id));
	}

	/**
	 * allows a swipe right if the previous_id > 0
	 * 
	 * @return
	 */
	public boolean canFlipLeft()
	{
		return (previous_id > -1);
	}

	/**
	 * allows a swipe left if the next_id > 0
	 * 
	 * @return
	 */
	public boolean canFlipRight()
	{
		return (next_id > -1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(Menu.NONE, ADD_ID, Menu.NONE, "Add Player").setIcon(R.drawable.add).setAlphabeticShortcut('a');
		menu.add(Menu.NONE, DELETE_ID, Menu.NONE, "Remove Player").setIcon(R.drawable.delete).setAlphabeticShortcut('e');
		menu.add(Menu.NONE, CLOSE_ID, Menu.NONE, "Close").setIcon(R.drawable.eject).setAlphabeticShortcut('c');

		return (super.onCreateOptionsMenu(menu));
	}

	/**
	 * check which menu item was selected
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case ADD_ID:
			try
			{
				addPiggyPlayer();
			}
			catch (Exception e)
			{
				CommonUtils.logStackTrace(e);
			}
			return (true);
		case DELETE_ID:
			try
			{
				deletePiggyPlayer(current_id);
			}
			catch (Exception e)
			{
				CommonUtils.logStackTrace(e);
			}
			return (true);
		case CLOSE_ID:
			finish();
			return (true);
		}

		return (super.onOptionsItemSelected(item));
	}

	private void deletePiggyPlayer(int current)
	{
		dbHelper.delete(pigstally.class, listPigsTally.get(current));
	}

	private void addPiggyPlayer()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("New Player");

		// Set up the input
		final EditText input = new EditText(this);
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				String playerName = input.getText().toString();
				if (playerName != null && playerName.length() > 0)
					dbHelper.create(pigstally.class, new pigstally(playerName));
				else
					dbHelper.create(pigstally.class, new pigstally(String.format("Player%d", current_id + 1)));
				refreshViewsNewPlayer();
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
			}
		});

		builder.show();
	}
}