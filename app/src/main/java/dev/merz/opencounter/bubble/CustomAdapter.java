package dev.merz.opencounter.bubble;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dev.merz.opencounter.R;

public class CustomAdapter extends ArrayAdapter<AppListItem> {

    private final PackageManager pm;
    Context context;
    int layoutResourceId;
    ArrayList<AppListItem> data = null;

    public CustomAdapter(Context context, int resource, List<AppListItem> objects) {
        super(context, resource, objects);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = (ArrayList) objects;
        this.pm = getContext().getPackageManager();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ItemHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ItemHolder();
            holder.label = (TextView) row.findViewById(R.id.list_label);
            holder.count = (TextView)row.findViewById(R.id.launch_count);

            row.setTag(holder);
        }
        else
        {
            holder = (ItemHolder) row.getTag();
        }

        AppListItem item = data.get(position);
        holder.label.setText(item.label);
        holder.count.setText(Integer.toString(item.count));

        return row;
    }

    private class ItemHolder {
        public TextView label;
        public TextView count;

    }
}
