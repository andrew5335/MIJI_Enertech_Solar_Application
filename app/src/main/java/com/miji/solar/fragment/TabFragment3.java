package com.miji.solar.fragment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    private ImageView refresh;
    private TextView sumData;

    private String sendRefresh = CommandConstants.sendRefresh;
    private String requestData = CommandConstants.sendRequest;

    private Button saveData;
    private Button loadData;

    private String data;
    private String data2;
    private String data3;
    private String dataSave;

    private TextView updateTime;

    private static final String foldername = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/miji");

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
        //loadData = root.findViewById(R.id.load_data);
        saveData = root.findViewById(R.id.save_data);
        refresh = root.findViewById(R.id.refresh);
        updateTime = root.findViewById(R.id.updateTime);

        refresh.setOnClickListener(this);
        saveData.setOnClickListener(this);
        //loadData.setOnClickListener(this);

        if(null != bundle) {
            //Log.i(TAG, bundle.getString("data"));
            //Toast.makeText(getContext(), "tab3 data1 : " + bundle.getString("data"), Toast.LENGTH_LONG).show();
            data = bundle.getString("data3");
            setData(data);
        } else {
            setData("");
        }

        return root;
        //return inflater.inflate(R.layout.fragment_tab3, container, false);
    }

    public void setData(String data) {
        //Toast.makeText(getContext(), "tab3 data2 : " + data, Toast.LENGTH_LONG).show();
        if(null != data && !"".equals(data) && 0 < data.length()) {
            data = data.replaceAll("&", "");
            String[] tmpArr = data.split("/");
            StringBuilder sb = new StringBuilder();

            if(null != tmpArr && 0 < tmpArr.length) {
                for(int i=0; i < tmpArr.length; i++) {
                    sb.append(i + 1 + "   ");
                    sb.append(tmpArr[i]);
                    sb.append("\r\n");
                }
            }
            data = sb.toString();
            dataSave = data;

            sumData.setText("");
            sumData.setText(data);
            sumData.setMovementMethod(new ScrollingMovementMethod());
            sumData.setTextColor(Color.WHITE);
        } else {
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

            data = data.replaceAll("&", "");
            String[] tmpArr = data.split("/");
            StringBuilder sb = new StringBuilder();

            if(null != tmpArr && 0 < tmpArr.length) {
                for(int i=0; i < tmpArr.length; i++) {
                    sb.append(i + 1 + "   ");
                    sb.append(tmpArr[i]);
                    sb.append("\r\n");
                }
            }
            data = sb.toString();
            dataSave = data;

            sumData.setText("");
            sumData.setText(data);
            sumData.setMovementMethod(new ScrollingMovementMethod());
            sumData.setTextColor(Color.WHITE);
        }
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

                /**
            case R.id.load_data:
                mijiMain.sendData2(requestData);
                Log.e(TAG, "data request");
                break;
                 **/

            case R.id.save_data:
                saveData();
                Log.i(TAG, "save data");
                break;
                
        }
    }

    public void saveData() {
        String data = sumData.getText().toString();
        String result = "";

        if(null != data && !"".equals(data) && 0 < data.length()) {
            result = writeData(data);

            if(null != result && !"".equals(result) && 0 < result.length()) {
                if("success".equals(result)) {
                    Toast.makeText(getActivity(), "데이터 저장 성공", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "데이터 저장 실패", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public String writeData(String str) {
        String result = "fail";
        String fileName = "";

        if(null != str && !"".equals(str) && 0 < str.length()) {
            File file = new File(foldername);
            if(!file.exists()) {
                file.mkdir();
            }

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            fileName = "mijiData_" + sdf.format(date);

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(foldername + "/" + fileName + ".txt", true);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
                bufferedWriter.write(str);
                bufferedWriter.flush();
                bufferedWriter.close();

                result = "success";
            } catch(Exception e) {
                Log.e(TAG, "Error : 파일 생성 실패" + e.toString());
            }
        }

        return result;
    }
}