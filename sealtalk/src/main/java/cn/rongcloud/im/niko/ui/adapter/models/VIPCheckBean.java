package cn.rongcloud.im.niko.ui.adapter.models;

public class VIPCheckBean {

    /**
     * Step1Way1 : true
     * Step1Way2 : true
     * Step2 : true
     * InviteCode : string
     */

    private boolean Step1Way1;
    private boolean Step1Way2;
    private boolean Step2;
    private String InviteCode;

    public boolean isStep1Way1() {
        return Step1Way1;
    }

    public void setStep1Way1(boolean Step1Way1) {
        this.Step1Way1 = Step1Way1;
    }

    public boolean isStep1Way2() {
        return Step1Way2;
    }

    public void setStep1Way2(boolean Step1Way2) {
        this.Step1Way2 = Step1Way2;
    }

    public boolean isStep2() {
        return Step2;
    }

    public void setStep2(boolean Step2) {
        this.Step2 = Step2;
    }

    public String getInviteCode() {
        return InviteCode;
    }

    public void setInviteCode(String InviteCode) {
        this.InviteCode = InviteCode;
    }
}
