package net.digitalfeed.pdroidalternative;

import java.io.IOException;
import java.util.LinkedList;

import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

public class DBInterface {
	private static DBHelper dbhelper = null;
	private static DBInterface dbinterface = null;
	
	public static final class ApplicationTable {
		public ApplicationTable(){}
		
		public static final int COMPRESS_ICON_QUALITY = 100;
		
		public static final String TABLE_NAME = "application";
		public static final String COLUMN_NAME_LABEL = "label";
		public static final String COLUMN_NAME_PACKAGENAME = "packageName";
		public static final String COLUMN_NAME_UID = "uid";
		public static final String COLUMN_NAME_VERSIONCODE = "versionCode";
		public static final String COLUMN_NAME_PERMISSIONS = "permissions";
		public static final String COLUMN_NAME_ICON = "icon";
		public static final String COLUMN_NAME_FLAGS = "appFlags";
		
		public static final int FLAG_IS_SYSTEM_APP = 0x1;
		public static final int FLAG_HAS_INTERNET_ACCESS = 0x2;
		
		public static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME + "(" + 
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				COLUMN_NAME_LABEL + " TEXT NOT NULL, " +
				COLUMN_NAME_PACKAGENAME + " TEXT NOT NULL, " + 
				COLUMN_NAME_UID + " INTEGER NOT NULL, " +
				COLUMN_NAME_VERSIONCODE + " INTEGER NOT NULL, " +
				COLUMN_NAME_PERMISSIONS + " TEXT, " + 
				COLUMN_NAME_ICON + " BLOB, " +
				COLUMN_NAME_FLAGS + "  INTEGER NOT NULL" + 
				");";
		
		public static final String DROP_SQL = "DROP TABLE " + TABLE_NAME + ";";
		
		public static final ContentValues getContentValues(Application application) {
			ContentValues contentValues = new ContentValues();
			contentValues.put(COLUMN_NAME_LABEL, application.getLabel());
			contentValues.put(COLUMN_NAME_PACKAGENAME, application.getPackageName());
			contentValues.put(COLUMN_NAME_UID, application.getUid());
			contentValues.put(COLUMN_NAME_VERSIONCODE, application.getVersionCode());
			contentValues.put(COLUMN_NAME_FLAGS, application.getAppFlags());
			contentValues.put(COLUMN_NAME_ICON, application.getIconByteArray());
			
			String[] permissions = application.getPermissions();
			if (permissions != null) {
				contentValues.put(COLUMN_NAME_PERMISSIONS, TextUtils.join(",", application.getPermissions()));
			}
			return contentValues;
		}
	}
	
	public static final class ApplicationStatusTable {
		public ApplicationStatusTable(){}
		public static final String TABLE_NAME = "application_status";
		public static final String COLUMN_NAME_PACKAGENAME = "packageName";
		public static final String COLUMN_NAME_FLAGS = "statusFlags";

		public static final int FLAG_IS_UNTRUSTED = 0x1;
		public static final int FLAG_NOTIFY_ON_ACCESS = 0x2;
		
		public static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME + "(" + 
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				COLUMN_NAME_PACKAGENAME + " TEXT, " + 
				COLUMN_NAME_FLAGS + " INTEGER, " + 
				"FOREIGN KEY(" + COLUMN_NAME_PACKAGENAME + ") REFERENCES " + ApplicationTable.TABLE_NAME + "(packageName)" + 
				");";
		
		public static final String DROP_SQL = "DROP TABLE " + TABLE_NAME + ";";
		
	}
	
	public static final class ApplicationLogTable {
		public ApplicationLogTable(){}
		public static final String TABLE_NAME = "application_log";
		public static final String COLUMN_NAME_DATETIME = "datetime";
		public static final String COLUMN_NAME_PACKAGENAME = "packageName";
		public static final String COLUMN_NAME_UID = "uid";
		public static final String COLUMN_NAME_VERSIONCODE = "versionCode";
		public static final String COLUMN_NAME_OPERATION = "operation";
		public static final String COLUMN_NAME_FLAGS = "logFlags";
		
		public static final int FLAGS_ALLOWED = 0x1;
		
		public static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME + "(" + 
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				COLUMN_NAME_DATETIME + " REAL, " + 
				COLUMN_NAME_PACKAGENAME + " TEXT, " + 
				COLUMN_NAME_UID + " INTEGER, " +
				COLUMN_NAME_VERSIONCODE + " INTEGER, " +
				COLUMN_NAME_OPERATION + " TEXT, " +
				COLUMN_NAME_FLAGS + " INTEGER" + 
				");";
		
