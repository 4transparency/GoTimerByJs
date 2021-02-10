package com.gnuda.gotimerbyjs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Chronometer;


import java.io.IOException;
import java.util.Locale;
import java.util.Timer;
import java.util.logging.LogRecord;

public class MainActivity extends FragmentActivity implements SettingFragment.Callback, TimerFragment.Listener {

    //Fragment
    private SettingFragment mSettingFragment;
    private TimerFragment mTimerFragment;
    private WebViewFragment mWebViewFragment;

    //Data set
    String p1_hour, p1_min, p1_sec;
    String p2_hour, p2_min, p2_sec;
    String cho_read_sec = "10";//기본 피셔 10초 셋팅을 위한 값 설정.
    int cho_read_times_p1 = 0;
    int cho_read_times_p2 = 0;
    String fischer_sec = "10";
    boolean isFischer = true;

    //setting page 에서 세팅 하지 않고 바로 게임 시작 하는 경우르 위한 플레그...
    int applyButtonFlag = -1;

    //stopwatch for total time spent
    Chronometer chronometer;
    long stopTime = 0;
    //another way for stopwatch
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    Handler handler;
    int Seconds, Minutes, MilliSeconds,Hours ;
    boolean isStopWatchOn = false;

    //timer.... 문제가 있다.. 타이머에.. 약간의 오차가 있다.. 그래서. 오랜 시간이.. 흐르게 되면.. 총 대국 시간과 차이가 생긴다... 문제네..
    boolean timerSwitch = false;
    boolean isGameCustomised = false;
    boolean isPlayer1Active = false;
    boolean isGameOn = false;

    CountDownTimer countDownTimer;
    long setTime = 0;
    long setTime_p1 =0;
    long setTime_p2 =0;
    long timePaused_p1=0;
    long timePaused_p2=0;
    String timerMinute;
    String timerSecond;
    String timerHour;

    //total soo
    int total_soo;

    //click sound effect
    MediaPlayer mp1;
    MediaPlayer soundForLessThan10Sec;
    Uri alert;

    //TTS
    TextToSpeech tts;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        total_soo=-1;

        // Create the fragments used by the UI.
        mSettingFragment = new SettingFragment();
        mTimerFragment = new TimerFragment();
        mWebViewFragment = new WebViewFragment();


        // Set the listeners and callbacks of fragment events.
        mSettingFragment.setmCallback(this);
        mTimerFragment.setmListener(this);

        //화면 전환 하기 전에... 그 머시냐.. 기본 디폴트값으로 세팅 하고 들어 가야지...


        //stopwatch setting..
        handler = new Handler();


        //click sound effect
        mp1 = MediaPlayer.create(this, R.raw.go_sound_effect);


