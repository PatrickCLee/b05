package tw.org.iii.brad.brad05;
//計時器
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private boolean isRunning;
    private Button leftBtn, rightBtn;   //必須在setContentView後才能找到,故無法在此assign
    private Timer timer = new Timer();
    private int hs; //百分位秒數 亦即0.01秒
    private Counter counter;
    private TextView clock;
    private UIHandler uiHandler = new UIHandler();

    private ListView listView;
    private SimpleAdapter adapter;      //建構式無法在此做(無法new
    private LinkedList<HashMap<String,String>> data = new LinkedList<>();
    private String[] from = {"lap","time1","time2"};        //此處名稱隨意
    private int[] to = {R.id.lap_rank,R.id.lap_time1,R.id.lap_time2};   //要對到元件
    private int lapCounter;
    private int lastHs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        leftBtn = findViewById(R.id.leftBtn);
        rightBtn = findViewById(R.id.rightBtn);
        clock = findViewById(R.id.clock);
        listView = findViewById(R.id.listview);

        changeDisplay();
        clock.setText(parseHS(hs)); //初始設定上方時間顯示為0
        initLap();
    }

    private void initLap(){
        adapter = new SimpleAdapter(this,data,R.layout.layout_lap,from,to);
        listView.setAdapter(adapter);
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
        lastHs = 0;
        lapCounter = 0;
        data.clear();
        adapter.notifyDataSetChanged();
        clock.setText(parseHS(hs));
    }

    private void doLap() {
        int dHs = hs - lastHs;
        lastHs = hs;
        HashMap<String,String> row = new HashMap<>();
        row.put(from[0],"lap" + ++lapCounter);
        row.put(from[1],parseHS(dHs));
        row.put(from[2],parseHS(hs));
        data.add(0,row);
        adapter.notifyDataSetChanged();
    }

    public void doRight(View view) {
        isRunning = !isRunning; // wow cool
        changeDisplay();

        if(isRunning) {
            counter = new Counter();    //每按一次start就一個新的計數任務
            timer.schedule(counter, 10, 10);
        }else{
            counter.cancel();   //按下stop則取消,(週期任務被取消後無法重新開始跑)
            counter = null;     //並回歸到null
        }

    }

    private class Counter extends TimerTask {
        @Override
        public void run() {
            hs++;
            uiHandler.sendEmptyMessage(0);  //此處沒有要帶的訊息,故只要送empty
        }
    }

    private class UIHandler extends Handler {   //因為要呈現執行序的計數所以需要一個Handler
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            clock.setText(parseHS(hs));
        }
    }

    private static String parseHS(int hs){
        int phs = hs % 100; //小數點後  (100個hs即為1秒,故餘數就是小數點後)
        int ts = hs / 100;  //總秒數
        int hh = ts / (60*60);  //一小時共60*60秒
        int mm = (ts - hh*60*60) / 60;  //一分鐘為總秒數去掉(要拿去當hh的秒數)後,除以60
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