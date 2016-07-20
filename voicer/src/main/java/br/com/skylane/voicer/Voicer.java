/**
 * 
 */
package br.com.skylane.voicer;

import android.app.Application;
import android.content.Context;

/**
 * @author maicon
 *
 */
public class Voicer extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        Voicer.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return Voicer.context;
    }
}
