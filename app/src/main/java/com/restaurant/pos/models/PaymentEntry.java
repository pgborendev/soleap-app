package com.restaurant.pos.models;
public class PaymentEntry {
    public enum Method{CASH,CARD,QR_CODE,MOBILE_BANKING}
    private String invoiceName,referenceNo;
    private Method method;
    private double amount,tenderedAmount,changeAmount;
    public PaymentEntry(String inv,Method m,double amt){invoiceName=inv;method=m;amount=amt;tenderedAmount=amt;}
    public String getInvoiceName(){return invoiceName;} public Method getMethod(){return method;}
    public double getAmount(){return amount;} public double getTenderedAmount(){return tenderedAmount;}
    public double getChangeAmount(){return changeAmount;} public String getReferenceNo(){return referenceNo;}
    public void setTenderedAmount(double t){tenderedAmount=t;changeAmount=t-amount;}
    public void setReferenceNo(String r){referenceNo=r;}
    public String getMethodString(){
        switch(method){case CARD:return"Credit Card";case QR_CODE:return"QR Code";case MOBILE_BANKING:return"Mobile Banking";default:return"Cash";}
    }
}
