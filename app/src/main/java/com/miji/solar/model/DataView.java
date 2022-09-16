package com.miji.solar.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DataView extends ViewModel {

    public MutableLiveData<Data> strData = new MutableLiveData<Data>();

    public void sendData(String data1, String data2, String data3) {
        Data data = new Data();
        data.setData1(data1);
        data.setData2(data2);
        data.setData3(data3);

        strData.setValue(data);
    }

    public LiveData<Data> getData() {
        return strData;
    }

}
