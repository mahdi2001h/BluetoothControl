package ir.mahdi2001h.bluetoothIOT;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import adapters.PersonListClickListener;
import adapters.RecycleViewDevice;
import adapters.RecycleViewLcd;
import me.aflak.bluetooth.Bluetooth;

public class MainActivity extends AppCompatActivity implements ServiceConnection, SerialListener {

    Bluetooth bluetooth;
    Button b1, b2, b3, b4;
    ImageButton turnRight, turnLeft, forward, backward;
    TextView txMessage, txColor ,tx_alert_message;
    View viewColor;
    RecycleViewDevice DeviceAdapter;
    RecyclerView deviceList;
    PersonListClickListener listListener;
    AlertDialog alertDialog;
    View alert_view;
    ArrayList<String> log = new ArrayList<>();
    RecyclerView re_log;
    RecycleViewLcd lcdAdapter;
    ImageView more;
    BluetoothDevice BDevice;
    SerialSocket socket;
    SerialService service;
    boolean initialStart = true;
    enum Connected {False, Pending, True}
    Connected connected = Connected.False;
    String newline = "\r\n";
    String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(MainActivity.this, SerialService.class));

        init();
        showDevices();

        more.setOnClickListener(v -> {
            View view = getLayoutInflater().inflate(R.layout.alert_about, null, false);
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setView(view)
                    .create();
            alertDialog.show();

        });

        b1.setOnClickListener(view -> send("action_1"));

        b2.setOnClickListener(view -> send("action_2"));

        b3.setOnClickListener(view -> send("action_3"));

        b4.setOnClickListener(view -> send("action_4"));

        turnRight.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                send("turnRight-1");
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                send("turnRight-0");
            }

            return false;
        });

        turnLeft.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                send("turnLeft-1");
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                send("turnLeft-0");
            }

            return false;
        });

        forward.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                send("forward-1");
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                send("forward-0");
            }

            return false;
        });

        backward.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                send("backward-1");
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                send("backward-0");
            }

            return false;
        });
    }

    private void init() {
        alert_view = getLayoutInflater().inflate(R.layout.alert_device_list, null, false);
        lcdAdapter = new RecycleViewLcd(this, log);
        deviceList = alert_view.findViewById(R.id.list);

        re_log = findViewById(R.id.re_log);
        re_log.setLayoutManager(new LinearLayoutManager(this));
        re_log.setAdapter(lcdAdapter);
        tx_alert_message = alert_view.findViewById(R.id.tx_alert_message);

        more = findViewById(R.id.img_more);

        txMessage = findViewById(R.id.tx_message);
        txColor = findViewById(R.id.tx_color);
        viewColor = findViewById(R.id.view_color);

        b1 = findViewById(R.id.btn_1);
        b2 = findViewById(R.id.btn_2);
        b3 = findViewById(R.id.btn_4);
        b4 = findViewById(R.id.btn_3);

        turnRight = findViewById(R.id.btn_forward);
        turnLeft = findViewById(R.id.btn_backward);
        forward = findViewById(R.id.btn_upward);
        backward = findViewById(R.id.btn_downward);

        listListener = (view, device) -> {
            BDevice = device;
            connect();
            Toast.makeText(MainActivity.this, "لطفا کمی صبر کنید...", Toast.LENGTH_SHORT).show();
        };

        List<BluetoothDevice> bList = new ArrayList<>(BluetoothAdapter.getDefaultAdapter().getBondedDevices());
        DeviceAdapter = new RecycleViewDevice(MainActivity.this, bList, listListener);


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        //      bluetooth.onStop();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bluetooth.onActivityResult(requestCode, resultCode);
    }

    private void showDevices() {
        //devices = bluetooth.getPairedDevices();
        deviceList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        deviceList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        deviceList.setAdapter(DeviceAdapter);
        alertDialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setView(alert_view)
                .create();
        alertDialog.show();
    }

    private void send(String str) {
        if (connected != Connected.True) {
            Toast.makeText(this, getString(R.string.not_connect), Toast.LENGTH_SHORT).show();
            return;
        }
        try {

            byte[] data = (str + newline).getBytes();
            socket.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        if (initialStart) {
            initialStart = false;
            this.runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    private void connect() {
        try {
            BluetoothDevice device = BDevice;
            String deviceName = device.getName() != null ? device.getName() : device.getAddress();
            Log.d(TAG, "connect: ");
            connected = Connected.Pending;
            socket = new SerialSocket();

            socket.connect(this, this, device);
        } catch (Exception e) {
            Log.e(TAG, "connect error: " + e.toString());
        }
    }

    private void disconnect() {
        connected = Connected.False;
        socket.disconnect();
        socket = null;

    }

    @Override
    public void onSerialConnect() {
        alertDialog.cancel();
        Toast.makeText(MainActivity.this, getString(R.string.connected), Toast.LENGTH_SHORT).show();
        connected = Connected.True;


    }

    @Override
    public void onSerialConnectError(Exception e) {
        Log.e(TAG, "onSerialConnectError: " + e);
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        runOnUiThread(() -> {
            //fixme (should be separate)
            txMessage.setText(data.toString());
            //txColor.setText(data.toString());

        });
    }

    @Override
    public void onSerialIoError(Exception e) {
        Log.e(TAG, "onSerialIoError: " + e);
        Toast.makeText(MainActivity.this, getString(R.string.disconnected), Toast.LENGTH_SHORT).show();
        disconnect();
    }

}


