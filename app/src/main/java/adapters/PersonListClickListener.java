package adapters;


import android.bluetooth.BluetoothDevice;
import android.view.View;

public interface PersonListClickListener {
  void onClick(View view, BluetoothDevice position);
}
