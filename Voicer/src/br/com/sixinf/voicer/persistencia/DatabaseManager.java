/**
 * 
 */
package br.com.sixinf.voicer.persistencia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author maicon
 *
 */
public class DatabaseManager extends SQLiteOpenHelper {
	
	public static final String TABLE_NAME = "config";
	
	private static final String SCRIPT_CREATE_DB = 
			"CREATE TABLE " + TABLE_NAME + " (" +
			"_id INTEGER PRIMARY KEY, " +
			"usuario TEXT, " +
			"senha TEXT, " +
			"realm TEXT, " +
			"domain TEXT, " +
			"host TEXT, " +
			"porta INTEGER);";
	
	private static final String NOME_BANCO = "voicer_db";
	
	private static final int VERSAO_BANCO = 1;

	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public DatabaseManager(Context context) {
		super(context, NOME_BANCO, null, VERSAO_BANCO);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SCRIPT_CREATE_DB);
		// setando a configuração padrão
		db.execSQL("INSERT INTO " + TABLE_NAME + 
				" ('usuario', 'senha', 'realm', 'domain', 'host', 'porta') values " +
				" ('user1', 'pass1', 'sip:openjsip.net', 'openjsip.net', '192.168.25.155', 5060)");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
