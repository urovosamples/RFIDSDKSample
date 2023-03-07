package com.ubx.rfid_demo.pojo;

import java.util.Objects;

public class TagScan {

    private String rssi;
    private String epc;
    private String tid;
    private int count;

    public TagScan() {
    }

    public TagScan(String epc,String tid, String rssi, int count) {
        this.rssi = rssi;
        this.epc = epc;
        this.tid = tid;
        this.count = count;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    @Override
    public String toString() {
        return "TagScan{" +
                "rssi='" + rssi + '\'' +
                ", epc='" + epc + '\'' +
                ", tid='" + tid + '\'' +
                ", count=" + count +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagScan tagScan = (TagScan) o;
        return Objects.equals(epc, tagScan.epc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rssi, epc, count);
    }
}
