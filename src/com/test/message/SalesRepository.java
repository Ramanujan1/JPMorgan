package com.test.message;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by ramanujanuppili on 28/04/2017.
 */
// Class to maintain all relevant information and history of sales and messages in the queue
public  class SalesRepository {
    private String item ;
    private BigDecimal sales;
    private  HashMap<String,BigDecimal> itemRepository = new HashMap();
    private  HashMap<String,Integer> countByItem = new HashMap();
    private HashMap<String,LinkedList<Message>> messageLog = new HashMap();
    private  HashMap<String,LinkedList<Message>> messageAdjustmentLog = new HashMap();
    private  HashMap<String,BigDecimal> messageStats = new HashMap<>();
    private  int messageCount =0;

    public  HashMap<String, BigDecimal> getMessageStats() {
        return messageStats;
    }

    public  void setMessageStats(HashMap<String, BigDecimal> messageStats) {
        this.messageStats = messageStats;
    }

    public  HashMap<String, LinkedList<Message>> getMessageAdjustmentLog() {
        return messageAdjustmentLog;
    }

    public  void setMessageAdjustmentLog(HashMap<String, LinkedList<Message>> messageAdjustmentLog) {
        this.messageAdjustmentLog = messageAdjustmentLog;
    }

    public  int getMessageCount() {
        return messageCount;
    }

    public HashMap<String, LinkedList<Message>> getMessageLog() {
        return messageLog;
    }

    public  void setMessageLog(HashMap<String, LinkedList<Message>> messageLog) {
        this.messageLog = messageLog;
    }

    public void setMessageCount(int numberOfSales) {
        if( numberOfSales > 1)
            this.messageCount = messageCount+numberOfSales;
        else
            ++this.messageCount ;
    }

    public  void incrementMessageCount() {

            ++this.messageCount ;
    }

    public String getItem() {
        return item;
    }

    public  HashMap<String, Integer> getCountByItem() {
        return countByItem;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public BigDecimal getSales() {
        return sales;
    }

    public void setSales(BigDecimal sales) {
        this.sales = sales;
    }

    public HashMap getItemRepository() {
        return itemRepository;
    }

    public  BigDecimal getItemSales(String item) {
        if ( itemRepository.get(item) != null){
            return itemRepository.get(item);
        }else
            return null;
    }

    public  Map<String,BigDecimal> getItemRepositoryString () {

            return itemRepository;

    }
}