		public static final String DROP_SQL = "DROP TABLE " + TABLE_NAME + ";";
		
		public static final ContentValues getContentValues(String packageName, int uid, int versionCode,
				String operation) {
			return getContentValues(packageName, uid, versionCode, operation, 0);	
		}
		
		public static final ContentValues getContentValues(String packageName, int uid, int versionCode,
				String operation, int flags) {
			ContentValues contentValues = new ContentValues();
			contentValues.put(COLUMN_NAME_PACKAGENAME, packageName);
			contentValues.put(COLUMN_NAME_UID, uid);
			contentValues.put(COLUMN_NAME_VERSIONCODE, versionCode);
			contentValues.put(COLUMN_NAME_OPERATION, operation);
			contentValues.put(COLUMN_NAME_FLAGS, flags);
			return contentValues;
		}
	}
	
	public static final class SettingTable {
		public SettingTable(){}
		public static final String TABLE_NAME = "setting";
		public static final String COLUMN_NAME_ID = "id";
		public static final String COLUMN_NAME_NAME = "name";
		public static final String COLUMN_NAME_TITLE = "title"; //Used to store the 'friendly' title of the setting, which may be language specific.
																//If we start adding support for multiple languages, possibly we should be handling this better; maybe having another table with all the language text in it?
																//The point of this is to avoid using reflection to get the titles from resources all the time
		public static final String COLUMN_NAME_GROUP = "group"; //Stored as a string, but maybe better in another table and linked?
		public static final String COLUMN_NAME_GROUP_TITLE = "groupTitle"; //As with the above 'group' column, may be better in a separate column, but then we need to be doing joins.
		public static final String COLUMN_NAME_OPTIONS = "options"; //Options are stored as a string array
		
		public static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME + "(" + 
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				COLUMN_NAME_ID + " TEXT NOT NULL, " + 
				COLUMN_NAME_NAME + " TEXT NOT NULL, " +
				COLUMN_NAME_TITLE + " TEXT, " +
				COLUMN_NAME_GROUP + " TEXT NOT NULL, " +
				COLUMN_NAME_GROUP_TITLE + " TEXT, " +
				COLUMN_NAME_OPTIONS + " TEXT NOT NULL, " +
				");";
		
		public static final String DROP_SQL = "DROP TABLE " + TABLE_NAME + ";";
		
		public static final ContentValues getContentValues(Setting setting) {
			ContentValues contentValues = new ContentValues();
			contentValues.put(COLUMN_NAME_ID, setting.getId());
			contentValues.put(COLUMN_NAME_NAME, setting.getName());
			//contentValues.put(COLUMN_NAME_TITLE, setting.getTitle());
			contentValues.put(COLUMN_NAME_GROUP, setting.getGroup());
			//contentValues.put(COLUMN_NAME_GROUP_TITLE, setting.getGroupTitle());
			contentValues.put(COLUMN_NAME_OPTIONS, TextUtils.join(",",setting.getOptions()));
			
			return contentValues;
		}
	}

	public static final class PermissionSettingTable {
		public PermissionSettingTable(){}
		public static final String TABLE_NAME = "permission_setting";
		public static final String COLUMN_NAME_PERMISSION = "permission";
		public static final String COLUMN_NAME_SETTING = "setting";
		
		public static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME + "(" + 
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				COLUMN_NAME_PERMISSION + " TEXT NOT NULL, " + 
				COLUMN_NAME_SETTING + " TEXT NOT NULL, " + 
				"FOREIGN KEY(" + COLUMN_NAME_SETTING + ") REFERENCES " + SettingTable.TABLE_NAME + "(ID)" + 
				");";
		
		public static final String DROP_SQL = "DROP TABLE " + TABLE_NAME + ";";
		
		public static final ContentValues getContentValues(String permission, String settingId) {
			ContentValues contentValues = new ContentValues();
			contentValues.put(COLUMN_NAME_PERMISSION, permission);
			contentValues.put(COLUMN_NAME_SETTING, settingId);		
			return contentValues;
		}
		
		public static final ContentValues getContentValues(String permission, Setting setting) {
			return getContentValues(permission, setting.getId());
		}

	}
	
	public static final class PermissionApplicationTable {
		public PermissionApplicationTable(){}
		public static final String TABLE_NAME = "permission_application";
		public static final String COLUMN_NAME_PERMISSION = "permission";
		public static final String COLUMN_NAME_PACKAGENAME = "packageName";
		
