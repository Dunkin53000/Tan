package activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.pandf.moovin.R;
import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;
import com.txusballesteros.bubbles.OnInitializedCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import adapter.CustomListAdapter;
import adapter.CustomListAdapterTemps;
import app.AppController;
import model.Arrets;
import model.Temps;
import util.Spfav;
import util.Utility;


public class TempsActivity extends ActionBarActivity  {
    final Random rnd = new Random();
    private static final String TAG = MainActivity.class.getSimpleName();
    private BubblesManager bubblesManager;
    private int Timebeforestart;
    private int Timenotif;
    Spfav sharedPreference;
    List<Arrets> favorites;
    Activity activity;
    CustomListAdapter productListAdapter;
    public static final String PREFS_NAME = "PRODUCT_APP";
    public static final String FAVORITES = "Product_Favorite_New";

    // Movies json url
    private Toolbar mToolbar;
    private List<Temps> directionList = new ArrayList<Temps>();
    private ListView listView2;
    private CustomListAdapterTemps adapter;
    private SwipeRefreshLayout swipeLayout2;
    private Menu menu;
    private MenuInflater inflater;
    private PendingIntent pendingIntent;
    String lieuid = "";
    String lignesextra = "";
    String arretextra = "";

    boolean favorited = false;

   private String arret;

