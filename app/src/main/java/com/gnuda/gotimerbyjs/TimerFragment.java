package com.gnuda.gotimerbyjs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimerFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    TextView player1_timer;
    TextView player1_cho_read;
    TextView player2_timer;
    TextView player2_cho_read;
    TextView totalTime;
    TextView totalSoo;
    TextView reset_button;
    View player1_screen;
    View player2_screen;

    ImageButton pauseButton;
    Button setButton;
    Button linkButton;

    boolean isPlayer1GameOn = false;
    boolean isPlayer2GameOn = false;

    //gameState  0 - ready|paused,  1-active, 2-paused, 3-초읽기... 4 - finished

    int gameState;





    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    interface Listener{
        void onPlayer1TimerClicked();
        void onPlayer2TimerClicked();
        void onSetButtonClicked();
        void onPauseButtonClicked();
        void onLinkButtonClicked();
        void onResumeControl();
        void onResetButtonClicked();
    }

    private Listener mListener = null;


    public TimerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimerFragment newInstance(String param1, String param2) {
        TimerFragment fragment = new TimerFragment();
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

        View rootView = inflater.inflate(R.layout.fragment_timer, container, false);

        final int[] clickableIds = new int[]{
                R.id.player1_screen,
                R.id.player2_screen,
                R.id.set_button,
                R.id.pause_button,
                R.id.webView_link_button,
                R.id.reset_button
        };

        for (int clickableId : clickableIds) {
            rootView.findViewById(clickableId).setOnClickListener(this);

        }

        player1_timer = rootView.findViewById(R.id.p1_timer);
        player1_cho_read = rootView.findViewById(R.id.p1_cho_read);
        player2_timer = rootView.findViewById(R.id.p2_timer);
        player2_cho_read = rootView.findViewById(R.id.p2_cho_read);
        totalTime = rootView.findViewById(R.id.total_time);
        //totalTime.animate().rotation(90);
        totalSoo = rootView.findViewById(R.id.total_soo);

        player1_screen = rootView.findViewById(R.id.player1_screen);
        player2_screen = rootView.findViewById(R.id.player2_screen);

        pauseButton = rootView.findViewById(R.id.pause_button);
        setButton = rootView.findViewById(R.id.set_button);
        linkButton = rootView.findViewById(R.id.webView_link_button);


        reset_button = rootView.findViewById(R.id.reset_button);



        return rootView;
    }

    public void setInitialUi(String player1_timer, String player2_timer, String p1_cho_read, String p2_cho_read, String time, String soo){
        this.player1_timer.setText(player1_timer);
        this.player2_timer.setText(player2_timer);
        this.player1_cho_read.setText(p1_cho_read);
        this.player2_cho_read.setText(p2_cho_read);
        this.totalTime.setText(time);
        this.totalSoo.setText(soo);
    }

    public void showPauseButton(){
        if(isPlayer1GameOn || isPlayer2GameOn){
            pauseButton.setVisibility(View.VISIBLE);
            setButton.setVisibility(View.INVISIBLE);
            linkButton.setVisibility(View.INVISIBLE);
            reset_button.setTextColor(getResources().getColor(R.color.background_black));
            reset_button.setClickable(false);
        }else{
            pauseButton.setVisibility(View.INVISIBLE);
            setButton.setVisibility(View.VISIBLE);
            linkButton.setVisibility(View.VISIBLE);
        }
    }

    public void setUiForGameReady(){
        player2_screen.setClickable(true);
        player1_screen.setClickable(true);
        reset_button.setTextColor(getResources().getColor(R.color.background_black));
        reset_button.setClickable(false);
    }

    public void setUiForTimeOver(){
        pauseButton.setVisibility(View.INVISIBLE);
        setButton.setVisibility(View.VISIBLE);
        linkButton.setVisibility(View.VISIBLE);
        reset_button.setTextColor(getResources().getColor(R.color.white));
        reset_button.setClickable(true);
        player2_screen.setClickable(false);
        player1_screen.setClickable(false);
        setIsPlayer1GameOn(false);
        setIsPlayer2GameOn(false);
    }


    public void updateUiForPlayers1(int gameState){
        switch (gameState){
            case 0:
                //ready
                player1_screen.setBackgroundResource(R.color.background_gray91);
                //아래 세줄 코드... 왜 있지.. 궁금했다.. 내가 썼는데.. 왜 p1에만 내가 써 노은거지..그런데..이유는.. 게임중 포오즈 버튼을 눌렀을때를 위한..것이였다..
                pauseButton.setVisibility(View.INVISIBLE);
                setButton.setVisibility(View.VISIBLE);
                linkButton.setVisibility(View.VISIBLE);
                break;
            case 1:
                //active
                player1_screen.setBackgroundResource(R.color.colorAccent);
                break;
            case 2:
                //paused
                player1_screen.setBackgroundResource(R.color.background_gray91);
                break;
            case 3:
                //초 읽기
                player1_screen.setBackgroundResource(R.color.red);
                break;
            case 4:
                //finished
                player1_screen.setBackgroundResource(R.color.colorPrimary);
                break;

        }
    }

    public void updateUiForPlayers2(int gameState){
        switch (gameState){
            case 0:
                //ready
                player2_screen.setBackgroundResource(R.color.background_gray91);
                break;
            case 1:
                //active
                player2_screen.setBackgroundResource(R.color.colorAccent);
                break;
            case 2:
                //paused
                player2_screen.setBackgroundResource(R.color.background_gray91);
                break;
            case 3:
                //초 읽기
                player2_screen.setBackgroundResource(R.color.red);
                break;
            case 4:
                //finished
                player2_screen.setBackgroundResource(R.color.colorPrimary);
                break;

        }
    }

    public void setIsPlayer1GameOn(boolean isGameOn){
        this.isPlayer1GameOn = isGameOn;
    }

    public void setIsPlayer2GameOn(boolean isGameOn){
        this.isPlayer2GameOn = isGameOn;
    }

    public boolean isPlayer1GameOn() {
        return isPlayer1GameOn;
    }

    public boolean isPlayer2GameOn() {
        return isPlayer2GameOn;
    }

    public void setPlayer1_bag_color(int color){
        player1_screen.setBackgroundResource(color);

    }
    public void setPlayer2_bag_color(int color){
        //처음에 setBackgroundColor 로 했는데.. 화면이 그냥 블랙으로...
        //아래 코드로 바꾸고 나서... 내가 원하는데로 작동... 그러니까.. values...colors.xml .. 에 설정한 컬러 정보를 쓰려면 요거 했어야 했어요..
        player2_screen.setBackgroundResource(color);
    }

    public void setPlayer1_timer(String timer){
        player1_timer.setText(timer);
    }
    public void setPlayer2_timer(String timer){
        player2_timer.setText(timer);
    }
    //to prevent double click...
    public void setPlayerScreenEnable(boolean isOn){
        player1_screen.setEnabled(isOn);
        player2_screen.setEnabled(!isOn);
    }

    public void setPlayerScreenControl(boolean isBothOn){
        player1_screen.setEnabled(isBothOn);
        player2_screen.setEnabled(isBothOn);
    }



    public void setPlayer1_cho_read(String cho_read){
        player1_cho_read.setText(cho_read);
    }
    public void setPlayer2_cho_read(String cho_read){
        player2_cho_read.setText(cho_read);
    }
    public void setTotalTime(String time){
        totalTime.setText(time);
    }
    public void setTotalSoo(String soo){
        totalSoo.setText(soo);
    }

    public void setmListener(Listener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.player1_screen:
                mListener.onPlayer1TimerClicked();
                break;
            case R.id.player2_screen:
                mListener.onPlayer2TimerClicked();
                break;
            case R.id.set_button:
                mListener.onSetButtonClicked();
                break;
            case R.id.pause_button:
                mListener.onPauseButtonClicked();
                break;
            case R.id.webView_link_button:
                mListener.onLinkButtonClicked();
                break;
            case R.id.reset_button:
                mListener.onResetButtonClicked();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.onResumeControl();
    }
}