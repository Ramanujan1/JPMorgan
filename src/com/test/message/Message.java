package com.test.message;

import java.math.BigDecimal;
import java.util.Vector;

// Entity class that stores the message structure
public  class Message
{

    private String id;
    private Vector argList;
    private String messageBody;
    private BigDecimal sales;
    private Adjustment adjustment;
    private int numberOfSales = 1;
    private String item;


    public Message() {
        argList = new Vector();
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Adjustment getAdjustment() {
        return adjustment;
    }

    public void setAdjustment(Adjustment adjustment) {
        this.adjustment = adjustment;
    }

    public int getNumberOfSales() {
        return numberOfSales;
    }

    public void setNumberOfSales(int numberOfSales) {
        this.numberOfSales = numberOfSales;
    }

    public BigDecimal getSales() {
        return sales;
    }

    public void setSales(BigDecimal sales) {
        this.sales = sales;
    }

    public Message(String mid) {
        id = mid;
        argList = new Vector();
    }

    protected void setId(String mid) {
        id = mid;
    }

    public void addArg(String arg) {
        argList.addElement(arg);
    }

    public String messageID() {
        return id;
    }

    public Vector argList() {
        Vector listCopy = (Vector)argList.clone();
        return listCopy;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

}
