package activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.pierre.tan.R;

import adapter.FragmentDrawer;
import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import model.Arrets;


public class MainActivity extends ActionBarActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    public static Arrets arrets = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppRate.with(this)
                .setInstallDays(5) // default 10, 0 means install day.
                .setLaunchTimes(10) // default 10
                .setRemindInterval(1) // default 1
                .setShowNeutralButton(true) // default true
                .setDebug(false) // default false
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        Log.d(MainActivity.class.getName(), Integer.toString(which));
                    }
                })
                .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);


        arrets = new Arrets();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);



        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        View searchContainer = findViewById(R.id.search_container);
        final EditText toolbarSearchView = (EditText) findViewById(R.id.search);
        ImageView searchClearButton = (ImageView) findViewById(R.id.search_clear);



        searchClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              toolbarSearchView.setText("");
            }
        });



        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        drawerFragment.setFocusableInTouchMode(false);

        // display the first navigation drawer view on app launch
        displayView(0);

        searchContainer.setVisibility(View.GONE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, MyPreferencesActivity.class);
            startActivity(i);
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Voulez-vous quitter l\'application ?")
                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("non", null).show();
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:

                fragment = new HomeFragment();
                title = getString(R.string.title_home);
                break;
            case 1:
                fragment = new ArretsFragment();
                title = "Arrets";
                break;
            case 2:
                fragment = new AboutFragment();
                title = "A propos";
                break;
            case 3:
                fragment = new SettingsFragment();
                title = "Paramètres";
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }
}
