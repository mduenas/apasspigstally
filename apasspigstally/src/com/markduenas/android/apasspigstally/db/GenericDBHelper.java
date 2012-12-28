package com.markduenas.android.apasspigstally.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides the DAOs used by the other classes.
 * 
 */
public class GenericDBHelper extends OrmLiteSqliteOpenHelper
{

	/** The log tag. */
	private final String LOG_TAG = getClass().getSimpleName();
	/** The Constant DATABASE_NAME. */
	private static final String DATABASE_NAME = "PigsTallyDB.db";
	/** The Constant DATABASE_VERSION. */
	private static final int DATABASE_VERSION = 1;
	/** The Constant DB_APPID. */
	public static final String DB_APPID = "appid";
	/** The Constant DB_APPNAME. */
	public static final String DB_APPNAME = "appname";
	/** The Constant DB_FOREIGN_APPID. */
	public static final String DB_FOREIGN_APPID = "applicationid";

	// list of tables that are managed by this helper
	protected List<Class<?>> tableList = new ArrayList<Class<?>>();

	public static GenericDBHelper createInstance(Context context, String applicationType, int databaseVersion)
	{
		return new ScanDBHelper(context, createDatabaseName(applicationType), databaseVersion);
	}

	// centralized location for databasename
	public static String createDatabaseName(String appName)
	{
		return String.format("%sDB.db", appName);
	}