		public static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME + "(" + 
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				COLUMN_NAME_PERMISSION + " TEXT NOT NULL, " + 
				COLUMN_NAME_PACKAGENAME + " TEXT NOT NULL, " + 
				"FOREIGN KEY(" + COLUMN_NAME_PERMISSION + ") REFERENCES " + PermissionSettingTable.TABLE_NAME + "(" + PermissionSettingTable.COLUMN_NAME_PERMISSION + ")" + 
				"FOREIGN KEY(" + COLUMN_NAME_PACKAGENAME + ") REFERENCES " + ApplicationTable.TABLE_NAME + "(" + ApplicationTable.COLUMN_NAME_PACKAGENAME + ")" +
				");";
		
		public static final String DROP_SQL = "DROP TABLE " + TABLE_NAME + ";";
		
		public static final ContentValues getContentValues(String permission, String packageName) {
			ContentValues contentValues = new ContentValues();
			contentValues.put(COLUMN_NAME_PERMISSION, permission);
			contentValues.put(COLUMN_NAME_PACKAGENAME, packageName);		
			return contentValues;
		}
		
		public static final ContentValues getContentValues(String permission, Application application) {
			return getContentValues(permission, application.getPackageName());
		}
	}
	
	public static final String QUERY_GET_ALL_APPS_WITH_STATUS_WITHOUT_PERMISSIONS = "SELECT " + 
			ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_LABEL + ", " +  
			ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_PACKAGENAME + ", " + 
			ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_UID + ", " +
			ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_VERSIONCODE + ", " +
			ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_ICON + ", " +
			ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_FLAGS + ", " +
			ApplicationStatusTable.TABLE_NAME + "." + ApplicationStatusTable.COLUMN_NAME_FLAGS +   
			" FROM " + ApplicationTable.TABLE_NAME + 
			" LEFT OUTER JOIN " + ApplicationStatusTable.TABLE_NAME +
			" ON " + ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_PACKAGENAME + " =  " +
			ApplicationStatusTable.TABLE_NAME + "." + ApplicationStatusTable.COLUMN_NAME_PACKAGENAME;
	
	public static final String QUERY_GET_APPS_BY_NAME_WITH_STATUS_WITHOUT_PERMISSIONS = QUERY_GET_ALL_APPS_WITH_STATUS_WITHOUT_PERMISSIONS + 
			" WHERE " + ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_LABEL + " LIKE ?";
	
	public static final String QUERY_GET_APPS_BY_NAME_WITH_PERMISSIONS = "SELECT " + 
			ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_LABEL + ", " +  
			ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_PACKAGENAME + ", " + 
			ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_UID + ", " +
			ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_VERSIONCODE + ", " +
			ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_ICON + ", " +
			ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_FLAGS + ", " +
			ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_PERMISSIONS +   
			" FROM " + ApplicationTable.TABLE_NAME + 
			" WHERE " + ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_PACKAGENAME + " = ?";
	
	public static final String QUERY_GET_APPS_BY_PERMISSION_WITH_PERMISSIONS = "SELECT " + 
			ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_LABEL + ", " +  
			ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_PACKAGENAME + ", " + 
			ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_UID + ", " +
			ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_VERSIONCODE + ", " +
			ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_ICON + ", " +
			ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_FLAGS + ", " +
			ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_PERMISSIONS +   
			" FROM " + ApplicationTable.TABLE_NAME + 
			" INNER JOIN " + PermissionApplicationTable.TABLE_NAME +
			" ON " + ApplicationTable.TABLE_NAME + "." + ApplicationTable.COLUMN_NAME_PACKAGENAME + " =  " +
			PermissionApplicationTable.TABLE_NAME + "." + PermissionApplicationTable.COLUMN_NAME_PACKAGENAME + 
			" WHERE " + PermissionApplicationTable.TABLE_NAME + "." + PermissionApplicationTable.COLUMN_NAME_PERMISSION + " = ?";
	
	public static final String QUERY_DELETE_APPS_WITHOUT_STATUS = "DELETE FROM " + 
			ApplicationStatusTable.TABLE_NAME + 
			" WHERE " + ApplicationStatusTable.COLUMN_NAME_PACKAGENAME + 
			" NOT IN (" +
				" SELECT " + ApplicationTable.COLUMN_NAME_PACKAGENAME + 
				" FROM " + ApplicationTable.TABLE_NAME + 
			");";
	
	public static final String QUERY_GET_PERMISSIONS_OF_INTEREST = "SELECT DISTINCT " + 
			PermissionSettingTable.COLUMN_NAME_PERMISSION + 
			" FROM " + PermissionSettingTable.TABLE_NAME;
	
	public Context context;
	
