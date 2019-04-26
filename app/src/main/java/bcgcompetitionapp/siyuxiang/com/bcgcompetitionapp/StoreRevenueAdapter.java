package bcgcompetitionapp.siyuxiang.com.bcgcompetitionapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class StoreRevenueAdapter extends ArrayAdapter<CaseData> {
    private ArrayList<CaseData> items;
    private Context adapterContext;

    public StoreRevenueAdapter(Context context, ArrayList<CaseData> items) {
        super(context, R.layout.list_item_store_revenue, items);
        adapterContext = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        try {
            CaseData caseData = items.get(position);

            if (v == null) {
                LayoutInflater vi = (LayoutInflater)
                        adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item_store, null);
            }

            TextView txtAddress = (TextView) v.findViewById(R.id.txtAddress);
            TextView txtCity = (TextView) v.findViewById(R.id.txtCity);
            TextView txtState = (TextView) v.findViewById(R.id.txtState);
            TextView txtZipCode = (TextView) v.findViewById(R.id.txtZipCode);
            TextView txtDetailInfo = (TextView) v.findViewById(R.id.txtDetailInfo);
            txtAddress.setText(caseData.getAddress());
            txtCity.setText(caseData.getCity());
            txtState.setText(caseData.getState());
            txtZipCode.setText(caseData.getZipCode());
            txtDetailInfo.setText("Average Order Revenue Per Month: " + String.valueOf(caseData.getOrderRevenue()));
        }
        catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        return v;
    }

    public void removeAll(){
        items.clear();
        //counter=0;
        notifyDataSetChanged();
    }

}
