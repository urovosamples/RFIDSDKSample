package com.rfid.demo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.rfid.demo.utils.MsgShow;
import com.ubx.usdk.RFIDSDKManager;
import com.ubx.usdk.bean.enums.EXChipType;
import com.ubx.usdk.bean.enums.FrequencyRegion;
import com.ubx.usdk.bean.enums.InventorySceneMode;
import com.ubx.usdk.bean.enums.QueryMemBank;
import com.ubx.usdk.bean.enums.RfidProfile;
import com.ubx.usdk.bean.enums.Target;
import com.ubx.usdk.util.LogUtils;

public class SettingActivity extends Activity implements OnClickListener{
	
	private TextView tvResult;
	private Spinner tvpowerdBm;

	private Button bSetting;
	private Button bRead;
	

	private Button measure_loss;
	private Button getRange;
	private Button setRange;


	private Button btSetQV;
	private Button btGetQV;
	private Button btSetSS;
	private Button btGetSS;
	private Button btGetEnergyMode,btSetEnergyMode;
	private Button btnTimeout;
	private String[] strBand =new String[9];
    private String[] strmaxFrm =null; 
    private String[] strminFrm =null;



    private String[] strProfile =new String[12];
	private String[] strRange =new String[101];


	Spinner spBand;
    Spinner spmaxFrm;
	Spinner spminFrm;

	Spinner spTarget;
	Spinner spqvalue;
	Spinner spsession;
	Spinner sptidaddr;
	Spinner sptidlen;
	Spinner spInvMode;
    Spinner spProfilr;
	Spinner spRange;
	private Spinner spinnerQueryMode;
    Button btSetPro,btGetPro;


	Button btSetTarget,btGetTarget;

	Button btSetPower,btGetPower;

	Button btSetArea,btGetArea;


    private ArrayAdapter<String> spada_Band;
    private ArrayAdapter<String> spada_maxFrm; 
    private ArrayAdapter<String> spada_minFrm;

    private ArrayAdapter<String> spada_profile;
	private ArrayAdapter<String> spada_range;
	private static final String TAG = "SacnView";
	private int ReaderType=-1;
	private int ModuleType=-1;//-1;other chips   3:E310 5:E510  7:E710

	private LinearLayout lineprofile;
	private LinearLayout linerange;
	private LinearLayout llInventoryParameters;
	private EditText tvRun;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub  properties
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		ModuleType = RFIDSDKManager.getInstance().getRfidManager().getEx10Version().type;
		Log.i(TAG, "ModuleType : " +ModuleType);

