package com.ubx.rfid_demo.pojo;

public class TagManage {

    private String epc;
    private String pc;
    private String data;
    private String crc;
    private boolean click;

    public TagManage() {
    }

    public TagManage(String epc, String pc, String data, String crc, boolean click) {
        this.epc = epc;
        this.pc = pc;
        this.data = data;
        this.crc = crc;
        this.click = click;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getPc() {
        return pc;
    }

    public void setPc(String pc) {
        this.pc = pc;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }

    public boolean isClick() {
        return click;
    }

    public void setClick(boolean click) {
        this.click = click;
    }

    @Override
    public String toString() {
        return "TagManage{" +
                "epc='" + epc + '\'' +
                ", pc='" + pc + '\'' +
                ", data='" + data + '\'' +
                ", crc='" + crc + '\'' +
                ", click=" + click +
                '}';
    }
}
