package com.miji.solar.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.miji.solar.MijiMainActivity;
import com.miji.solar.R;
import com.miji.solar.constant.CommandConstants;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TabFragment3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabFragment3 extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MijiMainActivity mijiMain;

    private String TAG = "miji";

    private Bundle bundle;

    private Button refresh;
    private EditText sumData;

    private String sendRefresh = CommandConstants.sendRefresh;

    private Button saveData;
    private Button loadData;

    private String data;
    private String data2;
    private String data3;

    public TabFragment3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabFragment3.
     */
    // TODO: Rename and change types and number of parameters
    public static TabFragment3 newInstance(String param1, String param2) {
        TabFragment3 fragment = new TabFragment3();
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
        View root = inflater.inflate(R.layout.fragment_tab3, container, false);
        mijiMain = (MijiMainActivity) getActivity();
        LinearLayout frag1Linear = root.findViewById(R.id.frag3linear);
        sumData = root.findViewById(R.id.sum_data);
        loadData = root.findViewById(R.id.load_data);
        saveData = root.findViewById(R.id.save_data);
        refresh = root.findViewById(R.id.refresh);

        if(null != bundle) {
            Log.i(TAG, bundle.getString("data"));
            Toast.makeText(getContext(), "tab3 data1 : " + bundle.getString("data"), Toast.LENGTH_LONG).show();
            data = bundle.getString("data3");
            setData(data);
        }

        return root;
        //return inflater.inflate(R.layout.fragment_tab3, container, false);
    }

    public void setData(String data) {
        Toast.makeText(getContext(), "tab3 data2 : " + data, Toast.LENGTH_LONG).show();
        if(null != data && !"".equals(data) && 0 < data.length()) {
            sumData.setText("");
            sumData.setText(data);
        }
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
}