package com.jamesdube.laravelnewsapp;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jamesdube.laravelnewsapp.models.Post;
import com.jamesdube.laravelnewsapp.util.CustomDateSerializer;
import com.jamesdube.laravelnewsapp.util.PostDeserializer;
import com.jamesdube.laravelnewsapp.util.Themes;
import com.kobakei.ratethisapp.RateThisApp;

import java.util.Date;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;

/**
 * Created by james
 */

public class App extends Application {
    private static App Instance;
    private static Gson gson;
    public static String Tag = "LaravelNewsApp";
    private static Realm realm;

    @Override
    public void onCreate() {
        super.onCreate();
        Instance = this;
        Realm();
        Fabric.with(this, new Crashlytics());
    }

    /**
     * App rating init
     */
    static void rateThisApp(Context context) {
        // Monitor launch times and interval from installation
        RateThisApp.onStart(context);
        // If the criteria is satisfied, "Rate this app" dialog will be shown
        RateThisApp.showRateDialogIfNeeded(context);
    }

    /**
     * Get the singleton Instance of the App class
     * @return App
     */
    public static App getInstance() {
        if(Instance == null) {
            Log.d(Tag,"app init");
            Instance = new App();
        }
        return Instance;
    }

    public static Context getAppContext() {
        return getInstance().getApplicationContext();
    }

    //Gson Singleton
    public static Gson Gson(){
        if(gson == null){
            configureGson();
        }
        return gson;
    }

    public static Realm Realm() {
        if(realm == null){
            // Create a RealmConfiguration that saves the Realm file in the app's "files" directory.
            Realm.init(getAppContext());
            RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();
            Realm.setDefaultConfiguration(realmConfig);

            // Get a Realm instance for this thread
            realm = Realm.getDefaultInstance();

            realm.addChangeListener(new RealmChangeListener<Realm>() {
                @Override
                public void onChange(Realm element) {
                    Log.d(Tag,"app realmOnChangeListener");
                }
            });
        }

        return realm;
    }

    private static void configureGson() {
        // Configure GSON
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new CustomDateSerializer());
        gsonBuilder.registerTypeAdapter(Post.class, new PostDeserializer());
        gsonBuilder.setPrettyPrinting();
        gson = gsonBuilder.create();
    }
}
