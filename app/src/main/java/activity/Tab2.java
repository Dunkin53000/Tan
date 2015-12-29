package activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.OnButtonClickListener;
import com.dexafree.materialList.card.provider.BasicButtonsCardProvider;
import com.dexafree.materialList.card.provider.SmallImageCardProvider;
import com.dexafree.materialList.view.MaterialListView;
import com.pandf.moovin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.AppController;

import util.SpLite;


public class Tab2 extends Fragment {


    String condition = " ";
    String tmp = " ";
    String icon = " ";


    String condition2 = " ";
    String tmp2 = " ";
    String icon2 = " ";

    private static final String TAG = MainActivity.class.getSimpleName();

    private SwipeRefreshLayout swipeLayout;
    SpLite sharedlite;





    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v =inflater.inflate(R.layout.tab_2,container,false);
        final MaterialListView mListView = (MaterialListView) v.findViewById(R.id.material_listview);






        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();


        if (networkInfo != null && networkInfo.isConnected()) {
            // Create an object for subclass of AsyncTask

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                "http://www.prevision-meteo.ch/services/json/nantes", null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    // Parsing json object response
                    // response will be a json object

                    JSONObject object = response.getJSONObject("current_condition");





                    tmp = object.getString("tmp");

                    condition = object.getString("condition");

                    final MaterialListView mListView = (MaterialListView) v.findViewById(R.id.material_listview);


                    sharedlite = new SpLite();

                    String foo = null;

                    Log.d("LOG", sharedlite.getFavorites(getActivity()).toString());

                        icon = object.getString("icon");




                    Card cardmeteo = new Card.Builder(getActivity())
                            .withProvider(SmallImageCardProvider.class)
                            .setTitle("En direct du ciel :")
                            .setDescription(condition + " (" + tmp + "°)")
                            .setDrawable(icon)
                            .endConfig()
                            .build();



                    mListView.add(cardmeteo);




                    Card card2 = new Card.Builder(getActivity())
                            .withProvider(BasicButtonsCardProvider.class)
                            .setTitle("Nouveau !")
                            .setDescription("Les itinéraires sont enfin disponibles ! (Attention instable !)")
                            .setLeftButtonText("Entrer")

                            .setOnLeftButtonClickListener(new OnButtonClickListener() {
                                @Override
                                public void onButtonClicked(View view, Card card) {
                                    Intent intent = getActivity().getIntent();


                                    Intent i = new Intent(Tab2.this.getActivity(), ItineraireActivity.class);

                                    startActivity(i);
                                }

                            })
                            .endConfig()
                            .build();



                    mListView.add(card2);


                    try {





                    } catch (NullPointerException e) {
                        // do something other
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }


            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());


            }
        });



        AppController.getInstance().addToRequestQueue(jsonObjReq);


        } else {




            Toast.makeText(getActivity().getApplicationContext(),
                    "Pas de connexion internet",
                    Toast.LENGTH_LONG).show();





            Card card = new Card.Builder(getActivity())
                    .withProvider(BasicButtonsCardProvider.class)
                    .setTitle("Pas de connexion")
                    .setDescription("Veuillez vérifier votre connexion internet")
                    .setLeftButtonText("Rafraichir")
                    .setOnLeftButtonClickListener(new OnButtonClickListener() {
                        @Override
                        public void onButtonClicked(View view, Card card) {
                            Intent intent = getActivity().getIntent();


                            Intent i = new Intent(Tab2.this.getActivity(), MainActivity.class);

                            startActivity(i);
                        }

                    })
                    .endConfig()
                    .build();




            mListView.add(card);



            Card card2 = new Card.Builder(getActivity())
                    .withProvider(BasicButtonsCardProvider.class)
                    .setTitle("Infos")
                    .setDescription("Vous avez besoin d'un site internet pour les horaires ? Cliquez ci-dessous !")
                    .setLeftButtonText("Entrer")

                    .setOnLeftButtonClickListener(new OnButtonClickListener() {
                        @Override
                        public void onButtonClicked(View view, Card card) {
                            String url = "http://pierre.hellophoto.fr/tan/arrets.php";
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }

                    })
                    .endConfig()
                    .build();



            mListView.add(card2);


        }

        JsonArrayRequest movieReq = new JsonArrayRequest("http://pierre.hellophoto.fr/moovinantes/json/cards.json",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());


                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = null;
                                try {
                                    obj = response.getJSONObject(i);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                final MaterialListView mListView = (MaterialListView) v.findViewById(R.id.material_listview);


                                if (obj.getString("type").equals("1")) {

                                    final String testurl = obj.getString("url");
                                    Card card2 = new Card.Builder(getActivity())
                                            .withProvider(BasicButtonsCardProvider.class)
                                            .setTitle(obj.getString("title"))
                                            .setDescription(obj.getString("description"))
                                            .setLeftButtonText(obj.getString("bouton"))
                                            .setOnLeftButtonClickListener(new OnButtonClickListener() {
                                                @Override
                                                public void onButtonClicked(View view, Card card) {
                                                    String url = testurl;
                                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                                    i.setData(Uri.parse(url));
                                                    startActivity(i);
                                                }

                                            })
                                            .endConfig()
                                            .build();


                                    mListView.add(card2);
                                }  else if (obj.getString("type").equals("2")){


                                    Card card2 = new Card.Builder(getActivity())
                                            .withProvider(SmallImageCardProvider.class)
                                            .setTitle(obj.getString("title"))
                                            .setDescription(obj.getString("description"))
                                            .endConfig()
                                            .build();



                                    mListView.add(card2);

                                }


                                // adding movie to movies array


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());





            }


        });


        AppController.getInstance().addToRequestQueue(movieReq);




        return v;






    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
setHasOptionsMenu(true);

        swipeLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.refreshome);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {



                Intent intent = getActivity().getIntent();
                Intent i = new Intent(Tab2.this.getActivity(), MainActivity.class);
                startActivity(i);




                swipeLayout.setRefreshing(false);


            }
        });
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);




    }




    public void onCreate (
            final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);





    }





}