        //initial set each player screen for 10 mins....
        setTime_p1=convertToMillsToSetTime("0","10","0");
        setTime_p2=convertToMillsToSetTime("0","10","0");


        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                mTimerFragment).commit();

       //mTimerFragment.setInitialUi("00:10:00","00:10:00","0회, +10","0회, +10",
          //     "총 대국진행 시간 00:00:00","총 대국진행 수순 00:00:00");

        //less than 10 sen left... sound effect
        try {
            setSoundEffectForLess10Sec();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TTS
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int result=tts.setLanguage(Locale.US);
                    /*
                    if(result==TextToSpeech.LANG_MISSING_DATA ||
                            result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("error", "This Language is not supported");
                    }
                    else{
                        ConvertTextToSpeech();
                    }

                     */
                }
                else
                    Log.e("error", "Initilization Failed!");
            }
        });


    }

    @Override
    protected void onPause() {
        if(tts != null){

            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }

    /*
    private void ConvertTextToSpeech() {
        // TODO Auto-generated method stub
        text = et.getText().toString();
        if(text==null||"".equals(text))
        {
            text = "Content not available";
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }else
            tts.speak(text+"is saved", TextToSpeech.QUEUE_FLUSH, null);
    }

     */

    public void setSoundEffectForLess10Sec() throws IOException {
        alert= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if(alert == null){
            // alert is null, using backup
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // I can't see this ever being null (as always have a default notification)
            // but just incase
            if(alert == null) {
                // alert backup is null, using 2nd backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }

        soundForLessThan10Sec = new MediaPlayer();
        soundForLessThan10Sec.setDataSource(this,alert);
        final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            soundForLessThan10Sec.setAudioStreamType(AudioManager.STREAM_ALARM);
            soundForLessThan10Sec.setLooping(true);
            soundForLessThan10Sec.prepare();

        }
    }

    private void switchToFragment(Fragment newFrag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFrag).addToBackStack("tag")
                .commit();
    }


    @Override
    public void onApplyButtonClicked(String p1_hour, String p1_min, String p1_sec, String p2_hour,
                                     String p2_min, String p2_sec, String cho_read_sec, String cho_read_times, boolean isFischer) {
        //applyButtonFlag = 1;

        //switch to control pause action. if customised... set as customised by user...
        isGameCustomised =true;
        isGameOn=false;

        //set both player screen enabled
        mTimerFragment.setPlayerScreenControl(true);

        //to reset timer
        mTimerFragment.setIsPlayer2GameOn(false);
        mTimerFragment.setIsPlayer1GameOn(false);


        this.isFischer=isFischer;



        this.cho_read_sec = cho_read_sec;
        this.cho_read_times_p1 = !isFischer?Integer.parseInt(cho_read_times):0;
        this.cho_read_times_p2 = !isFischer?Integer.parseInt(cho_read_times):0;
        //mTimerFragment.setPlayer1_cho_read(cho_read_times+"회, +"+cho_read_sec);
        //mTimerFragment.setPlayer1_cho_read(cho_read_times+"회, +"+cho_read_sec);



        /*
        this.p1_hour = p1_hour;
        this.p1_min = p1_min;
        this.p1_sec = p1_sec;
        this.p2_hour = p2_hour;
        this.p2_min = p2_min;
        this.p2_sec = p2_sec;
        this.cho_read_sec = cho_read_sec;
        this.cho_read_times = cho_read_times;
        this.fisher_sec = fisher_sec;
        this.isFischer = isFischer;

         */

        Log.i("test", p1_hour +"/>>>>>>>"+ p1_min+"<<<<<<<");

        //switchToFragment(mTimerFragment);

        stopWatchRunnable("stop");
        resetTimer();
        total_soo=0;

        setTime_p1 = convertToMillsToSetTime(p1_hour,p1_min,p2_sec);
        setTime_p2 = convertToMillsToSetTime(p2_hour,p2_min,p2_sec);


        switchToFragment(mTimerFragment);

    }

    @Override
    public void onCancelButtonClicked() {
        onBackPressed();


    }

    @Override
    public void onPlayer1TimerClicked() {
        isGameOn = true;
        isGameCustomised =false;
        isPlayer1Active=false;
        updateColor();

        if(!isStopWatchOn){
            stopWatchRunnable("start");
        }


        if(!mTimerFragment.isPlayer2GameOn()){


            pauseTimer();// to stop p1 timer...

            timerStart(setTime_p2);


                //mTimerFragment.setPlayer2_bag_color(android.R.color.darker_gray);
        }else{
                pauseTimer(); // to stop p1 timer...
                timerRestartWishSetTime();
                //mTimerFragment.setPlayer2_bag_color(R.color.colorAccent);
        }


        mTimerFragment.setIsPlayer2GameOn(true);
        mTimerFragment.showPauseButton();
        isStopWatchOn=true;

        total_soo++;
        mTimerFragment.setTotalSoo("총 대국진행 수순 "+total_soo+"수");

        //audioManager.playSoundEffect(SoundEffectConstants.CLICK);

        //lock player1 screen. and enable player2 screen
        mTimerFragment.setPlayerScreenEnable(isPlayer1Active);

        //sound effect
        mp1.start();
    }


    public void updateColor(){
        if(isPlayer1Active){
            mTimerFragment.setPlayer1_bag_color(R.color.colorAccent);
            mTimerFragment.setPlayer2_bag_color(R.color.background_gray91);
        }else{
            mTimerFragment.setPlayer2_bag_color(R.color.colorAccent);
            mTimerFragment.setPlayer1_bag_color(R.color.background_gray91);
        }
    }

    @Override
    public void onPlayer2TimerClicked() {
        isGameOn = true;
        isGameCustomised =false;
        isPlayer1Active=true;
        updateColor();

        //stopwatch conrol
        if(!isStopWatchOn){
            stopWatchRunnable("start");
        }


        if(!mTimerFragment.isPlayer1GameOn()){

            pauseTimer();// to stop p2 timer...
            timerStart(setTime_p1);
                //mTimerFragment.setPlayer2_bag_color(android.R.color.darker_gray);
        }else{
                Log.i("p2", "screen clicked...`~~ ~~~~~~I expect here...but your are here..nnbb~~~`");
            pauseTimer();//to stop p2 timer...
                timerRestartWishSetTime();
                //mTimerFragment.setPlayer1_bag_color(R.color.colorAccent);
        }



        mTimerFragment.setIsPlayer1GameOn(isPlayer1Active);
        mTimerFragment.showPauseButton();
        isStopWatchOn=true;

        total_soo++;
        mTimerFragment.setTotalSoo("총 대국진행 수순 "+total_soo+"수");


        //lock player2 screen. and enable player1 screen
        mTimerFragment.setPlayerScreenEnable(isPlayer1Active);

        //sound effect
        mp1.start();
    }

    @Override
    public void onSetButtonClicked() {
        switchToFragment(mSettingFragment);
    }

    @Override
    public void onPauseButtonClicked() {
        //pauseTimer();
        countDownTimer.cancel();
        timerSwitch=true;

        if(soundForLessThan10Sec.isPlaying()){
            soundForLessThan10Sec.pause();
        }

        stopWatchRunnable("pause");

        mTimerFragment.updateUiForPlayers1(0);
        mTimerFragment.updateUiForPlayers2(0);

        isStopWatchOn=false;
    }

    @Override
    public void onLinkButtonClicked() {

    }

    //set 버튼을 누르고... 설정을 하던 안하던.. 아래의 코드를 타게 된다...
    @Override
    public void onResumeControl() {

        if(soundForLessThan10Sec.isPlaying()){
            soundForLessThan10Sec.pause();
        }

        //TimerFragment.. onResume UI control

        //each player timer set... like a initial setting... 아래 코드는 게임이 이미 시작한 경우 리줌을 타면 실행
        if(!isGameCustomised && isGameOn){



            mTimerFragment.setPlayer1_timer(millisToString(timePaused_p1));


            mTimerFragment.setPlayer2_timer(millisToString(timePaused_p2));
            //player screen click enable set
            mTimerFragment.setPlayerScreenEnable(isPlayer1Active);
            //total time set
            setTotalTime();
            //total soo set..
            mTimerFragment.setTotalSoo("총 대국진행 수순 "+total_soo+"수");

        }else{
            //유저가 셋 버튼을 누르고 설정 버튼을 눌렀을 때 아래 코드가 실행 된다. 혹은...커스터 마이징 없이 바로 겜 시작할 경우도 실행
            setTimerFragmentCustomUI();
        }

        //어플라이 버튼 눌렀을 때  타이머 스위치 컨트롤 수정 해야함.... 방금... 타이머 포즈에서 스위치 폴스로 변경 했음.. 그에 따라서.. 위에 이프에 걸리나..안 걸리나..

    }

    @Override
    public void onResetButtonClicked() {
        //hope 아래 코드를 실행하면... 아마도..타임 오버가 된 후에 다시 모든 것을 리셋할 수 가 있나... 몰겠다...

        /*
        setTimerFragmentCustomUI();
        stopWatchRunnable("stop");
        total_soo=0;


         */
    }


    @Override
    protected void onResume() {
        super.onResume();
        stopWatchRunnable("stop");
    }

    public void stopWatch(String action){
        switch (action) {
            case "start":
                chronometer.setBase(SystemClock.elapsedRealtime() + stopTime);
                chronometer.start();
                break;
            case "pause":
                stopTime = chronometer.getBase() - SystemClock.elapsedRealtime();
                Log.i("chronometer",String.valueOf(stopTime));

                chronometer.stop();
                break;
            case "stop":
                chronometer.setBase(SystemClock.elapsedRealtime());
                stopTime = 0;
                chronometer.stop();
                break;


        }
    }

    public void stopWatchRunnable(String action){
        switch (action) {
            case "start":
                StartTime = SystemClock.uptimeMillis();
                handler.postDelayed(runnable, 0);
                break;
            case "pause":
                TimeBuff += MillisecondTime;

                handler.removeCallbacks(runnable);
                break;
            case "stop":
                MillisecondTime = 0L ;
                StartTime = 0L ;
                TimeBuff = 0L ;
                UpdateTime = 0L ;
                Seconds = 0 ;
                Minutes = 0 ;
                MilliSeconds = 0 ;

                mTimerFragment.setTotalTime("총 대국진행 시간 0:00:00");
                break;


        }
    }

    //set UI when user customise time_setting.... and also default setting...
    public void setTimerFragmentCustomUI(){

        mTimerFragment.setPlayer1_timer(millisToString(setTime_p1));
        mTimerFragment.setPlayer2_timer(millisToString(setTime_p2));
        mTimerFragment.setPlayer1_cho_read(cho_read_times_p1+"회, +"+cho_read_sec);
        mTimerFragment.setPlayer2_cho_read(cho_read_times_p2+"회, +"+cho_read_sec);

        mTimerFragment.setUiForGameReady();
    }

    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            setTotalTime();

            handler.postDelayed(this, 0);
        }

    };

    public void setTotalTime(){
        UpdateTime = TimeBuff + MillisecondTime;

        Seconds = (int) (UpdateTime / 1000);

        Minutes = Seconds / 60;

        Hours = Minutes /60;

        Minutes = Minutes % 60;

        Seconds = Seconds % 60;



        mTimerFragment.setTotalTime("총 대국진행 시간 "+ Hours + ":" + twoDigitString(Minutes) + ":"
                + String.format("%02d", Seconds) );
    }

    public long convertToMillsToSetTime(String hour, String min, String sec){
        int hours = Integer.parseInt(hour);
        int mins = Integer.parseInt(min);
        int secs = Integer.parseInt(sec);
        long millis = secs*1000 + mins*60*1000 + hours*60*60*1000;
        return millis;
    }

    public void timerStart(final long timerSet){

        if(timerSwitch){
            countDownTimer = new CountDownTimer(timerSet, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {


                    if(isPlayer1Active){
                        mTimerFragment.setPlayer1_timer(millisToString(millisUntilFinished));
                        //store time data for later use
                        timePaused_p1 =  millisUntilFinished;
                        if(millisUntilFinished < 10*1000){
                            mTimerFragment.updateUiForPlayers1(3);
                            soundForLessThan10Sec.start();
                        }

                    }else{
                        mTimerFragment.setPlayer2_timer(millisToString(millisUntilFinished));
                        timePaused_p2 =  millisUntilFinished;
                        if(millisUntilFinished < 10*1000){
                            mTimerFragment.updateUiForPlayers2(3);
                            soundForLessThan10Sec.start();
                        }

                    }
                }

                @Override
                public void onFinish() {
                    resetTimer();

                    if(soundForLessThan10Sec.isPlaying()){
                        soundForLessThan10Sec.pause();
                    }

                    if(!isFischer && isPlayer1Active && cho_read_times_p1>0){
                       cho_read_times_p1--;
                       String cho_read = String.valueOf(cho_read_times_p1);
                       tts.speak(cho_read + "times left",TextToSpeech.QUEUE_FLUSH,null);
                       mTimerFragment.setPlayer1_cho_read(cho_read_times_p1+"회, +"+cho_read_sec);
                       timerStart(Integer.parseInt(cho_read_sec)*1000);


                    }else if(!isFischer && !isPlayer1Active && cho_read_times_p2>0){
                        cho_read_times_p2--;
                        String cho_read = String.valueOf(cho_read_times_p2);
                        tts.speak(cho_read + "times left",TextToSpeech.QUEUE_FLUSH,null);
                        mTimerFragment.setPlayer2_cho_read(cho_read_times_p2+"회, +"+cho_read_sec);
                        timerStart(Integer.parseInt(cho_read_sec)*1000);

                    }else{

                        mTimerFragment.updateUiForPlayers1(4);

                        mTimerFragment.updateUiForPlayers2(4);




                        stopWatchRunnable("pause");

                        tts.speak("time over",TextToSpeech.QUEUE_FLUSH,null);

                        mTimerFragment.setUiForTimeOver();


                    }



                }
            }.start();

            timerSwitch = false;


        }else{
            pauseTimer();
        }
    }

    public String millisToString(long millisUntilFinished){
        int seconds = (int) (millisUntilFinished / 1000) % 60;
        int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
        int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);

        String curTimer = String.valueOf(hours) + ":" + twoDigitString(minutes) + ":"
                + twoDigitString(seconds);
        return curTimer;
    }

    public void resetTimer() {
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }

        //timeLeftTextView.setText("1min");
        //textView.setText("00:00");
        timerSwitch = true;
    }

    public void pauseTimer(){

        if(countDownTimer !=null){
            countDownTimer.cancel();
        }

        if(isGameOn && soundForLessThan10Sec.isPlaying()){
            soundForLessThan10Sec.pause();
        }

        //뭔가 불충분 하다.... 피셔 방식으로 하면... 어찌 됏든. .정해진 시간 만큰.. 흐른 시간에 더해 주는 것인데...
        if(isGameOn && isFischer){


            if(!isPlayer1Active && timePaused_p1!=0){ //timePaused_p1!=0 요 조건 추가... 타이머 시작할 때 타이머를 시작한 플레이어 타이머가..피셔 설정 초로 설정되는  오류 수정 위해
                timePaused_p1+=Integer.parseInt(cho_read_sec)*1000;
                mTimerFragment.setPlayer1_timer(millisToString(timePaused_p1));
            }else if(timePaused_p2 != 0){
                timePaused_p2+=Integer.parseInt(cho_read_sec)*1000;
                mTimerFragment.setPlayer2_timer(millisToString(timePaused_p2));
            }
        }


        timerSwitch = true;
    }

    public void timerRestartWishSetTime(){

        if(isPlayer1Active){
            timerStart(timePaused_p1);
        }else{
            timerStart(timePaused_p2);
        }
    }

    private String twoDigitString(long number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }





}