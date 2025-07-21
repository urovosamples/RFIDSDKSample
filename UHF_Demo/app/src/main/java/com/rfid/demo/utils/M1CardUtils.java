package com.rfid.demo.utils;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;

public class M1CardUtils {
    private static String TAG = M1CardUtils.class.getSimpleName();

    /**
     * 判断是否支持NFC
     *
     * @return
     */
    public static  NfcAdapter isNfcAble(Activity mContext){
        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(mContext);
        if (mNfcAdapter == null) {
//            ToastUtils.show("设备不支持NFC！");
            return null;
        }
        if (!mNfcAdapter.isEnabled()) {
            ToastUtils.show("请在系统设置中先启用NFC功能！");
        }
        return mNfcAdapter;
    }

    /**
     * 监测是否支持MifareClassic
     *
     * @param tag
     * @return
     */
    public static boolean isMifareClassic(Tag tag ) {
        String [] techList = tag.getTechList();
        boolean haveMifareUltralight = false;
        for (String tech : techList) {

            if (tech.contains("MifareClassic")) {
                haveMifareUltralight = true;
                break;
            }
        }
        if (!haveMifareUltralight) {
            ToastUtils.show("不支持MifareClassic");
            return false;
        }
        return true;
    }

    /**
     * 读取卡片信息
     *
     * @return
     */
    public static String readCard(Tag tag) throws Throwable {
        MifareClassic mifareClassic = MifareClassic.get(tag);
          try {
            mifareClassic.connect();
            StringBuilder metaInfo = new StringBuilder();
              Charset gbk = Charset.forName("gbk");

            // 获取TAG中包含的扇区数
            int sectorCount = mifareClassic.getSectorCount();
            //            for (int j = 0; j < sectorCount; j++) {
            int bCount = 0; //当前扇区的块数
            int  bIndex = 0; //当前扇区第一块
            if (m1Auth(mifareClassic, 2)) {
                bCount = mifareClassic.getBlockCountInSector(2);
                bIndex = mifareClassic.sectorToBlock(2);
                int length = 0;

                for (int i = 0; i < bCount; i++) {
                    byte[] data = mifareClassic.readBlock(bIndex);
                    for (int i1 = 0; i1 < data.length; i1++) {
                        if (data[i1] == Byte.parseByte("0")) {
                            length = i1;
                        }
                    }
                    String dataString = new String(data, 0, length, gbk).trim();// { it <= ' ' }
                    metaInfo.append(dataString);
                    bIndex++;
                }

//                for (i in 0 until bCount) {
//                    val data = mifareClassic.readBlock(bIndex)
//                    for (i1 in data.indices) {
//                        if (data[i1] == 0.toByte()) {
//                            length = i1
//                        }
//                    }
//                    val dataString = String(data, 0, length, gbk).trim { it <= ' ' }
//                    metaInfo.append(dataString)
//                    bIndex++;
//                }
            } else {
                Log.e(TAG,"密码校验失败");
            }
            //            }
            return metaInfo.toString();
        } catch ( IOException e) {
            e.printStackTrace();
        } finally {
            try {
                mifareClassic.close();
            } catch (  IOException e) {
                e.printStackTrace();
            }
        }
          return "";
    }

    /**
     * 改写数据
     *
     * @param block
     * @param blockbyte
     */
    private static boolean writeBlock( Tag tag, int block, byte[] blockbyte)  {
        MifareClassic mifareClassic = MifareClassic.get(tag);
        try {
            mifareClassic.connect();
            if (m1Auth(mifareClassic, block / 4)) {
                mifareClassic.writeBlock(block, blockbyte);
                Log.e(TAG,"writeBlock"+" 写入成功");
            } else {
                Log.e(TAG, "没有找到密码");
                return false;
            }
        } catch ( IOException e) {
           e.printStackTrace();
        } finally {
            try {
                mifareClassic.close();
            } catch ( IOException e) {
               e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 密码校验
     *
     * @param mTag
     * @param position
     * @return
     * @throws IOException
     */
    private static boolean m1Auth( MifareClassic mTag, int position) throws IOException {
        if (mTag.authenticateSectorWithKeyA(position, MifareClassic.KEY_DEFAULT)) {
            return true;
        } else if (mTag.authenticateSectorWithKeyB(position, MifareClassic.KEY_DEFAULT)) {
            return true;
        }
        return false;
    }



}
