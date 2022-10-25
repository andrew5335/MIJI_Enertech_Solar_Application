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
import java.util.LinkedList;
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

    private LinkedList<String> dataList = new LinkedList<String>();

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

        yAxis1.setTextColor(Color.WHITE);
        yAxis2.setTextColor(Color.WHITE);
        yAxis11.setTextColor(Color.WHITE);
        yAxis12.setTextColor(Color.WHITE);

        xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis1.setTextSize(7);
        xAxis1.setTextColor(Color.WHITE);
        xAxis2.setTextColor(Color.WHITE);
        xAxis2.setTextSize(7);
        xAxis1.setLabelCount(5);
        xAxis2.setLabelCount(5);
        /** 그리드 처리 : false면 그리드 감추기, true면 그리드 보이기
        xAxis1.setDrawGridLines(false);
        xAxis2.setDrawGridLines(false);
        yAxis1.setDrawGridLines(false);
        yAxis2.setDrawGridLines(false);
        yAxis11.setDrawGridLines(false);
        yAxis12.setDrawGridLines(false);
         **/

        updateTime = root.findViewById(R.id.updateTime);

        frag1Linear.setOnClickListener(this);
        refresh.setOnClickListener(this);
        dataBtn.setOnClickListener(this);

        if(null != dataList && 0 < dataList.size()) {
            dataList = new LinkedList<String>();
        }

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
                //mijiMain.sendData2(requestData);
                mijiMain.sendData2(sendRefresh);
                //this.displayChart("");
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
        if(null != dataList && 0 < dataList.size()) {
            dataList = new LinkedList<String>();
        }

        if(null != data && !"".equals(data) && 0 < data.length()) {
            if(data.startsWith("&")) {
                data = data.replaceAll("&", "");
            } else if(data.startsWith("~")) {
                data = data.replaceAll("~", "");
            }

            dataList.add(data);

            Log.e(TAG, "receive tag4 data : " + data);

            if(data.startsWith("*")) {
                StringBuilder sb = new StringBuilder();
                for(int i=0; i < dataList.size()-1; i++) {
                    sb.append("");
                    sb.append("&");
                    sb.append(dataList.get(i));
                    sb.append("~");
                    sb.append(dataList.get(i+1));
                    sb.append("/");
                    sb.append("\r\n");
                    i++;
                }

                String chartData = sb.toString();
                Log.e(TAG, "chartData : " + chartData);
                displayChart(chartData);
                barChart.notifyDataSetChanged();
                barChart2.notifyDataSetChanged();
                //displayChart("");
            }
        } else {
            //displayChart("");
        }
    }

    public void displayChart(String data) {
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
                    Log.e(TAG, "tmpStr length : " + tmpStr.length());
                    if(1 < tmpStr.length() && tmpStr.length() < 30) {
                        Log.e(TAG, "tmp1-1 : " + i + " " + tmpStr.substring(17, 21));
                        Log.e(TAG, "tmp1-1 : " + i + " " + tmpStr.substring(4, 8));
                        Log.e(TAG, "tmp1-1 : " + i + " " + String.valueOf(Integer.parseInt(tmpStr.substring(17, 21)) - Integer.parseInt(tmpStr.substring(4, 8))));
                        tmpList1.add(String.valueOf(Integer.parseInt(tmpStr.substring(17, 21)) - Integer.parseInt(tmpStr.substring(4, 8))));
                    }

                    if(0 < i && i < tmpArr.length) {
                        if(1 < tmpStr.length() && tmpStr.length() < 30) {
                            Log.e(TAG, "tmp2-1 : " + i + " " + tmpArr[i - 1].substring(17, 21));
                            Log.e(TAG, "tmp2-1 : " + i + " " + tmpArr[i].substring(17, 21));
                            Log.e(TAG, "tmp2-1 : " + i + " " + String.valueOf(Integer.parseInt(tmpArr[i - 1].substring(17, 21)) - Integer.parseInt(tmpArr[i].substring(4, 8))));
                            tmpList2.add(String.valueOf(Integer.parseInt(tmpArr[i - 1].substring(17, 21)) - Integer.parseInt(tmpArr[i].substring(4, 8))));
                        }
                    }

                    if(1 < tmpArr[i].length() && tmpArr[i].length() < 30) {
                        Log.e("mijierror333", tmpArr[i].substring(26, 28));
                        if(Integer.parseInt(tmpArr[i].substring(26, 28)) > 40) {
                            barList3.add(new BarEntry((float) i, 0f));
                        } else {
                            Float tmp = ((40 - Float.parseFloat(tmpArr[i].substring(26, 28))) / 20) * 100;
                            //Log.i(TAG, "tmpvalue : " + tmp);

                            String tmpFloatStr = String.valueOf(tmp);
                            if(tmpFloatStr.contains(".0")) {
                                tmpFloatStr = tmpFloatStr.substring(0, tmpFloatStr.indexOf("."));
                                //Log.i(TAG, "tmpFloatStrvalue : " + tmpFloatStr);
                            }
                            if(tmpFloatStr.length() == 2) {
                                tmpFloatStr = tmpFloatStr + ".00";
                            } else if(tmpFloatStr.length() == 3) {
                                tmpFloatStr = tmpFloatStr.substring(0, 1) + "." + tmpFloatStr.substring(1, 3);
                            } else if(tmpFloatStr.length() == 4) {
                                tmpFloatStr = tmpFloatStr.substring(0, 2) + "." + tmpFloatStr.substring(2, 4);
                            }

                            tmp = Float.parseFloat(tmpFloatStr);
                            barList3.add(new BarEntry((float) i, tmp));
                        }
                    }
                }

                if(null != tmpList1 && 0 < tmpList1.size()) {
                    for(int i=0; i < tmpList1.size(); i++) {
                        if(tmpList1.get(i).length() == 3) {
                            barList1.add(new BarEntry((float) i, Float.parseFloat(tmpList1.get(i).substring(0, 1) + "." + tmpList1.get(i).substring(1, 3)) * 250));
                        } else if(tmpList1.get(i).length() == 2) {
                            //barList1.add(new BarEntry((float) i, Float.parseFloat("0." + tmpList1.get(i).substring(0, 2)) * 250));
                            if(Integer.parseInt(tmpList1.get(i).substring(0, 2)) < 0) {
                                barList1.add(new BarEntry((float) i, Float.parseFloat("-0." + (Integer.parseInt(tmpList1.get(i).substring(0, 2))) * -1) * 250));
                            } else {
                                barList1.add(new BarEntry((float) i, Float.parseFloat("0." + tmpList1.get(i).substring(0, 2)) * 250));
                            }
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
                                //barList2.add(new BarEntry((float) j, Float.parseFloat("0." + tmpList2.get(j).substring(0, 2)) * 250));
                                if(Integer.parseInt(tmpList2.get(j).substring(0, 2)) < 0) {
                                    barList2.add(new BarEntry((float) j, Float.parseFloat("-0." + (Integer.parseInt(tmpList2.get(j).substring(0, 2))) * -1) * 250));
                                } else {
                                    barList2.add(new BarEntry((float) j, Float.parseFloat("0." + tmpList2.get(j).substring(0, 2)) * 250));
                                }
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

                dataSet.setColor(28254165, 100);
                dataSet2.setColor(78131249, 100);
                dataSet3.setColor(206211214, 100);

                dataSet.setValueTextColor(Color.WHITE);
                dataSet2.setValueTextColor(Color.WHITE);
                dataSet3.setValueTextColor(Color.WHITE);

                dataSetList.add(dataSet);
                dataSetList.add(dataSet2);
                dataSetList2.add(dataSet3);

                BarData chartData = new BarData(dataSet);
                chartData.addDataSet(dataSet2);

                BarData chartData2 = new BarData(dataSet3);

                chartData.setBarWidth(0.3f);
                chartData2.setBarWidth(0.3f);

                //dataSet.setColor(Color.BLUE);
                dataSet.setBarBorderWidth((float) 0.02);
                //dataSet2.setColor(Color.GREEN);
                dataSet2.setBarBorderWidth((float) 0.02);

                //dataSet3.setColor(Color.GRAY);
                dataSet3.setBarBorderWidth((float) 0.02);

                Collections.reverse(xVals1);
                Collections.reverse(xVals2);
                xAxis1.setValueFormatter(new IndexAxisValueFormatter(xVals1));
                xAxis2.setValueFormatter(new IndexAxisValueFormatter(xVals2));

                barChart.setData(chartData);
                barChart.groupBars(0.2f, 0.3f, 0.02f);
                barChart.notifyDataSetChanged();
                barChart.refreshDrawableState();
                barChart.invalidate();


                barChart2.setData(chartData2);
                barChart2.notifyDataSetChanged();
                barChart2.refreshDrawableState();
                barChart2.invalidate();
            }

        } else {
            // 데이터가 없을 경우 아래 샘플 코드로 챠트 생성
            data = "&001000000000~001000000000000/" +
                    "&002000000000~002128750000000/" +
                    "&003028700000~003128700000012/" +
                    "&004028700000~004128700000000/" +
                    "&005028700000~005128700000000/" +
                    "&006028750000~006128700000000/" +
                    "&007028650000~007128650000000/" +
                    "&008000000000~008128750000000/" +
                    "&009028750000~009028700000000/" +
                    "&010028700000~010128650000045/" +
                    "&011028800000~011128650000000/" +
                    "&012028700000~012128700000057/" +
                    "&013000000000~013128700000000/" +
                    "&014000000000~014128700000060/";

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
                            Log.e(TAG, "tmp2-1 : " + i + " " + tmpArr[i - 1].substring(17, 21));
                            Log.e(TAG, "tmp2-1 : " + i + " " + tmpArr[i].substring(17, 21));
                            Log.e(TAG, "tmp2-1 : " + i + " " + String.valueOf(Integer.parseInt(tmpArr[i - 1].substring(17, 21)) - Integer.parseInt(tmpArr[i].substring(4, 8))));

                            tmpList2.add(String.valueOf(Integer.parseInt(tmpArr[i - 1].substring(17, 21)) - Integer.parseInt(tmpArr[i].substring(4, 8))));
                        }

                        Log.e("mijierror333", tmpArr[i].substring(26, 28));
                        if(Integer.parseInt(tmpArr[i].substring(26, 28)) > 40) {
                            barList3.add(new BarEntry((float) i, 0f));
                        } else {
                            Float tmp = ((40 - Float.parseFloat(tmpArr[i].substring(26, 28))) / 20) * 100;
                            Log.i(TAG, "tmpvalue : " + tmp);

                            String tmpFloatStr = String.valueOf(tmp);
                            if(tmpFloatStr.contains(".0")) {
                                tmpFloatStr = tmpFloatStr.substring(0, tmpFloatStr.indexOf("."));
                                Log.i(TAG, "tmpFloatStrvalue : " + tmpFloatStr);
                            }
                            if(tmpFloatStr.length() == 2) {
                                tmpFloatStr = tmpFloatStr + ".00";
                            } else if(tmpFloatStr.length() == 3) {
                                tmpFloatStr = tmpFloatStr.substring(0, 1) + "." + tmpFloatStr.substring(1, 3);
                            } else if(tmpFloatStr.length() == 4) {
                                tmpFloatStr = tmpFloatStr.substring(0, 2) + "." + tmpFloatStr.substring(2, 4);
                            }

                            tmp = Float.parseFloat(tmpFloatStr);
                            barList3.add(new BarEntry((float) i, tmp));
                        }
                    }

                    if(null != tmpList1 && 0 < tmpList1.size()) {
                        for(int i=0; i < tmpList1.size(); i++) {
                            if(tmpList1.get(i).length() == 3) {
                                barList1.add(new BarEntry((float) i, Float.parseFloat(tmpList1.get(i).substring(0, 1) + "." + tmpList1.get(i).substring(1, 3)) * 250));
                            } else if(tmpList1.get(i).length() == 2) {
                                if(Integer.parseInt(tmpList1.get(i).substring(0, 2)) < 0) {
                                    barList1.add(new BarEntry((float) i, Float.parseFloat("-0." + (Integer.parseInt(tmpList1.get(i).substring(0, 2))) * -1) * 250));
                                } else {
                                    barList1.add(new BarEntry((float) i, Float.parseFloat("0." + tmpList1.get(i).substring(0, 2)) * 250));
                                }
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
                                    if(Integer.parseInt(tmpList2.get(j).substring(0, 2)) < 0) {
                                        barList2.add(new BarEntry((float) j, Float.parseFloat("-0." + (Integer.parseInt(tmpList2.get(j).substring(0, 2))) * -1) * 250));
                                    } else {
                                        barList2.add(new BarEntry((float) j, Float.parseFloat("0." + tmpList2.get(j).substring(0, 2)) * 250));
                                    }
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

                    dataSet.setValueTextColor(Color.WHITE);
                    dataSet2.setValueTextColor(Color.WHITE);
                    dataSet3.setValueTextColor(Color.WHITE);

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
                    barChart.invalidate();

                    barChart2.setData(chartData2);
                    barChart2.invalidate();
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