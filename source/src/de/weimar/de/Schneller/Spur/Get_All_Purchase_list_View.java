package de.weimar.de.Schneller.Spur;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.weimar.de.Schneller.Spur.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class Get_All_Purchase_list_View extends BaseAdapter{
	private JSONArray dataArray;
	private Activity activity;
	private static LayoutInflater inflater = null;

	public Get_All_Purchase_list_View(JSONArray jsonarray, Activity a) 
	{
		this.dataArray = jsonarray;
		this.activity = a;
		inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return this.dataArray.length();
	}
	
	@Override
	public Object getItem(int position) {
		if (position ==0)       // starts from the beginning of the page
			position = 7;
		return position;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// set up the convert view if it is null
		ListCell cell;
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.list_purchases_view, null);
			cell = new ListCell();
			cell.full_product_Name 	= (TextView) convertView.findViewById(R.id.purchase_name_view);
			cell.full_status 		= (TextView) convertView.findViewById(R.id.Status_view);
			cell.full_owner 		= (TextView) convertView.findViewById(R.id.p_owner_view);
			convertView.setTag(cell);
 		}
		else {
			cell = (ListCell) convertView.getTag();
		}
		// change the data cell
		try {
			JSONObject jsongObject = this.dataArray.getJSONObject(position); 
			cell.full_product_Name.setText(jsongObject.getString("Pname"));
			cell.full_status.setText(jsongObject.getString("status"));
			cell.full_owner.setText(jsongObject.getString("Owner_name"));
		}catch(JSONException e)
		{
			e.printStackTrace();
		}
		return convertView;
	}
	
	private class ListCell 
	{
		private TextView full_product_Name;
		private TextView full_status;
		private TextView full_owner;
	}
}