	public static DBInterface getInstance(Context context) {
		if (dbinterface == null) {
			dbinterface = new DBInterface(context.getApplicationContext());
		}
		return dbinterface;
	}
	
	public DBHelper getDBHelper() {
		if (dbhelper == null) {
			dbhelper = new DBHelper(this.context);
		}
		
		return dbhelper;
	}
	
	public void addLogEntry(String packageName, int uid, byte accessMode, String dataType) {
		if (dbhelper == null) {
			getDBHelper();
		}
		
		ContentValues contentValues = ApplicationLogTable.getContentValues(packageName, uid, 0, dataType,
				0);
		
		SQLiteDatabase write_db = dbhelper.getWritableDatabase();
		write_db.insert(ApplicationLogTable.TABLE_NAME, null, contentValues);
		write_db.close();
	}
	
	private DBInterface(Context context) {
		this.context = context;
	}
	
	public class DBHelper extends SQLiteOpenHelper {
		public static final String DATABASE_NAME = "pdroidmgr.db";
		public static final int DATABASE_VERSION = 2;
		
		//private SQLiteDatabase db;
		
		private DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
	
		@Override
		public void onCreate(SQLiteDatabase db) {
			createTables(db);
			loadDefaultData(db);
		}
	
		
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// At version 1 - no upgrades yet!
			deleteTables(db);
			createTables(db);
			loadDefaultData(db);
		}

		public void deleteTables(SQLiteDatabase db) {
			db.execSQL(ApplicationLogTable.DROP_SQL);
			db.execSQL(ApplicationStatusTable.DROP_SQL);
			db.execSQL(ApplicationTable.DROP_SQL);
			db.execSQL(PermissionSettingTable.DROP_SQL);
			db.execSQL(SettingTable.DROP_SQL);
		}

