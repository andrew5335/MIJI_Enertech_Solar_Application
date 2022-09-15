package com.miji.solar.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.miji.solar.MijiMainActivity;
import com.miji.solar.R;
import com.miji.solar.constant.CommandConstants;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TabFragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabFragment1 extends Fragment implements View.OnClickListener {

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
    private Button refresh;

    private MijiMainActivity mijiMain;

    private String sendRefresh = CommandConstants.sendRefresh;

    private View root;
    private Bundle bundle;

    public TabFragment1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabFragment1.
     */
    // TODO: Rename and change types and number of parameters
    public static TabFragment1 newInstance(String param1, String param2) {
        TabFragment1 fragment = new TabFragment1();
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
        root = inflater.inflate(R.layout.fragment_tab1, container, false);
        mijiMain = (MijiMainActivity) getActivity();
        LinearLayout frag1Linear = root.findViewById(R.id.frag1linear);
        refresh = root.findViewById(R.id.refresh);
        circleStatus = root.findViewById(R.id.status);
        statusVeryGood = root.findViewById(R.id.status_very_good);
        statusGood = root.findViewById(R.id.status_good);
        statusBad = root.findViewById(R.id.status_bad);
        statusPower = root.findViewById(R.id.status_power);
        statusCharge = root.findViewById(R.id.status_charge);
        statusOverVoltage = root.findViewById(R.id.status_over_voltage);
        statusLowVoltage = root.findViewById(R.id.status_low_voltage);

        frag1Linear.setOnClickListener(this);
        refresh.setOnClickListener(this);

        if(null != bundle) {
            Log.i(TAG, bundle.getString("data"));
            Toast.makeText(getContext(), "tab1 data1 : " + bundle.getString("data"), Toast.LENGTH_LONG).show();
            changeStatus(bundle.getString("data"));
        }

        return root;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.refresh:
                //((MijiMainActivity) getActivity()).sendData2(sendRefresh);
                mijiMain.sendData2(sendRefresh);
                Log.e(TAG, "refresh click");
                break;
        }
    }

    public void changeStatus(String data) {
        if(null != data && 0 < data.length()) {
            // 전달받은 데이터가 있을 경우 화면 변경 처리
            Log.e(TAG, data);
            Toast.makeText(getContext(), "tab1 data2 : " + bundle.getString("data"), Toast.LENGTH_LONG).show();
            if(null != data && !"".equals(data) && 0 < data.length()) {
                if(data.startsWith("$")) {
                    // 인입된 데이터가 $ 로 시작하면 화면 처리
                    String[] dataArr = data.split("/");

                    if(null != dataArr && 0 < dataArr.length) {
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

                        // 메인 상태와 파워 상태 변경
                        if (ret$1.equals("$0")) {
                            circleStatus.setImageResource(R.mipmap.m1);
                            statusPower.setImageResource(R.mipmap.m2);
                        } else if(ret$1.equals("$1")) {
                            circleStatus.setImageResource(R.mipmap.m2);
                            statusPower.setImageResource(R.mipmap.m2);
                        } else if(ret$1.equals("$2")) {
                            circleStatus.setImageResource(R.mipmap.m3);
                            statusPower.setImageResource(R.mipmap.m2);
                        } else {
                            circleStatus.setImageResource(R.mipmap.m0);
                            statusPower.setImageResource(R.mipmap.m0);
                        }

                        // 충전 상태 변경
                        if(ret$2.equals("1")) {
                            statusCharge.setImageResource(R.mipmap.m2);
                        } else if(ret$2.equals("0")) {
                            statusCharge.setImageResource(R.mipmap.m0);
                        }

                        // high, log voltage 상태 변경
                        if(ret$3.equals("0")) {
                            statusOverVoltage.setImageResource(R.mipmap.m0);
                            statusLowVoltage.setImageResource(R.mipmap.m0);
                        } else if(ret$3.equals("1")) {
                            statusOverVoltage.setImageResource(R.mipmap.m3);
                            statusLowVoltage.setImageResource(R.mipmap.m0);
                        } else if(ret$3.equals("2")) {
                            statusOverVoltage.setImageResource(R.mipmap.m0);
                            statusLowVoltage.setImageResource(R.mipmap.m3);
                        }

                     }
                }
            }
        } else {
            // 전달받은 데이터가 없을 경우에는 화면 변경하지 않음
        }
    }
}