		try {
			initView();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

	}
	private void initView() throws Throwable{


		tvRun = findViewById(R.id.tv_runtime);



		tvResult = findViewById(R.id.param_result);

		tvpowerdBm = findViewById(R.id.power_spinner);

		int maxPower = RFIDSDKManager.getInstance().getRfidManager().getSupportMaxOutputPower();
		String [] pwArray = new String[maxPower+1];
		for(int index=0;index<=maxPower;index++)
		{
			pwArray[index] = String.valueOf(index);
		}
		ArrayAdapter<CharSequence> adapter3 =  new ArrayAdapter(this,  android.R.layout.simple_spinner_item,pwArray);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		tvpowerdBm.setAdapter(adapter3);
		tvpowerdBm.setSelection(maxPower<30?24:maxPower, true);

		bSetting = findViewById(R.id.pro_setting);
		bRead = findViewById(R.id.pro_read);
        btSetPro = findViewById(R.id.bt_SetProfile);
		btGetPro = findViewById(R.id.bt_GetProfile);
		getRange = findViewById(R.id.bt_GetRange);
		setRange = findViewById(R.id.bt_SetDelay);

		btSetTarget = findViewById(R.id.bt_SetAB);
		btGetTarget = findViewById(R.id.bt_GetAB);
		btSetTarget.setOnClickListener(this);
		btGetTarget.setOnClickListener(this);

		btSetPower = findViewById(R.id.bt_SetPower);
		btGetPower = findViewById(R.id.bt_GetPower);
		btSetPower.setOnClickListener(this);
		btGetPower.setOnClickListener(this);

		btSetArea = findViewById(R.id.area_setting);
		btGetArea = findViewById(R.id.area_read);
		btSetArea.setOnClickListener(this);
		btGetArea.setOnClickListener(this);



		btSetQV  = findViewById(R.id.bt_SetQV);
		btGetQV  = findViewById(R.id.bt_GetQV);
		btSetSS  = findViewById(R.id.bt_SetSS);
		btGetSS  = findViewById(R.id.bt_GetSS);

		btSetQV.setOnClickListener(this);
		btGetQV.setOnClickListener(this);
		btSetSS.setOnClickListener(this);
		btGetSS.setOnClickListener(this);


		bSetting.setOnClickListener(this);
		bRead.setOnClickListener(this);
		getRange.setOnClickListener(this);
		setRange.setOnClickListener(this);

        btSetPro.setOnClickListener(this);
		btGetPro.setOnClickListener(this);

		btSetEnergyMode  = findViewById(R.id.bt_set_energy_mode);
		btSetEnergyMode.setOnClickListener(this);

		btGetEnergyMode  = findViewById(R.id.bt_get_energy_mode);
		btGetEnergyMode.setOnClickListener(this);

		btnTimeout  = findViewById(R.id.btn_timeout);
		btnTimeout.setOnClickListener(this);

		lineprofile =  findViewById(R.id.line_profile);
		linerange =  findViewById(R.id.line_range);
		llInventoryParameters =  findViewById(R.id.ll_inventory_parameters);

		strBand[0]="Chinese band2";
		strBand[1]="US band";
		strBand[2]="Korean band";
		strBand[3]="EU band";
		strBand[4]="Chinese band1";
		strBand[5]="Ukraine";
		strBand[6]="Peru";
		strBand[7]="US3";
		strBand[8]="Taiwan";





		spBand= findViewById(R.id.band_spinner);
		spada_Band = new ArrayAdapter<String>(SettingActivity.this,
	             android.R.layout.simple_spinner_item, strBand);  
		spada_Band.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
		spBand.setAdapter(spada_Band);  
		spBand.setSelection(1,false); 
		SetFre(FrequencyRegion.US);
		spBand.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,  int position, long arg3) {
				try {
					arg0.setVisibility(View.VISIBLE);
					int band=0;
					if(position==0)band=1;
					if(position==1)band=2;
					if(position==2)band=3;
					if(position==3)band=9;
					if(position==4)band=8;
					if(position==5)band=6;
					if(position==6)band=7;
					if(position==7)band=0x0C;
					if(position==8)band=0x0A;

					FrequencyRegion frequencyRegion = FrequencyRegion.fromRegion(band);
					SetFre(frequencyRegion);

				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});





