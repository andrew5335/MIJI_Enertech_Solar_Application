package com.miji.solar.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.miji.solar.MijiMainActivity;
import com.miji.solar.R;
import com.miji.solar.constant.CommandConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TabFragment4#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabFragment4 extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String TAG = "miji";
    private String ret$1, ret$2, ret$3,ret$4, ret$5;
    private CircleImageView circleStatus, statusVeryGood, statusGood, statusBad, statusPower, statusCharge, statusOverVoltage, statusLowVoltage;
    private ImageView refresh;

    private MijiMainActivity mijiMain;

    private String sendRefresh = CommandConstants.sendRefresh;

    private BarChart lineChart;

    private Bundle bundle;

    private TextView updateTime;

    public TabFragment4() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabFragment4.
     */
    // TODO: Rename and change types and number of parameters
    public static TabFragment4 newInstance(String param1, String param2) {
        TabFragment4 fragment = new TabFragment4();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        bundle = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_tab4, container, false);
        mijiMain = (MijiMainActivity) getActivity();
        LinearLayout frag1Linear = root.findViewById(R.id.frag4linear);
        refresh = root.findViewById(R.id.refresh);
        lineChart = root.findViewById(R.id.miji_chart);
        lineChart.setVisibleXRangeMaximum(5);
        updateTime = root.findViewById(R.id.updateTime);

        frag1Linear.setOnClickListener(this);
        refresh.setOnClickListener(this);

        setChart("1");
        Log.e("graph", "111");

        if(null != bundle) {
            //Log.i(TAG, bundle.getString("data"));
            Toast.makeText(getContext(), "tab4 data1 : " + bundle.getString("data"), Toast.LENGTH_LONG).show();

            setChart(bundle.getString("data"));
        } else {
            setChart("1");
        }


        return root;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.refresh:
                //((MijiMainActivity) getActivity()).sendData2(sendRefresh);
                mijiMain.sendData2(sendRefresh);
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
                String now = sdf.format(date);
                updateTime.setText("업데이트 시각 : " + now);
                Log.e(TAG, "refresh click");
                break;
        }
    }

    public void setChart(String data) {
        //Toast.makeText(getActivity().getApplicationContext(), "tab4 data2 : " + data, Toast.LENGTH_LONG).show();
        if(null != data && !"".equals(data) && 0 < data.length()) {
            ArrayList<BarEntry> chartVal = new ArrayList<BarEntry>();

            for(int i=0; i < 10; i++) {
                float val = (float) (Math.random() * 10);
                chartVal.add(new BarEntry(i, val));
            }

            BarDataSet dataSet = new BarDataSet(chartVal, "DataSet 1");
            ArrayList<BarDataSet> dataSetList = new ArrayList<BarDataSet>();
            dataSetList.add(dataSet);

            BarData chartData = new BarData(dataSet);

            dataSet.setColor(Color.CYAN);
            //dataSet.setCircleColor(Color.MAGENTA);
            //dataSet.setLineWidth(3);
            dataSet.setLabel("test");

            lineChart.setData(chartData);

        } else {

        }
    }
}