    public final String URL =
            "http://pierre.hellophoto.fr/tan2/" + rnd.nextInt(3) +".png";
    ImageView imageView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_temps, container, false);

        View headerView = getLayoutInflater().inflate(
                R.layout.view_list_item_header, listView2, false);

        // Inflate the layout for this fragment
        return rootView;


    }





    public void onCreate (
            final Bundle savedInstanceState) {
        Utility.themer(TempsActivity.this);
        super.onCreate(savedInstanceState);

        initializeBubblesManager();



        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Chargement");
        progress.setMessage("Veuillez patienter pendant le chargement des horaires en temps réel");
        progress.setCancelable(false);
        progress.show();


        Intent intents = getIntent();

        String id = intents.getStringExtra("libelle");
        setContentView(R.layout.activity_temps);



        listView2 = (ListView) findViewById(R.id.list_temps);


        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Create an object for subclass of AsyncTask
            GetXMLTask task = new GetXMLTask();
            // Execute the task
            task.execute(new String[] { URL });
        } else {
            Toast.makeText(getApplicationContext(),
                    "Pas de connexion internet",
                    Toast.LENGTH_LONG).show();
        }




        mToolbar = (Toolbar) findViewById(R.id.anim_toolbar);



        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        collapsingToolbar.setTitle(id);
        arretextra = id;


        final Intent intent = getIntent();

        final String url = "http://open.tan.fr/ewp/tempsattente.json/" + intent.getExtras().getString("text") + " ";

        lieuid = intent.getExtras().getString("text");
        lignesextra = intent.getExtras().getString("ligne");

        Log.d("ArretsActivityID", intent.getExtras().getString("text"));


        listView2 = (ListView) findViewById(R.id.list_temps);

        // movieList is an empty array at this point.
        adapter = new CustomListAdapterTemps(this, directionList);
        listView2.setAdapter(adapter);




        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent i = new Intent(getBaseContext(), HorairesActivity.class);


                TextView textView2 = (TextView) view.findViewById(R.id.sens);
                String libelle = textView2.getText().toString();

                TextView textView3 = (TextView) view.findViewById(R.id.terminus);
                String terminus = textView3.getText().toString();

                TextView textView4 = (TextView) view.findViewById(R.id.codearret);
                String arret2 = textView4.getText().toString();

                TextView textView5 = (TextView) view.findViewById(R.id.ligne);
                String ligne = textView5.getText().toString();


                TextView t = (TextView) findViewById(R.id.headertext);


                i.putExtra("sens", libelle);
                i.putExtra("id", arret2);
                i.putExtra("ligne", ligne);
                i.putExtra("arret", collapsingToolbar.getTitle().toString());
                i.putExtra("terminus", terminus);
                startActivity(i);


            }
        });


        listView2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {


            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, final View view, int position, long arg3) {


                TextView textView2 = (TextView) view.findViewById(R.id.temps);

                String temps = textView2.getText().toString().replace(" mn", "");

                try
                {


                    Timebeforestart = Integer.parseInt(temps);






                RelativeLayout linearLayout = new RelativeLayout(TempsActivity.this);
                final NumberPicker aNumberPicker = new NumberPicker(TempsActivity.this);
                aNumberPicker.setMaxValue(Timebeforestart - 1);
                aNumberPicker.setMinValue(1);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
                RelativeLayout.LayoutParams numPicerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

                linearLayout.setLayoutParams(params);
                linearLayout.addView(aNumberPicker, numPicerParams);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TempsActivity.this);
                alertDialogBuilder.setTitle("Créer une notification");
                alertDialogBuilder.setMessage("Nombre de minutes avant le départ théorique:");
                alertDialogBuilder.setView(linearLayout);

                alertDialogBuilder
                        .setCancelable(false)

                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        Log.e("", "New Quantity Value : " + aNumberPicker.getValue());

                                        int timeminute = aNumberPicker.getValue();

                                        Timenotif = Timebeforestart - aNumberPicker.getValue();
                                        Timenotif = Timenotif * 60000;


                                        scheduleNotification(getNotification("Votre bus passe dans " + timeminute + " minutes", view), Timenotif, view);

                                        Toast.makeText(TempsActivity.this, "Notification créée", Toast.LENGTH_SHORT).show();


                                        Log.d("ALARM", String.valueOf(Timenotif));


                                    }
                                })
                        .setNegativeButton("Annuler",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();




                } catch(NumberFormatException nfe)
                {

                    new AlertDialog.Builder(TempsActivity.this)
                            .setTitle("Erreur")
                            .setMessage("Votre bus partira bientôt")
                            .setPositiveButton("Ok", null).show();

                }


                return true;
            }
        });



        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {

                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());


                        if (response.length() == 0) {

                            new AlertDialog.Builder(TempsActivity.this)
                                    .setTitle("Erreur")
                                    .setMessage("Pas d'horaire disponible !")
                                    .setPositiveButton("Retour", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent();
                                            intent.setClass(TempsActivity.this, MainActivity.class);

                                            startActivity(intent);
                                        }
                                    })
                                    .setCancelable(false)
                                    .setIcon(R.drawable.ic_alert_circle_black_48dp)
                                    .show();

                        }


                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = null;
                                try {
                                    obj = response.getJSONObject(i);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }




                                Temps temps = new Temps();

                                JSONObject jObject = obj.getJSONObject("ligne");
                                JSONObject jObject2 = obj.getJSONObject("arret");



                                String text = new String(obj.getString("terminus").getBytes("ISO-8859-1"), "UTF-8");



                                temps.setDirection(text);

                                temps.setLigne(jObject.getString("numLigne"));






                                temps.setSens(obj.getString("sens"));

                                temps.setTerminus(text);

                                String temp = obj.getString("temps").replace("Close", "En approche");

                                temps.setTemps(temp);


                                temps.setArret(jObject2.getString("codeArret"));
                                directionList.add(temps);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }


                        }

                        progress.dismiss();
                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {

            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

                progress.dismiss();

                new AlertDialog.Builder(TempsActivity.this)
                        .setTitle("Erreur")
                        .setMessage("Une erreur est survenue")
                        .setPositiveButton("Retour", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setClass(TempsActivity.this, MainActivity.class);

                                startActivity(intent);
                            }
                        })
                        .setCancelable(false)
                        .setIcon(R.drawable.alert9)
                        .show();

                directionList.clear();



                adapter.notifyDataSetChanged();


            }


        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = TempsActivity.this.getTheme();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);

        swipeLayout2 = (SwipeRefreshLayout) findViewById(R.id.container2);
        swipeLayout2.setColorSchemeColors(typedValue.data);


        swipeLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            public void onRefresh() {
                Toast.makeText(getApplication(), "Rechargement...", Toast.LENGTH_SHORT).show();


                directionList.clear();






                adapter.notifyDataSetChanged();

                JsonArrayRequest movieReq = new JsonArrayRequest(url,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                Log.d(TAG, response.toString());



                                if (response.length() == 0) {

                                    new AlertDialog.Builder(TempsActivity.this)
                                            .setTitle("Erreur")
                                            .setMessage("Pas d'horaire disponible !")
                                            .setPositiveButton("Retour", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent();
                                                    intent.setClass(TempsActivity.this, MainActivity.class);

                                                    startActivity(intent);
                                                }
                                            })
                                            .setCancelable(false)
                                            .setIcon(R.drawable.ic_alert_circle_black_48dp)
                                            .show();

                                }


                                // Parsing json
                                for (int i = 0; i < response.length(); i++) {
                                    try {

                                        JSONObject obj = null;
                                        try {
                                            obj = response.getJSONObject(i);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        Temps temps = new Temps();

                                        JSONObject jObject = obj.getJSONObject("ligne");
                                        JSONObject jObject2 = obj.getJSONObject("arret");


                                        String text = new String(obj.getString("terminus").getBytes("ISO-8859-1"), "UTF-8");


                                        temps.setDirection(text);

                                        temps.setLigne(jObject.getString("numLigne"));






                                        temps.setSens(obj.getString("sens"));

                                        temps.setTerminus(text);

                                        String temp = obj.getString("temps").replace("Close", "En approche");

                                        temps.setTemps(temp);


                                        temps.setArret(jObject2.getString("codeArret"));
                                        directionList.add(temps);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                }

                                // notifying list adapter about data changes
                                // so that it renders the list view with updated data
                                adapter.notifyDataSetChanged();
                            }
                        }, new Response.ErrorListener() {

                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());

                        progress.dismiss();

                        new AlertDialog.Builder(TempsActivity.this)
                                .setTitle("Erreur")
                                .setMessage("Une erreur est survenue")
                                .setPositiveButton("Retour", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        intent.setClass(TempsActivity.this, MainActivity.class);

                                        startActivity(intent);
                                    }
                                })
                                .setCancelable(false)
                                .setIcon(R.drawable.ic_alert_circle_black_48dp)
                                .show();

                        directionList.clear();

                        adapter.notifyDataSetChanged();


                    }


                });


                AppController.getInstance().addToRequestQueue(movieReq);

                swipeLayout2.setRefreshing(false);


            }
        });


    }



    private class GetXMLTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            for (String url : urls) {
                map = downloadImage(url);
            }
            return map;
        }



        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.
                        decodeStream(stream, null, bmOptions);
                stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }



    public void onActivityCreated(Bundle savedInstanceState) {

        Intent intent = getIntent();


        final String url = "https://open.tan.fr/ewp/tempsattente.json/" + intent.getExtras().getString("text") + " ";
        System.out.println(intent.getExtras().getString("text") + " Test Test ");





        // movieList is an empty array at this point.
        adapter = new CustomListAdapterTemps(getParent(), directionList);
        listView2.setAdapter(adapter);

        // Showing progress dialog before making http request





    }




    protected final static int getResourceID
            (final String resName, final String resType, final Context ctx)
    {
        final int ResourceID =
                ctx.getResources().getIdentifier(resName, resType,
                        ctx.getApplicationInfo().packageName);
        if (ResourceID == 0)
        {
            throw new IllegalArgumentException
                    (
                            "No resource string found with name " + resName
                    );
        }
        else
        {
            return ResourceID;
        }
    }





    private void scheduleNotification(Notification notification, int delay, View view) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);

        String uuid = UUID.randomUUID().toString();

        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, uuid);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content, View view) {

        Intent intent = getIntent();

        String text = intent.getExtras().getString("text");

        String libelle = intent.getExtras().getString("libelle");


        Intent mapIntent = new Intent(TempsActivity.this, TempsActivity.class);


        String uuid = UUID.randomUUID().toString();

        mapIntent.setAction("dummy_unique_action_identifyer" + uuid);

        mapIntent.putExtra("text", text);
        mapIntent.putExtra("libelle", libelle);



        PendingIntent mapPendingIntent =
                PendingIntent.getActivity(this, 0, mapIntent, 0);





        long[] pattern = {0, 400, 100, 200, 100, 200};
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Alerte Moov'in");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_logo);
        builder.setVibrate(pattern);
        builder.setLights(Color.WHITE, 1000, 1000);
        builder.addAction(0,
               "Ouvrir", mapPendingIntent);
        builder.setAutoCancel(true);


        return builder.build();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_temps, menu);
        this.menu = menu;
        testfave();

        return true;//return true so that the menu pop up is opened

    }
    @Override
    public void onBackPressed() {
        Intent intent = getIntent();

        Intent i = new Intent(TempsActivity.this, MainActivity.class);

        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent intent = getIntent();

                Intent i = new Intent(TempsActivity.this, MainActivity.class);

                startActivity(i);
        }
        int id = menuItem.getItemId();
        if (id == R.id.action_bubble){
            addNewBubble();
            return true;
        }
        if (id == R.id.action_fave) {
            managefave();
            return true;
        }

        return (super.onOptionsItemSelected(menuItem));
    }

    private void testfave() {
        sharedPreference = new Spfav();
        favorites = sharedPreference.getFavorites(TempsActivity.this);
        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);


        Intent intents = getIntent();
        lieuid = intents.getExtras().getString("text");
        lignesextra = intents.getExtras().getString("ligne");
        arretextra = intents.getExtras().getString("libelle");


        if (favorites == null) {
            menu.getItem(0).setIcon(R.drawable.ic_star_outline_white_24dp);
            favorited = false;

        } else {

            if (favorites.size() == 0) {
                menu.getItem(0).setIcon(R.drawable.ic_star_outline_white_24dp);
                favorited = false;

            }
            if (favorites != null) {

                favorites = sharedPreference.getFavorites(TempsActivity.this);
                for (int v = 0; v < favorites.size(); v++) {
                    Arrets arrets = favorites.get(v);
                    if (arrets.getLieu().equals(lieuid)){
                        menu.getItem(0).setIcon(R.drawable.ic_star_white_24dp);
                        favorited = true;
                        Log.d("TEST FAV", "Already fave");
                    }

                }

            }


        }





    }


    private void managefave() {
        sharedPreference = new Spfav();
        favorites = sharedPreference.getFavorites(TempsActivity.this);
        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        Intent intents = getIntent();
        lieuid = intents.getExtras().getString("text");
        lignesextra = intents.getExtras().getString("ligne");
        arretextra = intents.getExtras().getString("libelle");

        if (favorites == null) {

            if (!favorited) {
                Arrets item = new Arrets();
                item.setArret(arretextra);
                item.setLieu(lieuid);

                ArrayList<String> arretsList = new ArrayList<String>();
                if (lignesextra.contains("'")){
                    arretsList = new  ArrayList<String>(Arrays.asList(lignesextra.split(",[ ]*")));
                }else {
                    arretsList.add(lignesextra);
                }

                item.setLigne(arretsList);

                sharedPreference.addFavorite(TempsActivity.this, item);
                Log.d("FAVORITES", "ADDED");
                menu.getItem(0).setIcon(R.drawable.ic_star_white_24dp);
                favorited = true;
            }

            Log.d("TEST FAV 2", "NULL");

        } else {

            if (favorites.size() == 0) {
                Log.d("TEST FAV 2", "Size 0");
            }
            if (favorites != null) {
                productListAdapter = new CustomListAdapter(TempsActivity.this, favorites, true);
            }

            if (!favorited) {
                Arrets item = new Arrets();
                item.setArret(arretextra);
                item.setLieu(lieuid);

                ArrayList<String> arretsList = new ArrayList<String>();
                if (lignesextra.contains("'")) {
                    arretsList = new ArrayList<String>(Arrays.asList(lignesextra.split(",[ ]*")));
                } else {
                    arretsList.add(lignesextra);
                }

                item.setLigne(arretsList);
                sharedPreference.addFavorite(TempsActivity.this, item);
                Log.d("FAVORITES", "ADDED");
                menu.getItem(0).setIcon(R.drawable.ic_star_white_24dp);
                favorited = true;

            } else {

                Intent intents2 = getIntent();
                lieuid = intents2.getExtras().getString("text");
                lignesextra = intents2.getExtras().getString("ligne");
                arretextra = intents2.getExtras().getString("libelle");
                for (int v = 0; v < favorites.size(); v++) {

                    if (favorites.get(v).getLieu().equals(lieuid)) {
                        sharedPreference.removeFavorite(TempsActivity.this,
                                favorites.get(v));
                        favorites.remove(favorites.get(v));
                        sharedPreference.saveFavorites(TempsActivity.this, favorites);
                        Log.d("FAVORITES", "REMOVED");
                        menu.getItem(0).setIcon(R.drawable.ic_star_outline_white_24dp);
                        favorited = false;
                    }
                }
            }






            // Inflate the layout for this fragment

        }



    }


    private void addNewBubble() {
        Intent intent = getIntent();
        String id = intent.getExtras().getString("text");
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
// generate random color
        int color1 = generator.getRandomColor();
        BubbleLayout bubbleView = (BubbleLayout) LayoutInflater.from(TempsActivity.this).inflate(R.layout.bubble_layout, null);

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(id, color1);

        TextDrawable drawable2 = TextDrawable.builder()
                .beginConfig()
                .fontSize(toPx(20))
                .endConfig()
                .buildRound(id, color1);
        ImageView image = (ImageView) bubbleView.findViewById(R.id.avatar);
        image.setImageDrawable(drawable2);

        bubbleView.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {

            @Override
            public void onBubbleClick(BubbleLayout bubble) {
                Toast.makeText(getApplicationContext(), "Chargement..",
                        Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                String text = intent.getExtras().getString("text");
                String libelle = intent.getExtras().getString("libelle");
                Intent i = new Intent(TempsActivity.this, TempsActivity.class);
                i.putExtra("text", text);
                i.putExtra("libelle", libelle);
                startActivity(i);
            }
        });
        bubbleView.setOnBubbleRemoveListener(new BubbleLayout.OnBubbleRemoveListener() {
            @Override
            public void onBubbleRemoved(BubbleLayout bubble) {



            }
        });
        bubbleView.setShouldStickToWall(true);

        bubblesManager.addBubble(bubbleView, 60, 20);
    }

    private void initializeBubblesManager() {
        bubblesManager = new BubblesManager.Builder(this)
                .setTrashLayout(R.layout.bubble_trash_layout)
                .setInitializationCallback(new OnInitializedCallback() {
                    @Override
                    public void onInitialized() {

                    }
                })
                .build();
        bubblesManager.initialize();
    }





    public int toPx(int dp) {
        Resources resources = getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }




}
