package tw.org.iii.brad.brad05;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private boolean isRunning;
    private Button leftBtn, rightBtn;   //必須在setContentView後才能找到,故無法在此assign
    private Timer timer = new Timer();
    private int hs;
    private Counter counter;
    private TextView clock;
    private UIHandler uiHandler = new UIHandler();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        leftBtn = findViewById(R.id.leftBtn);
        rightBtn = findViewById(R.id.rightBtn);
        clock = findViewById(R.id.clock);
        listView = findViewById(R.id.listview);

        changeDisplay();
        clock.setText(parseHS(hs));
        initLap();
    }

    private void initLap(){
        
    }

    private void changeDisplay(){
        leftBtn.setText(isRunning?"LAP":"RESET");
        rightBtn.setText(isRunning?"STOP":"START");
    }

    public void doLeft(View view) {
        if(isRunning){
            // LAP
            doLap();
        }else{
            // RESET
            doReset();
        }
    }

    private void doReset(){
        hs = 0;
        clock.setText(parseHS(hs));
    }

    private void doLap() {

    }

    public void doRight(View view) {
        isRunning = !isRunning; // wow cool
        changeDisplay();

        if(isRunning) {
            counter = new Counter();
            timer.schedule(counter, 10, 10);
        }else{
            counter.cancel();
            counter = null;
        }

    }

    private class Counter extends TimerTask {
        @Override
        public void run() {
            hs++;
            uiHandler.sendEmptyMessage(0);  //此處沒有要帶的訊息,故只要送empty
        }
    }

    private class UIHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            clock.setText(parseHS(hs));
        }
    }

    private static String parseHS(int hs){
        int phs = hs % 100; //小數點後
        int ts = hs / 100;  //總秒數
        int hh = ts / (60*60);
        int mm = (ts - hh*60*60) / 60;
        int ss = ts % 60;

        return String.format("%s:%s:%s:%s",
                (hh<10?"0"+hh:hh),
                (mm<10?"0"+mm:mm),
                (ss<10?"0"+ss:ss),
                (phs<10?"0"+phs:phs));
    }

    @Override
    public void finish() {
        super.finish(); //程式真正終結的地方,又因method內有執行序,故code先後順序沒差
        if(timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }
}