	/**
	 * Instantiates a new scan series db helper.
	 * 
	 * @param context
	 *            the context
	 */
	public GenericDBHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		// Initalize list of classes/tables to be stored in this database in the subclass

	}

	/**
	 * Instantiates a new scan series db helper.
	 * 
	 * @param context
	 *            the context
	 * @param databaseName
	 *            the name of the database to access
	 * @param databaseVersion
	 *            the version of the database to access
	 * 
	 */
	public GenericDBHelper(Context context, String databaseName, int databaseVersion)
	{
		super(context, databaseName, null, databaseVersion);

		// Initialize list of classes/tables to be stored in this database in the subclass
	}

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create the tables that will store your data.
	 * 
	 * @param db
	 *            the db
	 * @param connectionSource
	 *            the connection source
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource)
	{
		try
		{
			Log.i(LOG_TAG, "onCreate");
			dropTables(connectionSource);
			for (Class s : tableList)
			{
				TableUtils.createTable(connectionSource, s);
			}
		}
		catch (SQLException e)
		{
			Log.e(LOG_TAG, "Can't create database", e);
			throw new RuntimeException(e);
		}

	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust the various data to match the new version number.
	 * 
	 * @param db
	 *            the db
	 * @param connectionSource
	 *            the connection source
	 * @param oldVersion
	 *            the old version
	 * @param newVersion
	 *            the new version
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion)
	{
		Log.i(LOG_TAG, "onUpgrade");
		GenericDBUpgradeHelper upgrader = new GenericDBUpgradeHelper(this);
		// dropTables(connectionSource);
		// after we drop the old databases, we create the new ones
		// onCreate(db, connectionSource);
		int curVer = oldVersion;
		while (curVer < newVersion)
		{
			curVer++;
			switch (curVer)
			{
			case 2:
			{
				// Upgrade from V1 to V2
				upgrader.upgradeFromVersion1to2();
				break;
			}
			case 3:
			{
				// Upgrade from V2 to V3
				break;
			}
			case 4:
			{
				// Upgrade from V3 to V4
				break;
			}
			}
		}
	}

	public void createTables()
	{
		try
		{
			createTables(getConnectionSource());
		}
		catch (SQLException e)
		{
			CommonUtils.logStackTrace(e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createTables(ConnectionSource connectionSource) throws SQLException
	{
		for (Class s : tableList)
		{
			if (!tableExists(s))
				TableUtils.createTable(connectionSource, s);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void dropTables(ConnectionSource connectionSource) throws SQLException
	{
		for (Class s : tableList)
		{
			// boolean at the end will ignore errors
			TableUtils.dropTable(connectionSource, s, true);
		}
	}

	public SQLiteDatabase getSQLiteDatabase()
	{
		return this.getWritableDatabase();
	}

	public <T> boolean dropTable(Class<T> typeClass)
	{
		try
		{
			// boolean at the end will ignore errors
			TableUtils.dropTable(connectionSource, typeClass, true);
		}
		catch (SQLException e)
		{
			CommonUtils.logStackTrace(e);
			return false;
		}
		return true;
	}

	public <T> Dao<T, Integer> getObjectDao(Class<T> typeClass) throws SQLException
	{
		return getDao(typeClass);
	}

	public <T> List<T> getDatabaseList(Class<T> annotationType)
	{
		List<T> data = null;
		try
		{
			Dao<T, Integer> ssDao = getObjectDao(annotationType);
			data = ssDao.queryForAll();
		}
		catch (SQLException e)
		{
			CommonUtils.logStackTrace(e);
			checkMissingTable(annotationType, e);
		}

		return data;
	}

	public <T> List<T> getDatabaseListNoAppId(Class<T> annotationType)
	{
		List<T> data = null;
		try
		{
			Dao<T, Integer> ssDao = getObjectDao(annotationType);
			data = ssDao.queryForAll();
		}
		catch (SQLException e)
		{
			CommonUtils.logStackTrace(e);
			// checkMissingTable(annotationType, e);
		}

		return data;
	}

	public <T> List<T> getDatabaseListFiltered(Class<T> annotationType, String columnName, String value)
	{
		List<T> data = null;
		try
		{
			Dao<T, Integer> ssDao = getObjectDao(annotationType);
			QueryBuilder<T, Integer> queryBuilder = ssDao.queryBuilder();
			Where<T, Integer> where = queryBuilder.where();
			where.eq(columnName, value);
			PreparedQuery<T> preparedQuery = queryBuilder.prepare();
			data = ssDao.query(preparedQuery);
		}
		catch (SQLException e)
		{
			CommonUtils.logStackTrace(e);
			// checkMissingTable(annotationType, e);
		}

		return data;
	}

	public <T> List<T> getDatabaseListMultiFiltered(int appId, Class<T> annotationType, HashMap<String, Object> filterList)
	{
		if (filterList != null)
			filterList.put(DB_FOREIGN_APPID, appId);
		return getDatabaseListMultiFilteredAnd(annotationType, filterList);
	}

	public <T> List<T> getDatabaseListMultiFilteredAnd(Class<T> annotationType, HashMap<String, Object> filterList)
	{
		List<T> data = null;
		try
		{
			Dao<T, Integer> ssDao = getObjectDao(annotationType);
			QueryBuilder<T, Integer> queryBuilder = ssDao.queryBuilder();
			Where<T, Integer> where = queryBuilder.where();
			boolean first = true;
			for (Map.Entry<String, Object> entry : filterList.entrySet())
			{
				if (!first)
				{
					where.and();
				}
				else
				{
					first = false;
				}
				where.eq(entry.getKey(), entry.getValue());
			}
			PreparedQuery<T> preparedQuery = queryBuilder.prepare();
			data = ssDao.query(preparedQuery);
		}
		catch (SQLException e)
		{
			CommonUtils.logStackTrace(e);
			// checkMissingTable(annotationType, e);
		}

		return data;
	}

	public <T> List<T> getDatabaseListMultiFilteredOR(Class<T> annotationType, HashMap<String, Object> filterList)
	{
		List<T> data = null;
		try
		{
			Dao<T, Integer> ssDao = getObjectDao(annotationType);
			QueryBuilder<T, Integer> queryBuilder = ssDao.queryBuilder();
			Where<T, Integer> where = queryBuilder.where();
			boolean first = true;
			for (Map.Entry<String, Object> entry : filterList.entrySet())
			{
				if (!first)
				{
					where.or();
				}
				else
				{
					first = false;
				}
				where.eq(entry.getKey(), entry.getValue());
			}
			PreparedQuery<T> preparedQuery = queryBuilder.prepare();
			data = ssDao.query(preparedQuery);
		}
		catch (SQLException e)
		{
			CommonUtils.logStackTrace(e);
			// checkMissingTable(annotationType, e);
		}

		return data;
	}

	protected <T> void checkMissingTable(Class<T> annotationType, SQLException e)
	{
		if (e.getCause() != null)
		{
			if (e.getCause().getMessage() != null && e.getCause().getMessage().contains("no such table"))
			{
				createTable(annotationType);
			}
		}
	}

	public <T> List<T> getDatabaseListGroupBy(int appId, Class<T> annotationType, String columnName)
	{
		List<T> data = null;
		try
		{
			Dao<T, Integer> ssDao = getObjectDao(annotationType);
			QueryBuilder<T, Integer> queryBuilder = ssDao.queryBuilder();
			Where<T, Integer> where = queryBuilder.where();
			where.eq(DB_FOREIGN_APPID, appId);
			queryBuilder.groupBy(columnName);
			PreparedQuery<T> preparedQuery = queryBuilder.prepare();
			data = ssDao.query(preparedQuery);
		}
		catch (SQLException e)
		{
			CommonUtils.logStackTrace(e);
			// checkMissingTable(annotationType, e);
		}

		return data;
	}

	public <T> T getDatabaseObjectMaxBy(int appId, Class<T> annotationType, String columnName)
	{
		List<T> data = null;
		try
		{
			Dao<T, Integer> ssDao = getObjectDao(annotationType);
			QueryBuilder<T, Integer> queryBuilder = ssDao.queryBuilder();
			Where<T, Integer> where = queryBuilder.where();
			where.eq(DB_FOREIGN_APPID, appId);
			queryBuilder.orderBy(columnName, false);
			PreparedQuery<T> preparedQuery = queryBuilder.prepare();
			data = ssDao.query(preparedQuery);
		}
		catch (SQLException e)
		{
			CommonUtils.logStackTrace(e);
			// checkMissingTable(annotationType, e);
		}

		if (data != null && data.get(0) != null)
			return data.get(0);
		else
			return null;
	}

	public <T> T insertSingleDatabaseRow(Class<T> annotationType, T updateItem)
	{
		Dao<T, Integer> ssDao = null;
		try
		{

			ssDao = getObjectDao(annotationType);
			if (!ssDao.isTableExists())
				createTable(annotationType);

			// this is a helper for saving the applicationId
			// create the row
			ssDao.create(updateItem);
			return updateItem;
		}
		catch (SQLException e)
		{
			CommonUtils.logStackTrace(e);
		}

		return null;
	}

	public <T> boolean updateSingleDatabaseRow(Class<T> annotationType, T updateItem)
	{
		try
		{
			Dao<T, Integer> ssDao = getObjectDao(annotationType);
			ssDao.update(updateItem);
		}
		catch (SQLException e)
		{
			CommonUtils.logStackTrace(e);
			return false;
		}

		return true;
	}

	public <T> boolean insertListDatabaseRow(Class<T> annotationType, List<T> listUpdateItem)
	{
		Dao<T, Integer> ssDao = null;
		try
		{

			ssDao = getObjectDao(annotationType);
			if (!ssDao.isTableExists())
				createTable(annotationType);
			for (T updateItem : listUpdateItem)
			{
				// create the row
				ssDao.create(updateItem);
			}
		}
		catch (SQLException e)
		{
			CommonUtils.logStackTrace(e);
		}

		return true;
	}

	public <T> GenericRawResults<String[]> queryRaw(Class<T> annotationType, String query) throws SQLException
	{
		Dao<T, Integer> dao = getDao(annotationType);
		return dao.queryRaw(query);
	}

	public <T> boolean tableExists(Class<T> annotationType)
	{
		Dao<T, Integer> ssDao = null;
		try
		{

			ssDao = getObjectDao(annotationType);
			return ssDao.isTableExists();
		}
		catch (SQLException e)
		{
			CommonUtils.logStackTrace(e);
		}

		return false;
	}

	public <T> boolean insertBatch(final Class<T> annotationType, final List<T> list)
	{
		try
		{
			TransactionManager.callInTransaction(getConnectionSource(), new Callable<Void>()
			{
				public Void call() throws Exception
				{
					Dao<T, Integer> feedItemDao = getDao(annotationType);
					for (T item : list)
					{
						feedItemDao.create(item);
					}
					return null;
				}
			});
			return true;
		}
		catch (Exception e)
		{
			Log.e("insertBatch", e.getMessage());
			CommonUtils.logStackTrace(e);
		}
		finally
		{
		}
		return false;
	}

	public int createTable(Class<?> tableClass)
	{
		try
		{
			return TableUtils.createTable(connectionSource, tableClass);
		}
		catch (SQLException e)
		{
			CommonUtils.logStackTrace(e);
		}
		return 0;
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close()
	{
		super.close();
	}

	public <T> void delete(Class<T> annotationType, T tally)
	{
		try
		{
			Dao<T, Integer> dao = getDao(annotationType);
			dao.delete(tally);
		}
		catch (SQLException e)
		{
			CommonUtils.logStackTrace(e);
		}

	}

	public <T> void create(Class<pigstally> annotationType, T tally)
	{
		try
		{
			Dao<T, Integer> dao = getDao(annotationType);
			dao.create(tally);
		}
		catch (SQLException e)
		{
			CommonUtils.logStackTrace(e);
		}
	}
}
