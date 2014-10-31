package songmho.com.csvtoparse;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

/**
 * Created by songmho on 2014-10-28.
 */
public class Csv2ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, "p1PJUJFdYiwzkuo4amS9dJ76Xejjjxnuh4YLGCJX", "qh97PiWtLFJ25Lc6TC7UYlYzwQw1g1oSsETikVI6");

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
