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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.miji.solar.MijiMainActivity;
import com.miji.solar.R;
import com.miji.solar.constant.CommandConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
    private String requestData = CommandConstants.sendRequest;

    private BarChart barChart, barChart2;
    private XAxis xAxis1, xAxis2;
    private YAxis yAxis1, yAxis2, yAxis11, yAxis12;
    private ArrayList<String> xVals1, xVals2;

    private Bundle bundle;

    private TextView updateTime;
    private Button dataBtn;

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
        dataBtn = root.findViewById(R.id.load_data);

        barChart = root.findViewById(R.id.miji_chart);
        barChart.setVisibleXRangeMaximum(5);
        barChart2 = root.findViewById(R.id.miji_chart2);
        barChart2.setVisibleXRangeMaximum(5);

        xAxis1 = barChart.getXAxis();
        xAxis2 = barChart2.getXAxis();
        yAxis1 = barChart.getAxisLeft();
        yAxis2 = barChart2.getAxisLeft();
        yAxis11 = barChart.getAxisRight();
        yAxis12 = barChart2.getAxisRight();

        xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis1.setTextSize(7);
        xAxis1.setTextColor(Color.WHITE);
        xAxis2.setTextColor(Color.WHITE);
        xAxis2.setTextSize(7);
        xAxis1.setLabelCount(5);
        xAxis2.setLabelCount(5);
        xAxis1.setDrawGridLines(false);
        xAxis2.setDrawGridLines(false);
        yAxis1.setDrawGridLines(false);
        yAxis2.setDrawGridLines(false);
        yAxis11.setDrawGridLines(false);
        yAxis12.setDrawGridLines(false);

        updateTime = root.findViewById(R.id.updateTime);

        frag1Linear.setOnClickListener(this);
        refresh.setOnClickListener(this);
        dataBtn.setOnClickListener(this);

        setChart("");
        Log.e("graph", "111");

        if(null != bundle) {
            //Log.i(TAG, bundle.getString("data"));
            //Toast.makeText(getContext(), "tab4 data1 : " + bundle.getString("data3"), Toast.LENGTH_LONG).show();

            setChart(bundle.getString("data3"));
        } else {
            setChart("");
        }


        return root;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.refresh:
                //((MijiMainActivity) getActivity()).sendData2(sendRefresh);
                mijiMain.sendData2(requestData);
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
                String now = sdf.format(date);
                updateTime.setText("업데이트 시각 : " + now);
                Log.e(TAG, requestData + " click");
                break;

            case R.id.load_data:
                mijiMain.sendData2(requestData);
                Log.e(TAG, "data request");
                break;
        }
    }

    public void setChart(String data) {
        //Toast.makeText(getActivity().getApplicationContext(), "tab4 data2 : " + data, Toast.LENGTH_LONG).show();
        if(null != data && !"".equals(data) && 0 < data.length()) {
            data = data.replaceAll(System.getProperty("line.separator"), "");
            data = data.replaceAll("&", "");
            String[] tmpArr = data.split("/");

            List<String> tmpList1 = new ArrayList<String>();
            List<String> tmpList2 = new ArrayList<String>();

            List<BarEntry> barList1 = new ArrayList<BarEntry>();
            List<BarEntry> barList2 = new ArrayList<BarEntry>();
            List<BarEntry> barList3 = new ArrayList<BarEntry>();

            if(null != tmpArr && 0 < tmpArr.length) {
                for(int i=0; i < tmpArr.length; i++) {
                    String tmpStr = tmpArr[i];
                    tmpList1.add(String.valueOf(Integer.parseInt(tmpStr.substring(17, 21)) - Integer.parseInt(tmpStr.substring(4, 8))));

                    if(0 < i && i < tmpArr.length) {
                        tmpList2.add(String.valueOf(Integer.parseInt(tmpArr[i - 1].substring(17, 21)) - Integer.parseInt(tmpArr[i].substring(4, 8))));
                    }

                    Log.e("mijierror333", tmpArr[i].substring(26, 28));
                    barList3.add(new BarEntry((float) i, ((40 - Float.parseFloat(tmpArr[i].substring(26, 28))) / 20) * 100));
                }

                if(null != tmpList1 && 0 < tmpList1.size()) {
                    for(int i=0; i < tmpList1.size(); i++) {
                        if(tmpList1.get(i).length() == 3) {
                            barList1.add(new BarEntry((float) i, Float.parseFloat(tmpList1.get(i).substring(0, 1) + "." + tmpList1.get(i).substring(1, 3)) * 250));
                        } else if(tmpList1.get(i).length() == 2) {
                            barList1.add(new BarEntry((float) i, Float.parseFloat("0." + tmpList1.get(i).substring(0, 2)) * 250));
                        } else if(tmpList1.get(i).length() == 1) {
                            barList1.add(new BarEntry((float) i, Float.parseFloat(tmpList1.get(i).substring(0, 1)) * 250));
                        }
                    }
                }

                if(null != tmpList2 && 0 < tmpList2.size()) {
                    barList2.add(new BarEntry(0, 0));
                    for(int j=0; j < tmpList2.size(); j++) {
                        /**if(0 < j && j < tmpList2.size()) {
                         Log.e("mijierror", "mijierror : " + tmpList2.get(j));
                         }**/
                        if(0 < j && j < tmpList2.size()) {
                            if (tmpList2.get(j).length() == 3) {
                                barList2.add(new BarEntry((float) j, Float.parseFloat(tmpList2.get(j).substring(0, 1) + "." + tmpList2.get(j).substring(1, 3)) * 250));
                            } else if (tmpList2.get(j).length() == 2) {
                                barList2.add(new BarEntry((float) j, Float.parseFloat("0." + tmpList2.get(j).substring(0, 2)) * 250));
                            } else if (tmpList2.get(j).length() == 1) {
                                barList2.add(new BarEntry((float) j, Float.parseFloat(tmpList2.get(j).substring(0, 1)) * 250));
                            }
                        }
                    }
                }

                xVals1 = new ArrayList<String>();
                if(null != barList1 && 0 < barList1.size()) {
                    for(int i=0; i < barList1.size(); i++) {
                        xVals1.add(getDate(-i));
                    }
                }

                xVals2 = new ArrayList<String>();
                if(null != barList3 && 0 < barList3.size()) {
                    for(int i=0; i < barList3.size(); i++) {
                        xVals2.add(getDate(-i));
                    }
                }

                BarDataSet dataSet = new BarDataSet(barList1, "발전량");
                BarDataSet dataSet2 = new BarDataSet(barList2, "사용량");
                BarDataSet dataSet3 = new BarDataSet(barList3, "배터리성능");

                ArrayList<BarDataSet> dataSetList = new ArrayList<BarDataSet>();
                ArrayList<BarDataSet> dataSetList2 = new ArrayList<BarDataSet>();

                dataSetList.add(dataSet);
                dataSetList.add(dataSet2);
                dataSetList2.add(dataSet3);

                BarData chartData = new BarData(dataSet);
                chartData.addDataSet(dataSet2);

                BarData chartData2 = new BarData(dataSet3);

                chartData.setBarWidth(0.3f);
                chartData2.setBarWidth(0.3f);

                dataSet.setColor(Color.BLUE);
                dataSet.setBarBorderWidth((float) 0.02);
                dataSet2.setColor(Color.GREEN);
                dataSet2.setBarBorderWidth((float) 0.02);

                dataSet3.setColor(Color.GRAY);
                dataSet3.setBarBorderWidth((float) 0.02);

                Collections.reverse(xVals1);
                Collections.reverse(xVals2);
                xAxis1.setValueFormatter(new IndexAxisValueFormatter(xVals1));
                xAxis2.setValueFormatter(new IndexAxisValueFormatter(xVals2));

                barChart.setData(chartData);
                barChart.groupBars(0.2f, 0.3f, 0.02f);

                barChart2.setData(chartData2);
            }

        } else {
            // 데이터가 없을 경우 아래 샘플 코드로 챠트 생성
            /**
            data = "&001027520010~001128700031020/" +
                    "&002027520010~002128650031022/" +
                    "&003027260010~003128260031023/" +
                    "&004027520010~004128650031024/" +
                    "&005027470010~005128750031025/" +
                    "&006027520010~001128800031028/" +
                    "&007027470010~001128400031033/" +
                    "&008026980010~001127570031039/" +
                    "&009026390010~001128500031042/" +
                    "&010027470010~001128850031043/";
             **/

            if(null != data && !"".equals(data) && 0 < data.length()) {
                data = data.replaceAll(System.getProperty("line.separator"), "");
                data = data.replaceAll("&", "");
                String[] tmpArr = data.split("/");

                List<String> tmpList1 = new ArrayList<String>();
                List<String> tmpList2 = new ArrayList<String>();

                List<BarEntry> barList1 = new ArrayList<BarEntry>();
                List<BarEntry> barList2 = new ArrayList<BarEntry>();
                List<BarEntry> barList3 = new ArrayList<BarEntry>();

                if(null != tmpArr && 0 < tmpArr.length) {
                    for(int i=0; i < tmpArr.length; i++) {
                        String tmpStr = tmpArr[i];
                        tmpList1.add(String.valueOf(Integer.parseInt(tmpStr.substring(17, 21)) - Integer.parseInt(tmpStr.substring(4, 8))));

                        if(0 < i && i < tmpArr.length) {
                            tmpList2.add(String.valueOf(Integer.parseInt(tmpArr[i - 1].substring(17, 21)) - Integer.parseInt(tmpArr[i].substring(4, 8))));
                        }

                        Log.e("mijierror333", tmpArr[i].substring(26, 28));
                        barList3.add(new BarEntry((float) i, ((40 - Float.parseFloat(tmpArr[i].substring(26, 28))) / 20) * 100));
                    }

                    if(null != tmpList1 && 0 < tmpList1.size()) {
                        for(int i=0; i < tmpList1.size(); i++) {
                            if(tmpList1.get(i).length() == 3) {
                                barList1.add(new BarEntry((float) i, Float.parseFloat(tmpList1.get(i).substring(0, 1) + "." + tmpList1.get(i).substring(1, 3)) * 250));
                            } else if(tmpList1.get(i).length() == 2) {
                                barList1.add(new BarEntry((float) i, Float.parseFloat("0." + tmpList1.get(i).substring(0, 2)) * 250));
                            } else if(tmpList1.get(i).length() == 1) {
                                barList1.add(new BarEntry((float) i, Float.parseFloat(tmpList1.get(i).substring(0, 1)) * 250));
                            }
                        }
                    }

                    if(null != tmpList2 && 0 < tmpList2.size()) {
                        barList2.add(new BarEntry(0, 0));
                        for(int j=0; j < tmpList2.size(); j++) {
                            /**if(0 < j && j < tmpList2.size()) {
                                Log.e("mijierror", "mijierror : " + tmpList2.get(j));
                            }**/
                            if(0 < j && j < tmpList2.size()) {
                                if (tmpList2.get(j).length() == 3) {
                                    barList2.add(new BarEntry((float) j, Float.parseFloat(tmpList2.get(j).substring(0, 1) + "." + tmpList2.get(j).substring(1, 3)) * 250));
                                } else if (tmpList2.get(j).length() == 2) {
                                    barList2.add(new BarEntry((float) j, Float.parseFloat("0." + tmpList2.get(j).substring(0, 2)) * 250));
                                } else if (tmpList2.get(j).length() == 1) {
                                    barList2.add(new BarEntry((float) j, Float.parseFloat(tmpList2.get(j).substring(0, 1)) * 250));
                                }
                            }
                        }
                    }

                    xVals1 = new ArrayList<String>();
                    if(null != barList1 && 0 < barList1.size()) {
                        for(int i=0; i < barList1.size(); i++) {
                            xVals1.add(getDate(-i));
                        }
                    }

                    xVals2 = new ArrayList<String>();
                    if(null != barList3 && 0 < barList3.size()) {
                        for(int i=0; i < barList3.size(); i++) {
                            xVals2.add(getDate(-i));
                        }
                    }

                    BarDataSet dataSet = new BarDataSet(barList1, "발전량");
                    BarDataSet dataSet2 = new BarDataSet(barList2, "사용량");
                    BarDataSet dataSet3 = new BarDataSet(barList3, "배터리성능");

                    ArrayList<BarDataSet> dataSetList = new ArrayList<BarDataSet>();
                    ArrayList<BarDataSet> dataSetList2 = new ArrayList<BarDataSet>();

                    dataSetList.add(dataSet);
                    dataSetList.add(dataSet2);
                    dataSetList2.add(dataSet3);

                    BarData chartData = new BarData(dataSet);
                    chartData.addDataSet(dataSet2);

                    BarData chartData2 = new BarData(dataSet3);

                    chartData.setBarWidth(0.3f);
                    chartData2.setBarWidth(0.3f);

                    dataSet.setColor(Color.CYAN);
                    dataSet.setBarBorderWidth((float) 0.02);
                    dataSet2.setColor(Color.GREEN);
                    dataSet2.setBarBorderWidth((float) 0.02);

                    dataSet3.setColor(Color.GRAY);
                    dataSet3.setBarBorderWidth((float) 0.02);

                    Collections.reverse(xVals1);
                    Collections.reverse(xVals2);
                    xAxis1.setValueFormatter(new IndexAxisValueFormatter(xVals1));
                    xAxis2.setValueFormatter(new IndexAxisValueFormatter(xVals2));

                    barChart.setData(chartData);
                    barChart.groupBars(0.2f, 0.3f, 0.02f);

                    barChart2.setData(chartData2);
                }
            }
        }
    }

    public String getDate(int range) {
        String result = "";
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        cal.add(Calendar.DATE, range);

        return sdf.format(cal.getTime());
    }
}