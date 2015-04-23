/**
 * 
 */
package br.com.sixinf.voicer.persistencia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author maicon
 *
 */
public class VoicerDAO {
	
	private static VoicerDAO dao;
	
	public static VoicerDAO getInstance(Context ctx){
		if (dao == null)
			dao = new VoicerDAO(ctx);
		return dao;
	}
	
	private static DatabaseManager dbman;

	public VoicerDAO(Context ctx) {
		dbman = new DatabaseManager(ctx);
	}
	
	/**
	 * Altera os dados do carro passado pelo parâmetro
	 * 
	 * @param c
	 */
	public void alterarConfiguracao(Config c){
		SQLiteDatabase db = dbman.getWritableDatabase();
		
		ContentValues v = new ContentValues();
		v.put("usuario", c.getUsuario());
		v.put("senha", c.getSenha());
		v.put("realm", c.getRealm());
		v.put("domain", c.getDomain());
		v.put("host", c.getHost());
		v.put("porta", c.getPorta());
		
		String clausulaWhere = "_id=?";
		String[] args = new String[]{ c.getId().toString() };
		
		db.update(DatabaseManager.TABLE_NAME, v, clausulaWhere, args);
	}
	
	/**
	 * Busca um carro pelo id
	 * 
	 * @return
	 */
	public Config buscaConfiguracao(){
		SQLiteDatabase db = dbman.getReadableDatabase();
		Config conf = null;
		Cursor c = db.query(DatabaseManager.TABLE_NAME, 
				new String[] {"_id", "usuario", "senha", "realm", 
					"domain", "host", "porta"}, 
				null, null, null, null, null);
		
		if (c.moveToFirst()) {
			
			Long id = c.getLong(0);
			String usuario = c.getString(1);
			String senha = c.getString(2);
			String realm = c.getString(3);
			String domain = c.getString(4);
			String host = c.getString(5);
			Integer porta = c.getInt(6);
			
			conf = new Config();
			conf.setId(id);
			conf.setUsuario(usuario);
			conf.setSenha(senha);
			conf.setRealm(realm);
			conf.setDomain(domain);
			conf.setHost(host);
			conf.setPorta(porta);
			
		}
		
		c.close();
		
		return conf;
	}
	
	public void close(){
		dbman.close();
		VoicerDAO.dao = null;
	}
}
