package activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.pandf.moovin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import adapter.CustomPagerAdapterResto;
import adapter.GridSimpleAdapter;
import app.AppController;
import model.DiscoverResto;
import util.Utility;

/**
 * Created by dev on 29/07/2015.
 */
public class DiscoverActivity extends ActionBarActivity  {

    private Toolbar mToolbar;
    AutoCompleteTextView depart;
    AutoCompleteTextView arrive;
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    private static final String TAG = MainActivity.class.getSimpleName();
    private List<DiscoverResto> restoList = new ArrayList<DiscoverResto>();

    Activity activity;

    Toolbar toolbar;


    public Bitmap blurBitmap(Bitmap bitmap){

        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        //Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(getApplicationContext());

        //Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        //Set the radius of the blur
        blurScript.setRadius(25.f);

        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);

        //recycle the original bitmap
        bitmap.recycle();

        //After finishing everything, we destroy the Renderscript.
        rs.destroy();

        return outBitmap;


    }




    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Utility.themer(DiscoverActivity.this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_discover);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(DiscoverActivity.this, MainActivity.class);

                DiscoverActivity.this.startActivity(myIntent);

            }
        });
        LinearLayout layout_root = (LinearLayout) findViewById(R.id.content_root);


        final ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
        viewpager.setVisibility(View.GONE);

         /* adapt the image to the size of the display */
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Bitmap bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(),R.drawable.wood),size.x,size.y,true);

        Drawable d = new BitmapDrawable(getResources(), blurBitmap(bmp));

        layout_root.setBackground(d);


        TextView textsearch = (TextView) findViewById(R.id.textViewsearchdisco);

        Animation anim_title = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.anim_title);


        textsearch.startAnimation(anim_title);

        ImageButton buttonsearch = (ImageButton) findViewById(R.id.discoverImageBtn);

        buttonsearch.startAnimation(anim_title);


        final LinearLayout contentlayout = (LinearLayout) findViewById(R.id.content);


        final GridSimpleAdapter adapter = new GridSimpleAdapter(DiscoverActivity.this, true);
        buttonsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPlus dialogplus = DialogPlus.newDialog(DiscoverActivity.this)
                        .setContentHolder(new GridHolder(3))
                        .setCancelable(true)
                        .setAdapter(adapter)
                        .setGravity(Gravity.CENTER)
                        .setInAnimation(R.anim.abc_fade_in)
                        .setOutAnimation(R.anim.abc_fade_out)
                        .setExpanded(false)
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(DialogPlus dialog, Object item, View view, final int position) {
                                dialog.dismiss();
                                Animation anim_opacity = AnimationUtils.loadAnimation(getApplicationContext(),
                                        R.anim.opacity);

                                final Animation anim_slideup = AnimationUtils.loadAnimation(getApplicationContext(),
                                        R.anim.anim_viewpager);

                                anim_opacity.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        contentlayout.setVisibility(View.GONE);


                                        setupcardsview(position);

                                        viewpager.setVisibility(View.VISIBLE);
                                        viewpager.startAnimation(anim_slideup);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });

                                contentlayout.startAnimation(anim_opacity);

                            }
                        })


                        .create();
                dialogplus.show();
            }
        });

    }





    public void setupcardsview(int position) {
        final ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);



            if (position == 0) {
                viewpager.setAdapter(new CustomPagerAdapterResto(this, restoList));

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                        "http://data.nantes.fr/api/publication/23440003400026_J327/tourinsoft_restaurant_table/content/?format=json&limit=50", null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("DEBUG", response.toString());

                        try {
                            // Parsing json object response
                            // response will be a json object


                            JSONArray array = response.getJSONArray("data");

                            // Parsing json
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject obj = null;
                                try {
                                    obj = array.getJSONObject(i);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                DiscoverResto resto = new DiscoverResto();

                                resto.setNomresto(obj.getJSONObject("geo").getString("name"));
                                resto.setAdresse(obj.getString("Adresse2"));
                                resto.setTypecuisine(obj.getString("Categorie"));
                                resto.setNbcouverts(obj.getString("CapaciteNbCouverts"));

                                restoList.add(resto);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                        viewpager.getAdapter().notifyDataSetChanged();
                    }


                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                });


                AppController.getInstance().addToRequestQueue(jsonObjReq);

            }




    }



    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(DiscoverActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }



















}