		spqvalue= findViewById(R.id.qvalue_spinner);
		ArrayAdapter<CharSequence> adapter =  ArrayAdapter.createFromResource(this, R.array.men_q, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spqvalue.setAdapter(adapter); 
		spqvalue.setSelection(6, true);

		spTarget= findViewById(R.id.ab_spinner);
		ArrayAdapter<CharSequence> adapter0 =  ArrayAdapter.createFromResource(this, R.array.ab_target, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spTarget.setAdapter(adapter0);
		spTarget.setSelection(Target.TARGET_AB, true);


		spsession= findViewById(R.id.session_spinner);  
		ArrayAdapter<CharSequence> adapter1 =  ArrayAdapter.createFromResource(this, R.array.men_s, android.R.layout.simple_spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spsession.setAdapter(adapter1); 
		spsession.setSelection(1, true);


		
		sptidaddr= findViewById(R.id.tidptr_spinner);  
		sptidlen= findViewById(R.id.tidlen_spinner);  
		ArrayAdapter<CharSequence> adapter2 =  ArrayAdapter.createFromResource(this, R.array.men_tid, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sptidaddr.setAdapter(adapter2); 
		sptidaddr.setSelection(0, true);
		sptidlen.setAdapter(adapter2); 
		sptidlen.setSelection(6, true);


		spinnerQueryMode =  findViewById(R.id.spinner_inventory_mode);
		spinnerQueryMode.setSelection(0,false);
		spinnerQueryMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Log.i(TAG, "onItemSelected:   position : "+position);
				QueryMemBank queryMemBank = QueryMemBank.fromOrdinal(position);
				if ( queryMemBank == QueryMemBank.EPC || queryMemBank == QueryMemBank.FASTTID){
					sptidaddr.setEnabled(false);
					sptidlen.setEnabled(false);
				}else {
					sptidaddr.setEnabled(true);
					sptidlen.setEnabled(true);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		}
		);
		sptidaddr.setEnabled(false);
		sptidlen.setEnabled(false);


		ReaderType = RFIDSDKManager.getInstance().getRfidManager().getReaderType();
		InventorySceneMode inventorySceneMode = RFIDSDKManager.getInstance().getRfidManager().getInventorySceneMode();

		spInvMode= findViewById(R.id.inventory_mode_spinner);
		ArrayAdapter<CharSequence> spada_invMode = ArrayAdapter.createFromResource(this, R.array.en_sp_inv_mode, android.R.layout.simple_spinner_item);
		spada_invMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spInvMode.setAdapter(spada_invMode);
		spInvMode.setSelection(inventorySceneMode.getValue(),false);
		setVisibilityParamterUI(inventorySceneMode == InventorySceneMode.CUSTOM_MODE);

//		spInvMode.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
//					@Override
//					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//						setVisibilityParamterUI(InventoryMode.fromValue(position) == InventoryMode.CUSTOM_MODE);
//					}
//					@Override
//					public void onNothingSelected(AdapterView<?> parent) {
//					}
//				}
//		);


//		Log.i(TAG, "  ReaderType      "+ReaderType );

		if(ModuleType == EXChipType.E310)//E310
		{
			strProfile =new String[5];
			strProfile[0]=" 3:  PR_ASK, pie: 2.0, tari_us: 20.0,blf_khz: 320, M: 2";
			strProfile[1]=" 5:  PR_ASK, pie: 2.0, tari_us: 20.0,blf_khz: 320, M: 4";
			strProfile[2]=" 7:  PR_ASK, pie: 2.0, tari_us: 20.0,blf_khz: 250, M: 4";
			strProfile[3]="12: PR_ASK, pie: 2.0, tari_us: 15.0,blf_khz: 320, M: 2";
			strProfile[4]="13: PR_ASK, pie: 2.0, tari_us: 20.0,blf_khz: 160, M: 8";
			lineprofile.setVisibility(View.VISIBLE);
		}
		else if(ModuleType == EXChipType.E510)//
		{
			strProfile =new String[9];
			strProfile[0]=" 3:  PR_ASK, pie: 2.0, tari_us: 20.0,blf_khz: 320, M: 2";
			strProfile[1]=" 5:  PR_ASK, pie: 2.0, tari_us: 20.0,blf_khz: 320, M: 4";
			strProfile[2]=" 7:  PR_ASK, pie: 2.0, tari_us: 20.0,blf_khz: 250, M: 4";
			strProfile[3]="12: PR_ASK, pie: 2.0, tari_us: 15.0,blf_khz: 320, M: 2";
			strProfile[4]="13: PR_ASK, pie: 2.0, tari_us: 20.0,blf_khz: 160, M: 8";
			strProfile[5]="1:  PR_ASK, pie: 2.0, tari_us: 7.5, blf_khz: 640, M: 2";
			strProfile[6]="15: PR_ASK, pie: 2.0, tari_us: 7.5, blf_khz: 640, M: 4";
			strProfile[7]="51:DSB,    pie: 1.5, tari_us: 6.25,blf_khz: 640, M: 2";
			strProfile[8]="53:PR_ASK, pie: 1.5, tari_us: 7.5, blf_khz: 640, M: 4";
			lineprofile.setVisibility(View.VISIBLE);
		}
		else if(ModuleType == EXChipType.E710)//E710
		{
			strProfile =new String[12];
			strProfile[0]=" 3:  PR_ASK, pie: 2.0, tari_us: 20.0,blf_khz: 320, M: 2";
			strProfile[1]=" 5:  PR_ASK, pie: 2.0, tari_us: 20.0,blf_khz: 320, M: 4";
			strProfile[2]=" 7:  PR_ASK, pie: 2.0, tari_us: 20.0,blf_khz: 250, M: 4";
			strProfile[3]="12: PR_ASK, pie: 2.0, tari_us: 15.0,blf_khz: 320, M: 2";
			strProfile[4]="13: PR_ASK, pie: 2.0, tari_us: 20.0,blf_khz: 160, M: 8";
			strProfile[5]="1:  PR_ASK, pie: 2.0, tari_us: 7.5, blf_khz: 640, M: 2";
			strProfile[6]="15: PR_ASK, pie: 2.0, tari_us: 7.5, blf_khz: 640, M: 4";
			strProfile[7]="51:DSB,    pie: 1.5, tari_us: 6.25,blf_khz: 640, M: 2";
			strProfile[8]="53:PR_ASK, pie: 1.5, tari_us: 7.5, blf_khz: 640, M: 4";
			strProfile[9]="11: PR_ASK, pie: 2.0, tari_us: 7.5, blf_khz: 640, M: 1";
			strProfile[10]="50:DSB,    pie: 1.5, tari_us: 6.25,blf_khz: 640, M: 1";
			strProfile[11]="52:PR_ASK, pie: 2.0, tari_us: 15.0,blf_khz: 426, M: 1";
			lineprofile.setVisibility(View.VISIBLE);
		}
		else
		{
			linerange.setVisibility(View.VISIBLE);
			lineprofile.setVisibility(View.GONE);
		}

		if (ModuleType != -1){
        		spProfilr= findViewById(R.id.prof_spinner);
        		spada_profile = new ArrayAdapter<String>(SettingActivity.this,
        		        android.R.layout.simple_spinner_item, strProfile);
        		spada_profile.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        		spProfilr.setAdapter(spada_profile);
				 spProfilr.setSelection(1,false);
		}




		for(int index=0;index<=100;index++)
		{
			strRange[index] = String.valueOf(index);
		}
		spRange= findViewById(R.id.range_spinner);
		spada_range = new ArrayAdapter<String>(SettingActivity.this,
				android.R.layout.simple_spinner_item, strRange);
		spada_range.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spRange.setAdapter(spada_range);
		spRange.setSelection(0,false);

		tvRun.setText(String.format("%d", ScanActivity.timeout));
	}

	private void setVisibilityParamterUI(boolean Visible){
		if (Visible){
			llInventoryParameters.setVisibility(View.VISIBLE);
		}else {
			llInventoryParameters.setVisibility(View.GONE);
		}
	}




	private void ReadInformation() throws Throwable
	{

			FrequencyRegion frequencyRegion = RFIDSDKManager.getInstance().getRfidManager().getFrequencyRegion();
			if(frequencyRegion != null){
				LogUtils.v(TAG,"frequencyRegion : "+frequencyRegion.getRegion()+"   "+frequencyRegion.getMinChannelIndex()+"   "+frequencyRegion.getMaxChannelIndex());

				int region = frequencyRegion.getRegion();
				SetFre(frequencyRegion);
				int bandindex = 1;

				if(region==1   ){bandindex=0;}
				else if(region==2   ){bandindex=1;}
				else if(region==3   ){bandindex=2;}
				else if(region==9   ){bandindex=3;}
				else if(region==8   ){bandindex=4;}
				else if(region==6   ){bandindex=5;}
				else if(region==7   ){bandindex=6;}
				else if(region==0x0C){bandindex=7;}
				else if(region==0x0A){bandindex=8;}
				else{
					MsgShow.writelogErr(getString(R.string.get_failed),tvResult);
					return;
				}
			try {
				spBand.setSelection(bandindex,true);
				spminFrm.setSelection(frequencyRegion.getMinChannelIndex(),true);
				spmaxFrm.setSelection(frequencyRegion.getMaxChannelIndex(),true);
				//sptime.setSelection(ScanTime[0]&255,true);
				MsgShow.writelogSuc(getString(R.string.get_success),tvResult);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
		{
			MsgShow.writelogErr(getString(R.string.get_failed),tvResult);
		}
	}

	private void getRangeControll() throws Throwable
	{
		int range = RFIDSDKManager.getInstance().getRfidManager().getInventoryRssiLimit();
		if(range >= 0)
		{
			spRange.setSelection(range,true);
			MsgShow.writelogSuc(getString(R.string.get_success),tvResult);
		}
		else
		{
			MsgShow.writelogErr(getString(R.string.get_failed)+" "+range,tvResult);
		}
	}

	private void ReadProfile() throws Throwable
	{
		RfidProfile Profile = RFIDSDKManager.getInstance().getRfidManager().getProfile();
		if(ModuleType !=-1 )
		{
			int index = 0;
			if(Profile  !=null)
			{
				Log.d(TAG, "ReadProfile: "+Profile.value());
				switch(Profile.value() )
				{
					case 3:
						index = 0;
						break;
					case 5:
						index = 1;
						break;
					case 7:
						index = 2;
						break;
					case 12:
						index = 3;
						break;
					case 13:
						index = 4;
						break;
					case 1:
						index = 5;
						break;
					case 15:
						index = 6;
						break;
					case 51:
						index = 7;
						break;
					case 53:
						index = 8;
						break;
					case 11:
						index = 9;
						break;
					case 50:
						index = 10;
						break;
					case 52:
						index = 11;
						break;
				}
				spProfilr.setSelection(index,true);
				MsgShow.writelogSuc(getString(R.string.set_success),tvResult);
			}

			else
			{
				MsgShow.writelogErr(getString(R.string.set_failed),tvResult);
			}

		}
		else
		{
			MsgShow.writelogErr(getString(R.string.set_failed),tvResult);
		}

	}
	@Override
	public void onClick(View view) {
		try{
			 if (view == bSetting){
				
				int MaxFre=0;
				int MinFre=0;

				int fband = spBand.getSelectedItemPosition();
				int band=0;
				if(fband==0)band=1;
				if(fband==1)band=2;
				if(fband==2)band=3;
				if(fband==3)band=9;
				if(fband==4)band=8;
				 if(fband==5)band=6;
				 if(fband==6)band=7;
				 if(fband==7)band=0x0C;
				 if(fband==8)band=0x0A;
				int Frequent= spminFrm.getSelectedItemPosition();
				MinFre = Frequent;
				Frequent= spmaxFrm.getSelectedItemPosition();
				MaxFre = Frequent;


				FrequencyRegion frequencyRegion = FrequencyRegion.fromRegion(band);
				int result = RFIDSDKManager.getInstance().getRfidManager().setFrequencyRegion(frequencyRegion, MinFre,MaxFre);
				if(result!=0)
				{
					MsgShow.writelogErr(getString(R.string.frequent_error)+" "+result,tvResult);
				}
				else
				{
					MsgShow.writelogSuc(getString(R.string.set_success),tvResult);
				}
			}
			else if (view == setRange){
				int index = spRange.getSelectedItemPosition();
				int fCmdRet = RFIDSDKManager.getInstance().getRfidManager().setInventoryRssiLimit(index);
				if(fCmdRet==0)
				{
					MsgShow.writelogSuc(getString(R.string.set_success),tvResult);
				}
				else
				{
					MsgShow.writelogErr(getString(R.string.set_failed),tvResult);
				}
			}else if (view == getRange){
				getRangeControll();
			}
			 else if(view == btSetTarget)
			{
				int target =  spTarget.getSelectedItemPosition();
				Log.v(TAG,"target : "+target);
				int fCmdRet = RFIDSDKManager.getInstance().getRfidManager().setInventoryWithTarget(target);
				if(fCmdRet==0)
				{
					MsgShow.writelogSuc(getString(R.string.set_success),tvResult);
				}
				else
				{
					MsgShow.writelogErr(getString(R.string.set_failed),tvResult);
				}
			}else if(view == btGetTarget)
			{

				int fCmdRet = RFIDSDKManager.getInstance().getRfidManager().getInventoryWithTarget();
				if(fCmdRet>=0 && fCmdRet <3)
				{
						spTarget.setSelection(fCmdRet);
						MsgShow.writelogSuc(getString(R.string.get_success), tvResult);
				}
				else
				{
					MsgShow.writelogErr(getString(R.string.get_failed)+" "+fCmdRet,tvResult);
				}
			}else if(view == btSetPower)
			{

				int Power= tvpowerdBm.getSelectedItemPosition();
				int result = RFIDSDKManager.getInstance().getRfidManager().setOutputPower(Power);
				if(result!=0)
				{
					MsgShow.writelogErr(getString(R.string.power_error),tvResult);
				}else {
					MsgShow.writelogSuc(getString(R.string.set_success),tvResult);
				}
			}else if(view == btGetPower)
			{

				int power = RFIDSDKManager.getInstance().getRfidManager().getOutputPower();
				if (power>=0){
					tvpowerdBm.setSelection(power,true);
					MsgShow.writelogSuc(getString(R.string.get_success), tvResult);
				}else {
					MsgShow.writelogErr(getString(R.string.get_failed) ,tvResult);
				}
			}
			else if(view == btSetArea)
			{
				int length = sptidlen.getSelectedItemPosition();
				int startAddress = sptidaddr.getSelectedItemPosition();
				int area = spinnerQueryMode.getSelectedItemPosition();
				 int result = RFIDSDKManager.getInstance().getRfidManager().setQueryMemoryBank(QueryMemBank.fromOrdinal(area),startAddress,length);
				if(result!=0)
				{
					MsgShow.writelogErr(getString(R.string.set_failed),tvResult);
				}else {
					MsgShow.writelogSuc(getString(R.string.set_success),tvResult);
				}
				setInvTimeOut();
			}else if(view == btGetArea)
			{

				QueryMemBank queryMemBank = RFIDSDKManager.getInstance().getRfidManager().getQueryMemoryBank();
				if (queryMemBank != null){
					spinnerQueryMode.setSelection(queryMemBank.ordinal(),true);
					sptidlen.setSelection(queryMemBank.getReadLength(), true);
					sptidaddr.setSelection(queryMemBank.getStartAddress(), true);
					MsgShow.writelogSuc(getString(R.string.get_success), tvResult);
				}else {
					MsgShow.writelogErr(getString(R.string.get_failed) ,tvResult);
				}
			}
			else if (view == bRead){
				try
				{
					ReadInformation();
				}catch(Exception ex)
				{}

			}
			 else if(view == btGetPro)
			{
				ReadProfile();
			}
            else if(view == btSetPro)
            {
                int index = spProfilr.getSelectedItemPosition();
				int Profile=1;
				if(ModuleType != EXChipType.OTHER)
				{
					switch(index)
					{
						case 0:
							Profile = 3;
							break;
						case 1:
							Profile = 5;
							break;
						case 2:
							Profile = 7;
							break;
						case 3:
							Profile = 12;
							break;
						case 4:
							Profile = 13;
							break;
						case 5:
							Profile = 1;
							break;
						case 6:
							Profile =15;
							break;
						case 7:
							Profile = 51;
							break;
						case 8:
							Profile = 53;
							break;
						case 9:
							Profile = 11;
							break;
						case 10:
							Profile = 50;
							break;
						case 11:
							Profile = 52;
							break;
					}
				}
				else
				{
					Profile = index;
				}

                int result = RFIDSDKManager.getInstance().getRfidManager().setProfile(RfidProfile.fromIndex(Profile));
                if(result==0)
                {
                    MsgShow.writelogSuc(getString(R.string.set_success),tvResult);
                }
                else
                {
                    MsgShow.writelogErr(getString(R.string.set_failed),tvResult);
                }
            }
			else if(view == btSetSS)
			{
				int index = spsession.getSelectedItemPosition();
				int fCmdRet = RFIDSDKManager.getInstance().getRfidManager().setInventoryWithSession(index);
				if(fCmdRet==0)
				{
					MsgShow.writelogSuc(getString(R.string.set_success),tvResult);
				}
				else
				{
					MsgShow.writelogErr(getString(R.string.set_failed),tvResult);
				}
			}else if(view == btGetSS)
			{
				int value = RFIDSDKManager.getInstance().getRfidManager().getInventoryWithSession();
				if(value>=0)
				{
					int sessionindex = value;
					spsession.setSelection(sessionindex,true);
					MsgShow.writelogSuc(getString(R.string.get_success),tvResult);
				}
				else
				{
					MsgShow.writelogErr(getString(R.string.get_failed)+"  "+value,tvResult);
				}
			}
			else if(view == btSetQV)
			{
				int index = spqvalue.getSelectedItemPosition();
				int fCmdRet = RFIDSDKManager.getInstance().getRfidManager().setInventoryWithStartQvalue(index);
				if(fCmdRet==0)
				{
					MsgShow.writelogSuc(getString(R.string.set_success),tvResult);
				}
				else
				{
					MsgShow.writelogErr(getString(R.string.set_failed),tvResult);
				}
			}else if(view == btGetQV)
			{
				int value = RFIDSDKManager.getInstance().getRfidManager().getInventoryWithStartQvalue();
				if(value>=0)
				{
					spqvalue.setSelection(value,true);
					MsgShow.writelogSuc(getString(R.string.get_success),tvResult);
				}
				else
				{
					MsgShow.writelogErr(getString(R.string.get_failed)+"  "+value,tvResult);
				}
			}
			 else if(view == btGetEnergyMode)
			{

				InventorySceneMode inventorySceneMode =	RFIDSDKManager.getInstance().getRfidManager().getInventorySceneMode();
				if (inventorySceneMode !=null){
					spInvMode.setSelection(inventorySceneMode.getValue(),true);
					setVisibilityParamterUI(inventorySceneMode == InventorySceneMode.CUSTOM_MODE);
					MsgShow.writelogSuc(getString(R.string.get_success),tvResult);
				}else {
					MsgShow.writelogErr(getString(R.string.get_failed)+"  ",tvResult);
				}

			}
			else if(view == btSetEnergyMode)
			{
				int index = spInvMode.getSelectedItemPosition();
				int ret = RFIDSDKManager.getInstance().getRfidManager().setInventorySceneMode(InventorySceneMode.fromValue(index));
				if(ret==0)
				{
					setVisibilityParamterUI(InventorySceneMode.fromValue(index) == InventorySceneMode.CUSTOM_MODE);
					MsgShow.writelogSuc(getString(R.string.set_success),tvResult);
				}
				else
				{
					MsgShow.writelogErr(getString(R.string.set_failed),tvResult);
				}
			}
			else if(view == btnTimeout)
			{
					setInvTimeOut();
			}
		}catch(Throwable ex)
		{
			ex.printStackTrace();
		}
	}

	private void setInvTimeOut(){

		String edTime = tvRun.getText().toString();
		if (TextUtils.isEmpty(edTime)){
			MsgShow.writelogErr(getString(R.string.set_failed),tvResult);
			return;
		}
		int timeout = 0;
		try {
			timeout = Integer.valueOf(edTime);
		} catch (Exception e) {
			e.printStackTrace();
			timeout = - 1;
		}
		if (timeout>=0){
			ScanActivity.timeout = timeout;
			MsgShow.writelogSuc(getString(R.string.set_success),tvResult);
		}else {
			tvRun.setText(ScanActivity.timeout);
			MsgShow.writelogErr(getString(R.string.set_failed),tvResult);
		}
	}
	private void SetFre(FrequencyRegion frequencyRegion)  throws Throwable
	{

			strmaxFrm=new String[frequencyRegion.getChannelCount()];
			strminFrm=new String[frequencyRegion.getChannelCount()];
			for(int i=0;i<frequencyRegion.getChannelCount();i++){
				String temp="";
				float values=(float) (frequencyRegion.getStartFreqMHz() + i * frequencyRegion.getStepSizeMHz());
				temp=String.valueOf(values)+"MHz";
				strminFrm[i]=temp;
				strmaxFrm[i]=temp;
			}
			spmaxFrm= findViewById(R.id.max_spinner);
			spada_maxFrm = new ArrayAdapter<String>(SettingActivity.this,
					android.R.layout.simple_spinner_item, strmaxFrm);
			spada_maxFrm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spmaxFrm.setAdapter(spada_maxFrm);
			spmaxFrm.setSelection(frequencyRegion.getChannelCount()-1,false);

			spminFrm= findViewById(R.id.min_spinner);
			spada_minFrm = new ArrayAdapter<String>(SettingActivity.this,
					android.R.layout.simple_spinner_item, strminFrm);
			spada_minFrm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spminFrm.setAdapter(spada_minFrm);
			spminFrm.setSelection(0,false);

	}
}
