package activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pandf.moovin.R;

import adapter.FragmentDrawer;
import model.Arrets;
import util.Utility;


public class MainActivity extends ActionBarActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    public static Arrets arrets = null;





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Utility.themer(MainActivity.this);

        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(MainActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1234);
            }
        }
        else
        {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startService(intent);
        }

        setContentView(R.layout.activity_main);
        arrets = new Arrets();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        View searchContainer = findViewById(R.id.search_container);

        searchContainer.setVisibility(View.GONE);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        drawerFragment.setFocusableInTouchMode(false);



        // display the first navigation drawer view on app launch
        displayView(0);



    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setMessage("Voulez-vous quitter l\'application ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Intent relaunch = new Intent(MainActivity.this, Exiter.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK // CLEAR_TASK requires this
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK // finish everything else in the task
                                        | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS); // hide (remove, in this case) task from recents
                        startActivity(relaunch);
                    }
                }).setNegativeButton("Non", null).show();


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
                fragment = new MeteoFragment();
                title = "Météo";
                break;
            case 2:
                fragment = new ArretsFragment();
                title = "Arrêts";
                break;
            case 3:
                fragment = new LigneFragment();
                title = "Lignes";
                break;
            case 4:
                fragment = new GeoFragment();
                title = "Géolocalisation";
                break;
            case 5:
                fragment = new BiclooFragment();
                title = "Bicloos";
                break;
            case 6:
                Intent i = new Intent(MainActivity.this, ItineraireActivity.class);
                title = "Itinéraire";
                startActivity(i);
                break;
            case 7:
                fragment = new ParkingFragment();
                title = "Parkings";
                break;
            case 8:
                Intent i4 = new Intent(MainActivity.this, MyPreferencesActivity.class);
                title = "Paramètres";
                startActivity(i4);
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