		public void createTables(SQLiteDatabase db) {
			//this.db = db;  
			db.execSQL(ApplicationTable.CREATE_SQL);
			//db.execSQL("CREATE TABLE permission (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, label TEXT NOT NULL, description TEXT);");
			//db.execSQL("CREATE TABLE setting (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, label TEXT NOT NULL, permission_id INTEGER, options INTEGER, fake_type INTEGER, description TEXT, FOREIGN KEY(permission_id) REFERENCES permission(_id));");
			//db.execSQL("CREATE TABLE application_permission (application_id INTEGER, permission_id INTEGER, FOREIGN KEY(application_id) REFERENCES application(_id), FOREIGN KEY(permission_id) REFERENCES permission(_id));");
			db.execSQL(ApplicationStatusTable.CREATE_SQL);
			db.execSQL(ApplicationLogTable.CREATE_SQL);

			db.execSQL(SettingTable.CREATE_SQL);
			db.execSQL(PermissionSettingTable.CREATE_SQL);
		}

		
		private void loadDefaultData(SQLiteDatabase db) {
			Resources resources = context.getResources();
			String packageName = context.getPackageName();
			
			XmlResourceParser xrp = resources.getXml(R.xml.pdroid_settings);
			try {
				int eventType = xrp.next();
				while(!(eventType == XmlResourceParser.START_TAG && xrp.getName().equals("setting")) && eventType != XmlResourceParser.END_DOCUMENT) {
					eventType = xrp.next();
				}
				while (eventType == XmlResourceParser.START_TAG && xrp.getName().equals("setting")) {
		        	String name = xrp.getAttributeValue(null, "name");
		        	String id = xrp.getIdAttribute();
		        	String group = xrp.getAttributeValue(null, "group");
		        	//I wish there were a nicer way to get this string. Maybe a pair of arrays - one with identifiers, one with labels?
		        	String label = resources.getString(resources.getIdentifier("SETTING_LABEL_" + id, "string", packageName));
		        	eventType = xrp.next();
		 			while(eventType == XmlResourceParser.TEXT && xrp.isWhitespace()) {
		 				eventType = xrp.next();
		 	 		}
			        LinkedList<String> options = new LinkedList<String>();
		        	while (eventType == XmlResourceParser.START_TAG && xrp.getName().equals("option")) {
		        		options.add(xrp.getText());
			        	eventType = xrp.next();
						while(eventType == XmlResourceParser.TEXT && xrp.isWhitespace()) {
							eventType = xrp.next();
						}
				        if (eventType == XmlResourceParser.END_TAG && xrp.getName().equals("option")) {
				       	eventType = xrp.next();
							while(eventType == XmlResourceParser.TEXT && xrp.isWhitespace()) {
								eventType = xrp.next();
							}
				        } else {
				        	break;
				        }
		        	}
			        if (eventType == XmlResourceParser.END_TAG && xrp.getName().equals("setting")) {
			       	eventType = xrp.next();
						while(eventType == XmlResourceParser.TEXT && xrp.isWhitespace()) {
							eventType = xrp.next();
						}
			        } else {
			        	break;
			        }
					Log.d("PDroidAlternative","New Setting: id=" + id + " name=" + name + " group=" + group + " label=" + label + " options=" + TextUtils.join(",",options.toArray(new String[options.size()])));
			        Setting newSetting = new Setting(id, name, group, label, options.toArray(new String[options.size()]));
			        db.insert(SettingTable.TABLE_NAME, null, SettingTable.getContentValues(newSetting));
		       }
		       
				xrp = resources.getXml(R.xml.permission_setting_map);
				eventType = xrp.next();
				while(!(eventType == XmlResourceParser.START_TAG && xrp.getName().equals("permission")) && eventType != XmlResourceParser.END_DOCUMENT) {
					eventType = xrp.next();
				}
				while (eventType == XmlResourceParser.START_TAG && xrp.getName().equals("permission")) {
					String id = xrp.getIdAttribute();
					Log.d("PDroidAlternative","ID:" + id);

					eventType = xrp.next();
					while(eventType == XmlResourceParser.TEXT && xrp.isWhitespace()) {
						eventType = xrp.next();
					}
					while (eventType == XmlResourceParser.START_TAG && xrp.getName().equals("setting")) {
						String settingId = xrp.getIdAttribute();
						Log.d("PDroidAlternative","permission ID:" + id + " setting ID: " + settingId);
						db.insert(PermissionSettingTable.TABLE_NAME, null, PermissionSettingTable.getContentValues(id, settingId));
						eventType = xrp.next();
						while(eventType == XmlResourceParser.TEXT && xrp.isWhitespace()) {
							eventType = xrp.next();
						}
				        if (eventType == XmlResourceParser.END_TAG && xrp.getName().equals("setting")) {
				        	eventType = xrp.next();
							while(eventType == XmlResourceParser.TEXT && xrp.isWhitespace()) {
								eventType = xrp.next();
							}
				        } else {
				        	break;
				        }
					}
					if (eventType == XmlResourceParser.END_TAG && xrp.getName().equals("setting")) {
						eventType = xrp.next();
						while(eventType == XmlResourceParser.TEXT && xrp.isWhitespace()) {
							eventType = xrp.next();
						}
					} else {
						break;
					}
				}
			} catch (XmlPullParserException e) {
				Log.d("PDroidAlternative",e.getMessage());
				//TODO: Exception handling, mayhaps?
			} catch (IOException e) {
				Log.d("PDroidAlternative",e.getMessage());
			} catch (NotFoundException e) {
				Log.d("PDroidAlternative",e.getMessage());
			}
			/*
			 * The idea here is/was to have Settings and Permissions tables which declare the settings associated with each permission
			 * and then provide the settings (and relevant options) based on the table content. Maybe later.
			class Setting {
				public static final int OPTION_REAL = 1;
				public static final int OPTION_BLOCK = 2;
				public static final int OPTION_RANDOM = 3;
				public static final int OPTION_FAKE = 4;
				public static final int FAKE_TYPE_INTEGER = 0;
				public static final int FAKE_TYPE_STRING = 1;
				public static final int FAKE_TYPE_FLOAT = 2;
				public static final int FAKE_TYPE_COORDINATES = 3;
				public static 
				final String name;
				final String label;
				final int options;
				final String description;
				
				public Setting (String name, String label, int options, String description) {
					this.name = name;
					this.label = label;
					this.options = options;
					this.description = description;
				}
				
				public void writeToDatabase(long permission_id) {
					ContentValues values = new ContentValues();
					values.put("name", this.name);
					values.put("label", this.label);
					values.put("permission_id", permission_id);
					values.put("options", options);
					db.insert("permission", null, values);
				}
			}
			
			class Permission {
				long _id;
				final String name;
				final String label;
				final String description;
				Setting[] settings = null;
				
				public Permission (String name, String label, String description, Setting[] settings) {
					this.name = name;
					this.label = label;
					this.description = description;
					this.settings = settings;
				}
				
				public void writeToDatabase() {
					ContentValues values = new ContentValues();
					values.put("name", this.name);
					values.put("label", this.label);
					values.put("description", this.description);
					this._id = db.insert("permission", null, values);
					for (Setting setting : this.settings) {
						setting.writeToDatabase(this._id);
					}
				} 
			}
			*/
		}
	}
}