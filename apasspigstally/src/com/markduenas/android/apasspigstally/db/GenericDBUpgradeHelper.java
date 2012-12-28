package com.markduenas.android.apasspigstally.db;

import java.sql.SQLException;

public class GenericDBUpgradeHelper
{
	GenericDBHelper dbHelper;

	public GenericDBUpgradeHelper(GenericDBHelper genericDBHelper)
	{
		this.dbHelper = genericDBHelper;
	}

	// add upgrade methods here
	public boolean upgradeFromVersion1to2()
	{
		try
		{
			// On each version upgrade
			// Track changes and add them here as either a single sql statement or multiple
			//
			// dbHelper.queryRaw(queryraw.class, "ALTER TABLE BuildingIDData ADD COLUMN realinspectionid INT;");
			// dbHelper.queryRaw(queryraw.class, "ALTER TABLE BuildingIDData ADD COLUMN inspectionnote VARCHAR;");
			dbHelper.queryRaw(pigstally.class, "select * from pigstally;");
		}
		catch (SQLException e)
		{
			CommonUtils.logStackTrace(e);
		}

		return true;
	}

}
