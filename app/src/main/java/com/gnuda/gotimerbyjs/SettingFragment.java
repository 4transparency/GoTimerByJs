package com.gnuda.gotimerbyjs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.RadioButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText p1_hour;
    EditText p1_min;
    EditText p1_sec;
    EditText p2_hour;
    EditText p2_min;
    EditText p2_sec;
    EditText cho_read_sec;
    EditText cho_read_times;

    RadioButton cho_read_radioButton;
    RadioButton fischer_radioButton;



    String p1_hour_string,p1_min_string, p1_sec_string;
    String p2_hour_string,p2_min_string, p2_sec_string;
    String cho_read_sec_string, cho_read_times_string;
    boolean isFischer;






    interface Callback{
        void onApplyButtonClicked(String p1_hour, String p1_min, String p1_sec,
                                 String p2_hour, String p2_min, String p2_sec,
                                 String cho_read_sec, String cho_read_times, boolean isFischer);
        void onCancelButtonClicked();

    }

    private Callback mCallback = null;


    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);



        final int[] clickableIds = {
          R.id.applyButton,
          R.id.cancelButton,
                R.id.cho_read_radioButton,
                R.id.fischer_radioButton
        };

        for (int clickableId : clickableIds){
            rootView.findViewById(clickableId).setOnClickListener(this);
        }

        p1_hour = rootView.findViewById(R.id.p1_hour);
        p1_hour.setFilters(new InputFilter[]{new MinMaxFilter(0,9)});
        p1_min = rootView.findViewById(R.id.p1_min);
        p1_min.setFilters(new InputFilter[]{new MinMaxFilter(0,59)});
        p1_sec = rootView.findViewById(R.id.p1_sec);
        p1_sec.setFilters(new InputFilter[]{new MinMaxFilter(0,59)});
        p2_hour = rootView.findViewById(R.id.p2_hour);
        p2_hour.setFilters(new InputFilter[]{new MinMaxFilter(0,9)});
        p2_min = rootView.findViewById(R.id.p2_min);
        p2_min.setFilters(new InputFilter[]{new MinMaxFilter(0,59)});
        p2_sec = rootView.findViewById(R.id.p2_sec);
        p2_sec.setFilters(new InputFilter[]{new MinMaxFilter(0,59)});





        cho_read_sec = rootView.findViewById(R.id.cho_read_sec);
        cho_read_sec.setFilters(new InputFilter[]{new MinMaxFilter(0,60)});
        cho_read_times = rootView.findViewById(R.id.cho_read_times);
        cho_read_times.setFilters(new InputFilter[]{new MinMaxFilter(1,10)});



        cho_read_radioButton = rootView.findViewById(R.id.cho_read_radioButton);
        fischer_radioButton = rootView.findViewById(R.id.fischer_radioButton);




        return rootView;
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.cho_read_radioButton:
                if (checked)
                    cho_read_sec.setEnabled(true);
                    cho_read_times.setEnabled(true);
                    break;
            case R.id.fischer_radioButton:
                if (checked)
                    cho_read_sec.setEnabled(true);
                    cho_read_sec.requestFocus();
                    cho_read_times.setText("7");
                    cho_read_times.setEnabled(false);

                    break;
        }
    }

    public void setmCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }



    public void setData(){
        p1_hour_string = !p1_hour.getText().toString().equals("")?p1_hour.getText().toString():"0";
        p1_min_string = !p1_min.getText().toString().equals("")?p1_min.getText().toString():"0";
        p1_sec_string = !p1_sec.getText().toString().equals("")?p1_sec.getText().toString():"0";
        p2_hour_string = !p2_hour.getText().toString().equals("")?p2_hour.getText().toString():"0";
        p2_min_string = !p2_min.getText().toString().equals("")?p2_min.getText().toString():"0";
        p2_sec_string = !p2_sec.getText().toString().equals("")?p2_sec.getText().toString():"0";

        cho_read_sec_string = !cho_read_sec.getText().toString().equals("")?cho_read_sec.getText().toString():"0";
        cho_read_times_string = !cho_read_times.getText().toString().equals("")?cho_read_times.getText().toString():"0";

        isFischer = fischer_radioButton.isChecked();

        Log.i("inSetFrag", p1_hour_string+"/"+p1_sec_string+"<<<<");



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.applyButton:
                //setting apply button action.. maybe go to timer page??
                setData();
                Log.i("onClick", p1_hour_string+"/"+p1_sec_string+"<<<<");
                mCallback.onApplyButtonClicked(p1_hour_string,p1_min_string,p1_sec_string,
                        p2_hour_string,p2_min_string,p2_sec_string,
                        cho_read_sec_string, cho_read_times_string,isFischer);
                break;
            case R.id.cancelButton:
                //아무튼... 세팅 취소면.. 입력한 데이터에 상관 없이 다시.. 타이머 화면으로 가야 되나?? 웅 맞아...
                mCallback.onCancelButtonClicked();
                break;
            case R.id.cho_read_radioButton:
                if (cho_read_radioButton.isChecked())
                    cho_read_sec.setEnabled(true);
                cho_read_times.setEnabled(true);
                break;
            case R.id.fischer_radioButton:
                if (fischer_radioButton.isChecked())
                    cho_read_sec.setEnabled(true);
                cho_read_times.setEnabled(false);
                break;

        }
    }







}