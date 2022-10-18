package com.miji.solar.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miji.solar.MijiMainActivity;
import com.miji.solar.R;
import com.miji.solar.constant.CommandConstants;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TabFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabFragment2 extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String TAG = "miji";
    private String ret$1, ret$2, ret$3,ret$4, ret$5;
    private String reta1, reta2, reta3, reta4, reta5;
    private EditText batteryVoltage, batteryCharge, solarVoltage, solarAmp, solarWatt;
    private Button lampOn, lampOff, lampTest;
    private ImageView refresh;

    private String sendRefresh = CommandConstants.sendRefresh;
    private String sendOn = CommandConstants.sendOn;
    private String sendOff = CommandConstants.sendOff;
    private String sendTest = CommandConstants.sendTest;
    private String data;
    private String data2;
    private String data3;
    private String data4;
    private String data5;
    private String data6;
    private TextView updateTime, cell1, cell2, cell3, cell4, cell5, cell6, cell7, temperature;

    private MijiMainActivity mijiMain;

    private Bundle bundle;

    public TabFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabFragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static TabFragment2 newInstance(String param1, String param2) {
        TabFragment2 fragment = new TabFragment2();
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

        //if(null != savedInstanceState) {
        //    bundle = savedInstanceState;
        //} else {
            bundle = getArguments();
        //}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_tab2, container, false);
        mijiMain = (MijiMainActivity) getActivity();
        LinearLayout frag1Linear = root.findViewById(R.id.frag2linear);
        batteryVoltage = root.findViewById(R.id.battery_voltage);
        batteryCharge = root.findViewById(R.id.battery_charge);
        solarVoltage = root.findViewById(R.id.solar_voltage);
        solarAmp = root.findViewById(R.id.solar_amp);
        solarWatt = root.findViewById(R.id.solar_watt);
        lampOn = root.findViewById(R.id.lamp_on);
        lampOff = root.findViewById(R.id.lamp_off);
        lampTest = root.findViewById(R.id.lamp_test);
        refresh = root.findViewById(R.id.refresh);
        updateTime = root.findViewById(R.id.updateTime);
        cell1 = root.findViewById(R.id.cell1);
        cell2 = root.findViewById(R.id.cell2);
        cell3 = root.findViewById(R.id.cell3);
        cell4 = root.findViewById(R.id.cell4);
        cell5 = root.findViewById(R.id.cell5);
        cell6 = root.findViewById(R.id.cell6);
        cell7 = root.findViewById(R.id.cell7);
        temperature = root.findViewById(R.id.temperature);

        frag1Linear.setOnClickListener(this);
        refresh.setOnClickListener(this);
        lampOn.setOnClickListener(this);
        lampOff.setOnClickListener(this);
        lampTest.setOnClickListener(this);

        if(null != bundle) {
            //Log.i(TAG, bundle.getString("data"));
            //Toast.makeText(getContext(), "tab2 data1 : " + bundle.getString("data"), Toast.LENGTH_LONG).show();

            data = bundle.getString("data");
            data2 = bundle.getString("data2");
            data3 = bundle.getString("data4");
            data4 = bundle.getString("data5");
            data5 = bundle.getString("data6");
            data6 = bundle.getString("data7");

            checkChange(data, data2, data3, data4, data5, data6);
        }

        return root;
    }

    public void checkChange(String data, String data2, String data3, String data4, String data5, String data6) {
        //Toast.makeText(getContext(), "tab2 data2 : " + data, Toast.LENGTH_LONG).show();
        if(null != data && !"".equals(data) && 0 < data.length()) {
            if (data.startsWith("$")) {
                String[] dataArr = data.split("/");

                if (null != dataArr && 0 < dataArr.length) {
                    try {
                        ret$1 = dataArr[0];
                        ret$2 = dataArr[1];
                        ret$3 = dataArr[2];
                        ret$4 = dataArr[3];
                        ret$5 = dataArr[4];
                    } catch (Exception e) {
                        Log.d(TAG, "e " + e);
                    }

                    ret$1 = ret$1.trim();
                    ret$2 = ret$2.trim();
                    ret$3 = ret$3.trim();
                    ret$4 = ret$4.trim();
                    ret$5 = ret$5.trim();

                    if (ret$4.length() == 4) {
                        batteryVoltage.setText(ret$4.substring(0, 2) + "." + ret$4.substring(2, ret$4.length()));
                    } else if (ret$4.length() == 3) {
                        batteryVoltage.setText(ret$4.substring(0, 1) + "." + ret$4.substring(1, ret$4.length()));
                    } else if (ret$4.length() == 2) {
                        batteryVoltage.setText("00." + ret$4);
                    } else if (ret$4.length() == 1) {
                        batteryVoltage.setText(("00.0" + ret$4));
                    }

                    batteryCharge.setText(ret$5);
                }
            }
        }

        if(null != data2 && !"".equals(data2) && 0 < data2.length()) {
            if(data2.startsWith("@")) {
                String[] dataArr2 = data2.split("/");

                if(null != dataArr2 && 0 < dataArr2.length) {
                    try {
                        reta1 = dataArr2[0];
                        reta2 = dataArr2[1];
                        reta3 = dataArr2[2];
                    } catch(Exception e) {
                        Log.e(TAG, "Error : " + e.toString());
                    }

                    reta1 = reta1.trim();
                    reta2 = reta2.trim();
                    reta3 = reta3.trim();

                    if(reta1.contains("@")) {
                        reta1 = reta1.replace("@", "");
                    }

                    if(reta1.length() == 4) {
                        solarVoltage.setText(reta1.substring(0, 2) + "." + reta1.substring(2, reta1.length()));
                    } else if(reta1.length() == 3) {
                        solarVoltage.setText(reta1.substring(0, 1) + "." + reta1.substring(1, reta1.length()));
                    } else if(reta1.length() == 2) {
                        solarVoltage.setText("00." + reta1);
                    } else if(reta1.length() == 1) {
                        solarVoltage.setText("00.0" + reta1);
                    }

                    if(reta2.length() == 4) {
                        solarAmp.setText(reta2.substring(0, 2) + "." + reta2.substring(2, reta2.length()));
                    } else if(reta2.length() == 3) {
                        solarAmp.setText(reta2.substring(0, 1) + "." + reta2.substring(1, reta2.length()));
                    } else if(reta2.length() == 2) {
                        solarAmp.setText("00." + reta2);
                    } else if(reta2.length() == 1) {
                        solarAmp.setText("00.0" + reta2);
                    }

                    if(reta3.length() == 5) {
                        solarWatt.setText(reta3.substring(0, 3) + "." + reta3.substring(3, reta3.length()));
                    } else if(reta3.length() == 4) {
                        if(reta2.length() == 4) {
                            solarWatt.setText(reta3.substring(0, 3) + "." + reta3.substring(3, reta3.length()));
                        } else {
                            solarWatt.setText(reta3.substring(0, 2) + "." + reta3.substring(2, reta3.length()));
                        }
                    } else if(reta3.length() == 3) {
                        if(reta2.length() == 4) {
                            solarWatt.setText(reta3.substring(0, 2) + "." + reta3.substring(2, reta3.length()));
                        } else {
                            solarWatt.setText(reta3.substring(0, 1) + "." + reta3.substring(1, reta3.length()));
                        }
                    } else if(reta3.length() == 2) {
                        solarWatt.setText("00." + reta3);
                    } else if(reta3.length() == 1) {
                        solarWatt.setText("00.0" + reta3);
                    }
                }
            }
        }

        if(null != data3 && !"".equals(data3) && 0 < data3.length()) {
            String[] tmpArr = data3.split("/");

            if(null != tmpArr && 0 < tmpArr.length) {
                if(tmpArr[0].contains("#")) {
                    tmpArr[0] = tmpArr[0].replaceAll("#", "");
                }
                cell1.setText(tmpArr[0]);
                cell2.setText(tmpArr[1]);
            }
        }

        if(null != data5 && !"".equals(data5) && 0 < data5.length()) {
            String[] tmpArr = data5.split("/");

            if(null != tmpArr && 0 < tmpArr.length) {
                cell3.setText(tmpArr[0]);
                cell4.setText(tmpArr[1]);
            }
        }

        if(null != data4 && !"".equals(data4) && 0 < data4.length()) {
            String[] tmpArr = data4.split("/");

            if(null != tmpArr && 0 < tmpArr.length) {
                if(tmpArr[0].contains("%")) {
                    tmpArr[0] = tmpArr[0].replaceAll("%", "");
                }
                cell5.setText(tmpArr[0]);
                cell6.setText(tmpArr[1]);
            }
        }

        if(null != data6 && !"".equals(data6) && 0 < data6.length()) {
            String[] tmpArr = data6.split("/");

            if(null != tmpArr && 0 < tmpArr.length) {
                cell7.setText(tmpArr[0]);
                temperature.setText(tmpArr[1]);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.lamp_on:
                //((MijiMainActivity) getActivity()).sendData2(sendRefresh);
                mijiMain.sendData2(sendOn);
                Log.e(TAG, "lamp on click");
                break;

            case R.id.lamp_off:
                mijiMain.sendData2(sendOff);
                Log.e(TAG, "lamp off click");
                break;

            case R.id.lamp_test:
                mijiMain.sendData2(sendTest);
                Log.e(TAG, "lamp Test click");
                break;

            case R.id.refresh:
                mijiMain.sendData2(sendRefresh);
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
                String now = sdf.format(date);
                updateTime.setText("업데이트 시각 : " + now);
                Log.e(TAG, "refresh click");
                break;

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("data", data);
    }
}