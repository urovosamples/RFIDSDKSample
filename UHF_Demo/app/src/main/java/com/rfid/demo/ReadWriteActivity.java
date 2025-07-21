package com.rfid.demo;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.rfid.demo.utils.MsgShow;
import com.rfid.demo.utils.ToastUtils;
import com.rfid.demo.utils.Util;
import com.ubx.usdk.RFIDSDKManager;
import com.ubx.usdk.bean.TagResult;
import com.ubx.usdk.rfid.util.RfidErrorConstants;


public class ReadWriteActivity extends Activity implements OnClickListener, OnItemSelectedListener{

	private String TAG = ReadWriteActivity.class.getSimpleName();
	private int mode;
	EditText edENum0;
	int selectedEd = 3;
	int selectedWhenPause = 0;
	
	Spinner c_mem;
	Spinner lock_mem;
	Spinner lock_type;
	EditText c_wordPtr;
	EditText c_len;
	EditText c_pwd;
	EditText c_kwd;
	EditText c_ptr;
	TextView tvResult;
	
	EditText b_id;
	EditText b_addr;
	EditText b_num;
	
	EditText content;
	EditText readContent;
	Button rButton;
	Button wButton;
	Button btWriteEPC;
	Button btLock;
	Button btKill;
	Button btLed;
	Button btErase;
	CheckBox u9lock ;
	//private ArrayAdapter<String> spada_epc;
	//Spinner spepc;
	TextView epcText,tidText;
	private static final int CHECK_W_6B = 0;
	private static final int CHECK_R_6B = 1;
	private static final int CHECK_W_6C = 2;
	private static final int CHECK_R_6C = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
//				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_read_write);
		initView();
	}
	
	@Override
	protected void onResume() {
		epcText.setText(ScanActivity.epc);
		tidText.setText(ScanActivity.tid);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void initView(){
		//edENum0 = (EditText)findViewById(R.id.epc0);
		epcText = (TextView)findViewById(R.id.epc_text);
		tidText = (TextView)findViewById(R.id.tid_text);
		tvResult = (TextView)findViewById(R.id.rw_result);	
		c_mem = (Spinner)findViewById(R.id.mem_spinner);
		ArrayAdapter<CharSequence> adapter =  ArrayAdapter.createFromResource(this, R.array.men_select, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		c_mem.setAdapter(adapter); 
		c_mem.setSelection(1, true);
		c_mem.setOnItemSelectedListener(this);

		lock_mem = (Spinner)findViewById(R.id.lockmem_spinner);
		ArrayAdapter<CharSequence> adapter1 =  ArrayAdapter.createFromResource(this, R.array.arrayLockMem, android.R.layout.simple_spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		lock_mem.setAdapter(adapter1);
		lock_mem.setSelection(4, true);

		lock_type = (Spinner)findViewById(R.id.locktype_spinner);
		ArrayAdapter<CharSequence> adapter2 =  ArrayAdapter.createFromResource(this, R.array.arrayLock, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		lock_type.setAdapter(adapter2);
		lock_type.setSelection(2, true);
		u9lock = (CheckBox)findViewById(R.id.checku9);


		c_wordPtr = (EditText)findViewById(R.id.et_wordptr);
		c_wordPtr.setText("2");
		c_len = (EditText)findViewById(R.id.et_length);
		c_len.setText("6");
		c_pwd = (EditText)findViewById(R.id.et_pwd);
		c_pwd.setText("00000000");
		c_kwd = (EditText)findViewById(R.id.et_kwd);
		content = (EditText)findViewById(R.id.et_content_6c);
		readContent =  (EditText)findViewById(R.id.et_read_6c);
		rButton = (Button)findViewById(R.id.button_read_6c);
		wButton = (Button)findViewById(R.id.button_write_6c);
		btWriteEPC = (Button)findViewById(R.id.button_write_epc);
		btKill = (Button)findViewById(R.id.button_kill);
		btLock = (Button)findViewById(R.id.button_lock);
		btLed = (Button)findViewById(R.id.button_led);
		btErase = findViewById(R.id.button_erase);
		rButton.setOnClickListener(this);
		wButton.setOnClickListener(this);	
		btWriteEPC.setOnClickListener(this);
		btKill.setOnClickListener(this);
		btLock.setOnClickListener(this);
		btLed.setOnClickListener(this);
		btErase.setOnClickListener(this);
	}
	

	@SuppressLint("ResourceAsColor")
	@Override
	public void onClick(View view) {
		String tidStr = tidText.getText().toString();
		if(view == wButton){
			if(!checkContent(CHECK_W_6C))return;
			try{
				int result = 0x30;
				String strEPC="";
				if(epcText.getText()!=null)
				{
					strEPC = epcText.getText().toString();
				}


				if(content.getText()==null) return;
				int Mem = c_mem.getSelectedItemPosition();
				int WordPtr = Integer.valueOf(c_wordPtr.getText().toString());
				int Num =  Integer.valueOf(c_len.getText().toString());
				String Password =c_pwd.getText().toString();
				String strData =content.getText().toString();
				Log.i(TAG, "onClick:   strEPC : "+strEPC+"   Password : "+Password+"  strData : "+strData);

					result = RFIDSDKManager.getInstance().getRfidManager().writeTag(strEPC,Password,Mem,WordPtr,strData);
					Log.i(TAG, "writeTag: "+strData);
//

				//普通模式  001234567814
				//隐私模式  011234567815
//				RFIDSDKManager.getInstance().getRfidManager().protectedMode("E2801191A503006340974800",1,"12345678");
//				RFIDSDKManager.getInstance().getRfidManager().setCfgParameter(  1, 14,"011234567809",6);


				//WNum	 	要写入存储区的字数
				//WordPtr	写入的起始字地址
//				result = RFIDSDKManager.getInstance().getRfidManager().maskWriteTag(Num,strData,Mem,WordPtr,-1,Password,1,12,2,"25");

				if(result!=0){
					MsgShow.writelogErr(getString(R.string.write_failed)+" "+result, tvResult);
					ToastUtils.show(""+ RfidErrorConstants.getDescription(result));
				}else{
					MsgShow.writelogSuc(getString(R.string.write_success), tvResult);
				}
			}catch(Exception ex)
			{
                MsgShow.writelogErr(getString(R.string.write_failed), tvResult);}
			
			
		}else if(view == rButton){
			if(!checkContent(CHECK_R_6C))return;
			try{

				String strEPC ="";
				if(epcText.getText()!=null)
				{
					strEPC = epcText.getText().toString();
				}

				int memBank = (byte)c_mem.getSelectedItemPosition();
				int wordCnt =  Integer.valueOf(c_len.getText().toString());
				int startAdd = Integer.valueOf(c_wordPtr.getText().toString());
				String password =c_pwd.getText().toString();


				TagResult tagResult = RFIDSDKManager.getInstance().getRfidManager().readTag(strEPC,memBank,startAdd,wordCnt,password);
				Log.i(TAG, "readTag: "+tagResult.code);

				//通过TID
				//普通模式  001234567814
				//隐私模式  011234567815
//				RFIDSDKManager.getInstance().getRfidManager().setCfgParameter(1,14,"001234567808",6);

				if(tagResult.code != 0){
					readContent.setText("");
					MsgShow.writelogErr(getString(R.string.get_failed) +" "+tagResult.code, tvResult);
					ToastUtils.show(""+ RfidErrorConstants.getDescription(tagResult.code));
				}
				else{
					readContent.setText(tagResult.data+"");
					MsgShow.writelogSuc(getString(R.string.get_success), tvResult);
				}
			}catch(Exception ex)
			{
				MsgShow.writelogErr(getString(R.string.get_failed), tvResult);
			}
		}
		else if(view == btWriteEPC)
		{
			if(!checkContent(CHECK_W_6C))return;
			try{
				int result = 0x30;
				String Password = c_pwd.getText().toString();
				String strData =content.getText().toString();

				String strEPC ="";
				if(epcText.getText()!=null)
				{
					strEPC = epcText.getText().toString();
				}

				result = RFIDSDKManager.getInstance().getRfidManager().writeTagEpc(strEPC,Password,strData);
//				result = RFIDSDKManager.getInstance().getRfidManager().writeEpcString(strData,Password);

				if(result!=0){
					MsgShow.writelogErr(getString(R.string.write_failed)+" "+result, tvResult);
					ToastUtils.show(""+ RfidErrorConstants.getDescription(result));
				}
				else{
					MsgShow.writelogSuc(getString(R.string.write_success), tvResult);
				}
			}catch(Exception ex)
			{
                MsgShow.writelogErr(getString(R.string.write_failed), tvResult);}

//		int	result = RFIDSDKManager.getInstance().getRfidManager().setWorkRegion("EST");
//			int	result = RFIDSDKManager.getInstance().getRfidManager().setWorkRegion("CHN");
//			int	result = RFIDSDKManager.getInstance().getRfidManager().setInventoryWithStartQvalue(16);
//			Toast.makeText(this, "result : "+result, Toast.LENGTH_SHORT).show();
		}
		else if(view == btKill)
		{
			try{
				int result = 0x30;
				String Password = c_kwd.getText().toString();
				if(Password==null || Password.length()!=8)return;
				String strEPC ="";
				if(epcText.getText()!=null)
				{
					strEPC = epcText.getText().toString();
				}

					result = RFIDSDKManager.getInstance().getRfidManager().killTag(strEPC,Password);
					Log.i(TAG, "killTag: "+result);

				if(result!=0){
					MsgShow.writelogErr(getString(R.string.kill_failed)+" "+result, tvResult);
					ToastUtils.show(""+ RfidErrorConstants.getDescription(result));
				}
				else{
					MsgShow.writelogSuc(getString(R.string.kill_success), tvResult);
				}
			}catch(Exception ex)
			{
                MsgShow.writelogErr(getString(R.string.kill_failed), tvResult);}
		}
		else if(view == btLock)
		{
			try{
				int result = 0x30;
				String PasswordStr = c_pwd.getText().toString();
				String strEPC ="";
				if(epcText.getText()!=null)
				{
					strEPC = epcText.getText().toString();
				}
				int select =   (lock_mem.getSelectedItemPosition());
				int setprotect =   (lock_type.getSelectedItemPosition());
				if(u9lock.isChecked())
				{
					Log.i(TAG, "lockTag  U9 ");
					select =  255;
					setprotect =  255;
				}
					result = RFIDSDKManager.getInstance().getRfidManager().lockTag(strEPC,PasswordStr,select,setprotect);
					Log.i(TAG, "lockTag: "+result);


				if(result!=0){
					MsgShow.writelogErr(getString(R.string.lock_failed)+" "+result, tvResult);
					ToastUtils.show(""+ RfidErrorConstants.getDescription(result));
				}
				else{
					MsgShow.writelogSuc(getString(R.string.lock_success), tvResult);
				}
			}catch(Exception ex)
			{
                MsgShow.writelogErr(getString(R.string.lock_failed), tvResult);}
		}
		else if(view == btLed)
		{
			try{
				int result=0x30;
				String PasswordStr = c_pwd.getText().toString();
				String strEPC="";
				if(epcText.getText()!=null)
				{
					strEPC = epcText.getText().toString();
				}
				result = RFIDSDKManager.getInstance().getRfidManager().lightUpLedTag(strEPC,PasswordStr,25500);//最大 25500ms
				if(result!=0){
					MsgShow.writelogErr(getString(R.string.Optfailed), tvResult);
				}
				else{
					MsgShow.writelogSuc(getString(R.string.Optsuccess), tvResult);
				}
			}catch(Exception ex)
			{
				MsgShow.writelogErr(getString(R.string.Optfailed), tvResult);
			}

//			int  ret = RFIDSDKManager.getInstance().getRfidManager().getInventoryWithStartQvalue();
//			Toast.makeText(this, "Qvalue : "+ret , Toast.LENGTH_SHORT).show();

//			WorkRegion workRegion = RFIDSDKManager.getInstance().getRfidManager().getWorkRegion();
//			if (workRegion == null){
//				Toast.makeText(this, "返回为null" , Toast.LENGTH_SHORT).show();
//			}else {
//				Toast.makeText(this, "Region : "+workRegion.getRegion()+"    Band : "+workRegion.getBand()+"    MaxPower : "+workRegion.getMaxPower(), Toast.LENGTH_SHORT).show();
//			}

		}else if(view == btErase){
			if(!checkContent(CHECK_R_6C))return;
//			try{
//				int retEarse = -1;
//
//				String strEPC ="";
//				if(epcText.getText()!=null)
//				{
//					strEPC = epcText.getText().toString();
//				}
//				int Mem = (byte)c_mem.getSelectedItemPosition();
//				int Num =  Integer.valueOf(c_len.getText().toString());
//				int WordPtr = Integer.valueOf(c_wordPtr.getText().toString());
//				String Password =c_pwd.getText().toString();
//
//					retEarse =  (strEPC,Password,Mem,WordPtr,Num);
//					Log.i(TAG, "eraseTag: "+retEarse);
//
//				if(retEarse != 0){
//					MsgShow.writelogErr(getString(R.string.get_failed)+ "  "+retEarse, tvResult);
//				}
//				else{
//					MsgShow.writelogSuc(getString(R.string.get_success), tvResult);
//				}
//			}catch(Exception ex)
//			{
//				MsgShow.writelogErr(getString(R.string.get_failed), tvResult);
//			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		selectedEd = position;
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	private boolean checkContent(int check){
		switch (check) {
		case CHECK_W_6C:
			if(Util.isEtEmpty(content)) return Util.showWarning(this,R.string.content_empty_warning);
			if((content.getText().toString().length()%4)!=0)
				return Util.showWarning(this,R.string.length_content_warning);
			if(!(Util.isLenLegal(content)))
				return Util.showWarning(this,R.string.str_lenght_odd_warning);
			if(!(Util.isLenLegal(c_pwd)))
				return Util.showWarning(this,R.string.str_lenght_odd_warning);
		case CHECK_R_6C:
			if(Util.isEtEmpty(c_wordPtr)) return Util.showWarning(this,R.string.wordptr_empty_warning);
			if(Util.isEtEmpty(c_len)) return Util.showWarning(this,R.string.length_empty_warning);
			if(Util.isEtEmpty(c_pwd)) return Util.showWarning(this,R.string.pwd_empty_warning);
			
			if(!(Util.isLenLegal(c_pwd)))
				return Util.showWarning(this,R.string.str_lenght_odd_warning);
			
			break;
		default:
			break;
		}
		return true;
	}
	
	public String bytesToHexString(byte[] src, int offset, int length) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = offset; i < length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() == 1) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

	public byte[] hexStringToBytes(String hexString) {  
        if (hexString == null || hexString.equals("")) {  
            return null;  
        }  
        hexString = hexString.toUpperCase();  
        int length = hexString.length() / 2;  
        char[] hexChars = hexString.toCharArray();  
        byte[] d = new byte[length];  
        for (int i = 0; i < length; i++) {  
            int pos = i * 2;  
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
        }  
        return d;  
    }   
    private byte charToByte(char c) {  
        return (byte) "0123456789ABCDEF".indexOf(c);  
    } 
}
