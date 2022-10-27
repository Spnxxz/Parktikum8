package com.example.prakmodul7;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class MainActivity extends AppCompatActivity {
    private ImageView imgSlot1;
    private ImageView imgSlot2;
    private ImageView imgSlot3;
    private Button btnGet;
    private TextView tvHasil;
    private String state = "GET";
    private SlotTask slotTask1, slotTask2, slotTask3;
    private ExecutorService executorService;
    ArrayList<String> arrayUrl = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnGet = findViewById(R.id.btn_get);
        imgSlot1 = findViewById(R.id.img_slot1);
        imgSlot2 = findViewById(R.id.img_slot2);
        imgSlot3 = findViewById(R.id.img_slot3);
        tvHasil = findViewById(R.id.tv_hasil);
        slotTask1 = new SlotTask(imgSlot1);
        slotTask2 = new SlotTask(imgSlot2);
        slotTask3 = new SlotTask(imgSlot3);
        executorService = Executors.newFixedThreadPool(3);
        ExecutorService execGetImage =
                Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (state){
                    case "GET":
                        execGetImage.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    final String txt =

                                            loadStringFromNetwork("https://mocki.io/v1/821f1b13-fa9a-43aa-ba9a9e328df8270e");
                                    try {
                                        JSONArray jsonArray = new
                                                JSONArray(txt);
                                        for (int i = 0; i <
                                                jsonArray.length();
                                             i++) {
                                            JSONObject jsonObject =

                                                    jsonArray.getJSONObject(i);

                                            arrayUrl.add(jsonObject.getString("url"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Glide.with(MainActivity.this).load(arrayUrl.get(0)).into(imgSlot1);
                                            Glide.with(MainActivity.this).load(arrayUrl.get(1)).into(imgSlot2);
                                            Glide.with(MainActivity.this).load(arrayUrl.get(2)).into(imgSlot3);
                                            tvHasil.setText(txt);
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        state = "PLAY";
                        btnGet.setText("Play");
                        break;
                    case "PLAY":
                        slotTask1.play = true;
                        slotTask2.play = true;
                        slotTask3.play = true;
                        executorService.execute(slotTask1);
                        executorService.execute(slotTask2);
                        executorService.execute(slotTask3);
                        state = "STOP";
                        btnGet.setText("Stop");
                        break;
                    case "STOP":
                        slotTask1.play = false;
                        slotTask2.play = false;
                        slotTask3.play = false;
                        state = "PLAY";
                        btnGet.setText("Play");
                        break;
                }
            }
        });
    }
    private String loadStringFromNetwork(String s) throws IOException {
        final URL myUrl = new URL(s);
        final InputStream in = myUrl.openStream();
        final StringBuilder out = new StringBuilder();
        final byte[] buffer = new byte[1024];
        try {
            for (int ctr; (ctr = in.read(buffer)) != -1; ) {
                out.append(new String(buffer, 0, ctr));
            }
        } catch (IOException e) {
            throw new RuntimeException("Gagal mendapatkan text",
                    e);
        }
        final String yourFileAsAString = out.toString();
        return yourFileAsAString;
    }
    class SlotTask implements Runnable{
        private ImageView slotImage;
        private Random random;
        public boolean play = true;
        public SlotTask(ImageView slotImage) {
            this.slotImage = slotImage;
            random = new Random();
        }
        @Override
        public void run() {
            while (play){
                int index = random.nextInt(3);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(MainActivity.this).load(arrayUrl.get(index)).into(slotIm age);
                    }
                });
                try {
                    Thread.sleep(random.nextInt(500));
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
