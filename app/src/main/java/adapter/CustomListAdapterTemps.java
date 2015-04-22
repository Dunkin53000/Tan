package adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.example.pierre.tan.R;

import java.util.List;

import app.AppController;
import model.Temps;


public class CustomListAdapterTemps extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Temps> directionItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    public CustomListAdapterTemps(Activity activity, List<Temps> directionItems) {
        this.activity = activity;
        this.directionItems = directionItems;
    }

    @Override
    public int getCount() {
        return directionItems.size();
    }

    @Override
    public Object getItem(int location) {
        return directionItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_rowtemps, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        TextView direction = (TextView) convertView.findViewById(R.id.direction);
        TextView ligne = (TextView) convertView.findViewById(R.id.ligne);
        TextView sens = (TextView) convertView.findViewById(R.id.sens);
        TextView temps = (TextView) convertView.findViewById(R.id.temps);
        TextView terminus = (TextView) convertView.findViewById(R.id.terminus);

        // getting movie data for the row
        Temps m = directionItems.get(position);


        // title
        direction.setText(m.getDirection());


        ligne.setText(m.getLigne());

        temps.setText(m.getTemps());

        sens.setText(m.getSens());

        terminus.setText(m.getTerminus());

        return convertView;


    }


}