package com.miji.solar.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.miji.solar.MijiMainActivity;
import com.miji.solar.R;
import com.miji.solar.constant.CommandConstants;
import com.miji.solar.databinding.FragmentMijiMainBinding;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private FragmentMijiMainBinding binding;

    private String TAG = "miji";
    private Button refresh;
    private MijiMainActivity mijiMain;
    private String sendRefresh = CommandConstants.sendRefresh;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {


        binding = FragmentMijiMainBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.sectionLabel;
        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        /**
        int pos = getArguments().getInt(ARG_SECTION_NUMBER);
        View root = null;

        if (pos == 0) {
            root = inflater.inflate(R.layout.fragment_tab1, container, false);
        } else if(pos == 1) {
            root = inflater.inflate(R.layout.fragment_tab2, container, false);
        } else if(pos == 2) {
            root = inflater.inflate(R.layout.fragment_tab3, container, false);
        } else if(pos == 3) {
            root = inflater.inflate(R.layout.fragment_tab4, container, false);
        }
         **/

        mijiMain = (MijiMainActivity) getActivity();
        refresh = root.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mijiMain.sendData2(sendRefresh);
                Log.e(TAG, "refresh click");
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {

    }
}