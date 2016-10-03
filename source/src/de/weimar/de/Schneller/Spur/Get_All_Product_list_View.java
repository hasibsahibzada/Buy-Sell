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
import android.widget.ImageView;
import android.widget.TextView;

public class Get_All_Product_list_View extends BaseAdapter{
	private JSONArray dataArray;
	private Activity activity;
	private static LayoutInflater inflater = null;
	
	public Get_All_Product_list_View(JSONArray jsonarray, Activity a) 
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
			convertView = inflater.inflate(R.layout.list_product_view, null);
			cell = new ListCell();
			cell.Full_PName = (TextView) convertView.findViewById(R.id.pname_view);
			cell.full_price = (TextView) convertView.findViewById(R.id.pprice_view);
			cell.Product_image = (ImageView) convertView.findViewById(R.id.product_image);
			convertView.setTag(cell);
 		}
		else {
			cell = (ListCell) convertView.getTag();
		}
		// change the data cell
		try {
			JSONObject jsongObject = this.dataArray.getJSONObject(position); 
			String p_name = jsongObject.getString("Pname");
			cell.Full_PName.setText(jsongObject.getString("Pname"));
			cell.full_price.setText(jsongObject.getString("Price"));
			switch(p_name){
			case "shoes":
				cell.Product_image.setImageResource(R.drawable.cash);
				break;
			case "book":
				cell.Product_image.setImageResource(R.drawable.blue_bg);
				break;
			case "piano":
				cell.Product_image.setImageResource(R.drawable.gray_bg);
				break;
			default:
				cell.Product_image.setImageResource(R.drawable.ic_launcher);
			}
			
		}catch(JSONException e)
		{
			e.printStackTrace();
		}
		return convertView;
	}
	
	private class ListCell 
	{
		private TextView Full_PName;
		private TextView full_price;
		private ImageView Product_image;
	}
}
