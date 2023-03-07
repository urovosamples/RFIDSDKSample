package com.ubx.rfid_demo.pojo;

import java.util.Arrays;

public class ManageFormInfo {

    private byte btWordCnt;
    private byte btWordAdd;
    private byte btMemBank;
    private byte[] btAryPassWord;
    private byte[] btAryData;

    public ManageFormInfo() {
    }

    public ManageFormInfo(byte btWordCnt, byte btWordAdd, byte btMemBank, byte[] btAryPassWord, byte[] btAryData) {
        this.btWordCnt = btWordCnt;
        this.btWordAdd = btWordAdd;
        this.btMemBank = btMemBank;
        this.btAryPassWord = btAryPassWord;
        this.btAryData = btAryData;
    }

    public byte getBtWordCnt() {
        return btWordCnt;
    }

    public void setBtWordCnt(byte btWordCnt) {
        this.btWordCnt = btWordCnt;
    }

    public byte getBtWordAdd() {
        return btWordAdd;
    }

    public void setBtWordAdd(byte btWordAdd) {
        this.btWordAdd = btWordAdd;
    }

    public byte getBtMemBank() {
        return btMemBank;
    }

    public void setBtMemBank(byte btMemBank) {
        this.btMemBank = btMemBank;
    }

    public byte[] getBtAryPassWord() {
        return btAryPassWord;
    }

    public void setBtAryPassWord(byte[] btAryPassWord) {
        this.btAryPassWord = btAryPassWord;
    }

    public byte[] getBtAryData() {
        return btAryData;
    }

    public void setBtAryData(byte[] btAryData) {
        this.btAryData = btAryData;
    }

    @Override
    public String toString() {
        return "ManageFormInfo{" +
                "btWordCnt=" + btWordCnt +
                ", btWordAdd=" + btWordAdd +
                ", btMemBank=" + btMemBank +
                ", btAryPassWord=" + Arrays.toString(btAryPassWord) +
                ", btAryData=" + Arrays.toString(btAryData) +
                '}';
    }
}
