/** Copyright 2011 Maximilian Fellner, Gernot Solic, Florian Sumann
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package at.tugraz.kmi.energy2live.database;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import at.tugraz.kmi.energy2live.model.implementation.E2LActivityImplementation;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class E2LDatabaseHelper extends OrmLiteSqliteOpenHelper {
	private static final String DATABASE_NAME = "click.db";
	private static final int DATABASE_VERSION = 6;

	private Dao<E2LActivityImplementation, Integer> activityDao;

	public E2LDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, E2LActivityImplementation.class);
		} catch (SQLException e) {
			Log.e(E2LDatabaseHelper.class.getName(), "Unable to create datbases", e);
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			TableUtils.dropTable(connectionSource, E2LActivityImplementation.class, true);
			onCreate(database, connectionSource);
		} catch (SQLException e) {
			Log.e(E2LDatabaseHelper.class.getName(), "Unable to upgrade database from version " + oldVersion
					+ " to new " + newVersion, e);
		}
	}

	public Dao<E2LActivityImplementation, Integer> getActivityDao() throws SQLException {
		if (activityDao == null) {
			activityDao = getDao(E2LActivityImplementation.class);
		}
		return activityDao;
	}
}
