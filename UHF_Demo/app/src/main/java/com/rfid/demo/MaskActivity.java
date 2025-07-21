package com.rfid.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ubx.usdk.RFIDSDKManager;

public class MaskActivity extends Activity implements View.OnClickListener {
    private String TAG = MaskActivity.class.getSimpleName();
    private EditText tvAddr;
    private EditText tvLen;
    private EditText tvData;
    private Spinner spMem;
    private Button btAdd;
    private Button btClear;
    TextView txt_mask;




    private String[] strMem =new String[3];
    private ArrayAdapter<String> spada_Mem;
    String MaskData="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mask);
        tvAddr = (EditText)findViewById(R.id.et_addr);
        tvLen = (EditText)findViewById(R.id.et_len);
        tvData = (EditText)findViewById(R.id.et_data);
        txt_mask = (TextView)findViewById(R.id.txt_mask);
        strMem[0] = "EPC";
        strMem[1] = "TID";
        strMem[2] = "USER";
        spMem=(Spinner)findViewById(R.id.mem_spinner);
        spada_Mem = new ArrayAdapter<String>(MaskActivity.this,
                android.R.layout.simple_spinner_item, strMem);
        spada_Mem.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMem.setAdapter(spada_Mem);
        spMem.setSelection(0,false);

        btAdd = (Button)findViewById(R.id.button_add);
        btClear = (Button)findViewById(R.id.button_clear);

        btAdd.setOnClickListener(this);
        btClear.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v==btAdd)
        {
            if((tvAddr.getText()==null)||(tvLen.getText()==null)||(tvData.getText()==null))return;
            try {
                int maskAddr = (int)Integer.valueOf(tvAddr.getText().toString());
                int maskMem = spMem.getSelectedItemPosition()+1;
                int maskLen = (int)Integer.valueOf(tvLen.getText().toString());
                String strData = tvData.getText().toString();
                  int ret =   RFIDSDKManager.getInstance().getRfidManager().addMask(maskMem,maskAddr,maskLen,strData);
                   if (ret == 0) {
                       String temp = maskMem + "," + maskAddr + "," + maskLen + "," + strData;
                       MaskData += (temp + "\r\n");
                       txt_mask.setText(MaskData);
                       Toast.makeText(
                               getApplicationContext(),
                               getString(R.string.strsuccess),
                               Toast.LENGTH_SHORT).show();
                   }else {
                       Toast.makeText(
                               getApplicationContext(),
                               getString(R.string.strfailed)+" "+ret,
                               Toast.LENGTH_SHORT).show();
                   }
            }catch (Exception ex)
            {
                Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.strfailed),
                        Toast.LENGTH_SHORT).show();
            }

        }
        else if(v==btClear)
        {
                MaskData="";
                txt_mask.setText("");
                RFIDSDKManager.getInstance().getRfidManager().clearMask();
                Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.strsuccess),
                        Toast.LENGTH_SHORT).show();

        }

    }
}