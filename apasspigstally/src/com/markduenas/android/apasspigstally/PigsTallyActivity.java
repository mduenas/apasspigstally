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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.markduenas.android.PigsTallyActivity.R;
import com.markduenas.android.apasspigstally.db.CommonUtils;
import com.markduenas.android.apasspigstally.db.GenericDBHelper;
import com.markduenas.android.apasspigstally.db.pigstally;

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
	private static List<pigstally> listPigsTally = new ArrayList<pigstally>();

	private static int index_1 = -1;
	private static int index_2 = -1;
	private static int index_3 = -1;

	LinearLayout viewOne;
	LinearLayout viewTwo;
	LinearLayout viewThree;
	ViewFlipper viewFlipper;

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
		viewOne = (LinearLayout) findViewById(R.id.llViewFlip01);
		viewTwo = (LinearLayout) findViewById(R.id.llViewFlip02);
		viewThree = (LinearLayout) findViewById(R.id.llViewFlip03);

		// Set up the text views
		tvPig1Label1 = (TextView) viewTwo.findViewById(R.id.tvPig1Label);
		tvPig2Label1 = (TextView) viewTwo.findViewById(R.id.tvPig2Label);
		tvScoreThusFar1 = (TextView) viewTwo.findViewById(R.id.tvScoreThusfarLabel);
		tvScore1 = (TextView) viewTwo.findViewById(R.id.tvScore);
		tvPlayerName1 = (TextView) viewTwo.findViewById(R.id.tvPlayerName);
		// set up the text views
		tvPig1Label2 = (TextView) viewOne.findViewById(R.id.tvPig1Label);
		tvPig2Label2 = (TextView) viewOne.findViewById(R.id.tvPig2Label);
		tvScoreThusFar2 = (TextView) viewOne.findViewById(R.id.tvScoreThusfarLabel);
		tvScore2 = (TextView) viewOne.findViewById(R.id.tvScore);
		tvPlayerName2 = (TextView) viewOne.findViewById(R.id.tvPlayerName);
		// set up the text views
		tvPig1Label3 = (TextView) viewThree.findViewById(R.id.tvPig1Label);
		tvPig2Label3 = (TextView) viewThree.findViewById(R.id.tvPig2Label);
		tvScoreThusFar3 = (TextView) viewThree.findViewById(R.id.tvScoreThusfarLabel);
		tvScore3 = (TextView) viewThree.findViewById(R.id.tvScore);
		tvPlayerName3 = (TextView) viewThree.findViewById(R.id.tvPlayerName);

		// Set up the buttons
		setButtonListener(viewTwo, R.id.buttonSider);
		setButtonListener(viewTwo, R.id.buttonSiderDot);
		setButtonListener(viewTwo, R.id.buttonSnouter);
		setButtonListener(viewTwo, R.id.buttonRazorback);
		setButtonListener(viewTwo, R.id.buttonLeaningJowler);
		setButtonListener(viewTwo, R.id.buttonTrotter);
		setButtonListener(viewTwo, R.id.buttonBankIt);
		setButtonListener(viewTwo, R.id.buttonTotalLoss);
		// Set up the buttons
		setButtonListener(viewOne, R.id.buttonSider);
		setButtonListener(viewOne, R.id.buttonSiderDot);
		setButtonListener(viewOne, R.id.buttonSnouter);
		setButtonListener(viewOne, R.id.buttonRazorback);
		setButtonListener(viewOne, R.id.buttonLeaningJowler);
		setButtonListener(viewOne, R.id.buttonTrotter);
		setButtonListener(viewOne, R.id.buttonBankIt);
		setButtonListener(viewOne, R.id.buttonTotalLoss);
		// Set up the buttons
		setButtonListener(viewThree, R.id.buttonSider);
		setButtonListener(viewThree, R.id.buttonSiderDot);
		setButtonListener(viewThree, R.id.buttonSnouter);
		setButtonListener(viewThree, R.id.buttonRazorback);
		setButtonListener(viewThree, R.id.buttonLeaningJowler);
		setButtonListener(viewThree, R.id.buttonTrotter);
		setButtonListener(viewThree, R.id.buttonBankIt);
		setButtonListener(viewThree, R.id.buttonTotalLoss);

		// now listen for gestures
		viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
		gestureDetector = new GestureDetector(new MyGestureDetector(this, viewFlipper));
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

		dbHelper = GenericDBHelper.createInstance(getBaseContext(), "PigsTally", 1);

		refreshViews();
	}

	private void refreshViews()
	{
		listPigsTally = getPlayers();
		if (listPigsTally.size() > 0)
		{
			index_1 = 0;
			if (listPigsTally.size() == 1)
			{
				index_2 = -1;
				index_3 = -1;
			}
			else if (listPigsTally.size() == 2)
			{
				index_2 = 1;
				index_3 = 0;
			}
			else if (listPigsTally.size() > 2)
			{
				index_2 = 1;
				index_3 = 2;
			}
		}
		else
		{
			pigstally newTally = new pigstally("Player1");
			dbHelper.create(pigstally.class, newTally);
			index_1 = 0;
			listPigsTally = getPlayers();
		}
		loadAllViews();
		Log.v("refreshViews", String.format("child %d", viewFlipper.getDisplayedChild()));
	}

	private void refreshViewsNewPlayer()
	{
		listPigsTally = getPlayers();
		int viewIndex = viewFlipper.getDisplayedChild();
		if (viewIndex == 0)
		{
			index_1 = listPigsTally.size() - 1;
			index_2 = 0;
			index_3 = 1;
		}
		if (viewIndex == 1)
		{
			index_2 = listPigsTally.size() - 1;
			index_1 = index_2 - 1;
			index_3 = 0;
		}
		if (viewIndex == 2)
		{
			index_3 = listPigsTally.size() - 1;
			index_1 = 0;
			index_2 = 1;
		}
		loadAllViews();
	}

	private List<pigstally> getPlayers()
	{
		List<pigstally> list = dbHelper.getDatabaseList(pigstally.class);
		if (list == null)
		{
			list = new ArrayList<pigstally>();
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
		if (index_1 > -1)
			savedInstanceState.putSerializable("tallyPrevious", listPigsTally.get(index_1));
		if (index_2 > -1)
			savedInstanceState.putSerializable("tallyCurrent", listPigsTally.get(index_2));
		if (index_3 > -1)
			savedInstanceState.putSerializable("tallyNext", listPigsTally.get(index_3));
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
			setUIValues(tallyPrevious, viewOne);
		if (tallyCurrent != null)
			setUIValues(tallyCurrent, viewTwo);
		if (tallyNext != null)
			setUIValues(tallyNext, viewThree);
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
		View updateView = viewFlipper.getCurrentView();
		int viewIndex = viewFlipper.getDisplayedChild();
		int index = getIndexValue(viewIndex);
		pigstally tally1 = listPigsTally.get(index);
		String msg = "default msg";
		msg = getButtonMessage(view, msg);
		String currentRoll = msg.trim();
		tally1.roll(currentRoll);
		dbHelper.updateSingleDatabaseRow(pigstally.class, tally1);

		// update the UI
		setUIValues(tally1, (LinearLayout) updateView);
		// check for pig out
		if (tally1.isPigout() == true)
		{
			toastMsg("Pigout!!!", 3000);
			animateNextPlayer();
		}

		if (msg.equals("Bank It") && listPigsTally.size() > 1)
		{
			animateNextPlayer();
		}

		if (msg.equals("Total Loss (Pigs touching)"))
		{
			animateNextPlayer();
		}
	}

	private void animateNextPlayer()
	{
		Animation slideLeftIn = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_left_in);
		Animation slideLeftOut = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_left_out);
		viewFlipper.setInAnimation(slideLeftIn);
		viewFlipper.setOutAnimation(slideLeftOut);
		viewFlipper.showNext();
		setNextPiggy();
	}

	private int getIndexValue(int viewIndex)
	{
		int index = 0;
		switch (viewIndex)
		{
		case 0:
			index = index_1;
			break;
		case 1:
			index = index_2;
			break;
		case 2:
			index = index_3;
			break;
		}
		return index;
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
		tvPlayerName.setText("Player: " + tally1.getPlayerName());
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
		Log.v("setNextPiggy", String.format("setNextPiggy before: %d %d %d", index_1, index_2, index_3));
		if (listPigsTally.size() == 1)
		{
			index_2 = 0;
			index_3 = -1;
			index_1 = -1;
			return;
		}
		if (listPigsTally.size() == 2)
		{
			// on the last view invert the values
			if (viewFlipper.getDisplayedChild() == 0)
			{
				if (index_1 == 0)
					index_1 = 1;
				else
					index_1 = 0;

				if (index_2 == 0)
					index_2 = 1;
				else
					index_2 = 0;

				if (index_3 == 0)
					index_3 = 1;
				else
					index_3 = 0;
			}
			loadAllViews();
			return;
		}
		index_1++;
		index_2++;
		index_3++;

		if (index_3 > listPigsTally.size() - 1)
			index_3 = 0;
		if (index_2 > listPigsTally.size() - 1)
			index_2 = 0;
		if (index_1 > listPigsTally.size() - 1)
			index_1 = 0;
		Log.v("setNextPiggy", String.format("setNextPiggy after: %d %d %d", index_1, index_2, index_3));
		loadAllViews();
	}

	private void loadAllViews()
	{
		if (index_1 > -1)
			setUIValues(listPigsTally.get(index_1), viewOne);

		if (index_2 > -1)
			setUIValues(listPigsTally.get(index_2), viewTwo);

		if (index_3 > -1)
			setUIValues(listPigsTally.get(index_3), viewThree);
	}

	/**
	 * set the previous piggy as the current piggy
	 */
	public void setPreviousPiggy()
	{
		Log.v("setPreviousPiggy", String.format("setPreviousPiggy before: %d %d %d", index_1, index_2, index_3));
		if (listPigsTally.size() == 1)
		{
			index_1 = 0;
			index_2 = -1;
			index_3 = -1;
			return;
		}
		if (listPigsTally.size() == 2)
		{
			int index = viewFlipper.getDisplayedChild();
			Log.v("setPreviousPiggy", String.format("child %d", index));
			if (index == 2)
			{
				if (index_1 == 0)
					index_1 = 1;
				else
					index_1 = 0;

				if (index_2 == 0)
					index_2 = 1;
				else
					index_2 = 0;

				if (index_3 == 0)
					index_3 = 1;
				else
					index_3 = 0;
			}
			Log.v("setPreviousPiggy", String.format("setPreviousPiggy after: %d %d %d", index_1, index_2, index_3));
			loadAllViews();
			return;
		}
		index_1--;
		index_2--;
		index_3--;
		if (index_1 < 0)
			index_1 = listPigsTally.size() - 1;
		if (index_2 < 0)
			index_2 = listPigsTally.size() - 1;
		if (index_3 < 0)
			index_3 = listPigsTally.size() - 1;
		Log.v("setPreviousPiggy", String.format("setPreviousPiggy after: %d %d %d", index_1, index_2, index_3));
		loadAllViews();
	}

	/**
	 * allows a swipe right if the previous_id > 0
	 * 
	 * @return
	 */
	public boolean canFlipLeft()
	{
		return listPigsTally.size() > 1;
	}

	/**
	 * allows a swipe left if the next_id > 0
	 * 
	 * @return
	 */
	public boolean canFlipRight()
	{
		return listPigsTally.size() > 1;
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
				deletePiggyPlayer(viewFlipper.getDisplayedChild());
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

	private void deletePiggyPlayer(int viewIndex)
	{
		int index = getIndexValue(viewIndex);
		dbHelper.delete(pigstally.class, listPigsTally.get(index));
		refreshViews();
	}

	private void addPiggyPlayer()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("New Player");

		// Set up the input
		final EditText input = new EditText(this);
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_TEXT);
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
					dbHelper.create(pigstally.class, new pigstally(String.format("Player%d", index_2 + 1)));
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