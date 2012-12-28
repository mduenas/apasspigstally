package com.markduenas.android.apasspigstally.db;


import android.content.Context;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides the DAOs used by the other classes.
 * 
 */
public class ScanDBHelper extends GenericDBHelper
{

	/** The Constant DATABASE_NAME. Overrides the GenericDBHelper names */
	private static final String DATABASE_NAME = "PigsTallyDB.db";
	/** The Constant DATABASE_VERSION. Overrides the GenericDBHelper names */
	private static final int DATABASE_VERSION = 1;

	/**
	 * Instantiates a new scan series db helper.
	 * 
	 * @param context
	 *            the context
	 */
	public ScanDBHelper(Context context)
	{
		super(context, DATABASE_NAME, DATABASE_VERSION);

		initializeTableList();

	}

	public ScanDBHelper(Context context, String databaseName, int databaseVersion)
	{
		super(context, databaseName, databaseVersion);

		initializeTableList();
		createTables();

	}

	protected void initializeTableList()
	{
		// Initalize list of classes/tables to be stored in this database
		tableList.add(pigstally.class);
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close()
	{
		super.close();
	}

}
