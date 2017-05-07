package com.test.message;
import java.math.BigDecimal;
import java.util.*;
import java.lang.*;
import java.io.*;


public  class MessageHandler
{
    //  Message handler for applications where only one message
    // handler is used and needs to be globally accessible.

    private boolean  runProcess = true;
    private SalesRepository salesRepository= new SalesRepository();
    private Queue messageQueue = new PriorityQueue();
    private final int maxMessageCount;

    public MessageHandler(Queue<Message>  messageQueue,int maxMessageCount) {
        setStreams(messageQueue);
        this.maxMessageCount = maxMessageCount;
    }

    public SalesRepository getSalesRepository() {
        return salesRepository;
    }

    public void setSalesRepository(SalesRepository salesRepository) {
        this.salesRepository = salesRepository;
    }


    protected void setStreams(Queue<Message> messageQueue) {
        this.messageQueue = messageQueue;
    }

    public HashMap<String, BigDecimal> readMsg() throws IOException {
        Message msg;
        String token;

        while(runProcess){

            //handle cases where the message is null
            msg = (Message)messageQueue.poll();
            if(msg == null) break;

           // set/increment count of messages when it is the first/subsequent message inthe queue
            if( salesRepository.getCountByItem().get(msg.getItem()) != null) {
                if (msg.getNumberOfSales() > 1)
                    salesRepository.getCountByItem().put(msg.getItem(), salesRepository.getCountByItem().get(msg.getItem()) + msg.getNumberOfSales());
                else
                    salesRepository.getCountByItem().put(msg.getItem(), salesRepository.getCountByItem().get(msg.getItem()) + 1);
            }else {
                if(msg.getNumberOfSales() > 1)
                    salesRepository.getCountByItem().put(msg.getItem(), msg.getNumberOfSales());
                else
                    salesRepository.getCountByItem().put(msg.getItem(), 1);
            }

            // handle message adjustments, process and store them in an adjustments repository(Hash Map)
            if(msg.getAdjustment() != null && (Adjustment.ADD.equals(msg.getAdjustment())
                    || Adjustment.MULTIPLY.equals(msg.getAdjustment())
                    || Adjustment.SUBTRACT.equals(msg.getAdjustment()))){
                handleAdjustments(msg);
            }
            // block to handle cases that are not Adjustment cases.
            else {
                processSalesMessage(msg);
            }

            salesRepository.incrementMessageCount();

            // display summary of sales details of messages processed for every 2 messages processed
            if(salesRepository.getMessageCount() % 2 == 0 ) {
                displayMessageSummary();
            }

            // terminate the process if its the 50th message
            if (salesRepository.getMessageCount() >= maxMessageCount){
                terminateMessageHandler(maxMessageCount);
            }

            //handle boundray conditions
            if (messageQueue.isEmpty()){
                this.runProcess = false;
            }
      }
        return null;
    }

    public void run() {
        try {
            while (runProcess) {
                HashMap<String,BigDecimal> messageStats = readMsg();
            }

            System.out.println(" Process terminated successfully .....");

        }
        catch (Exception e) {};
    }

    private void handleAdjustments( Message msg){

        if(salesRepository.getMessageAdjustmentLog().get(msg.getItem()) != null){
            Message adjustmentMessage = new Message();
            adjustmentMessage.setSales(msg.getSales());
            adjustmentMessage.setAdjustment(msg.getAdjustment());
            adjustmentMessage.setItem(msg.getItem());
            LinkedList<Message> messageList = salesRepository.getMessageAdjustmentLog().get(msg.getItem());
            messageList.add(adjustmentMessage);

            salesRepository.getMessageAdjustmentLog().put(msg.getItem(),messageList);
        }
        else{

            LinkedList<Message> newAdjustmentList = new LinkedList();

            Message adjustmentMessage = new Message();
            adjustmentMessage.setSales(msg.getSales());
            adjustmentMessage.setAdjustment(msg.getAdjustment());
            adjustmentMessage.setItem(msg.getItem());

            newAdjustmentList.add(adjustmentMessage);
            salesRepository.getMessageAdjustmentLog().put(msg.getItem(),newAdjustmentList );
        }
        LinkedList salesLog =  salesRepository.getMessageLog().get(msg.getItem());
        if( salesLog != null) {
            Iterator salesIter = salesLog.iterator();
            while (salesIter.hasNext()) {
                Message message = (Message) salesIter.next();

                Message salesAdjMessage = new Message();
                salesAdjMessage.setSales(msg.getSales());
                salesAdjMessage.setAdjustment(msg.getAdjustment());
                salesAdjMessage.setItem(msg.getItem());
                salesAdjMessage.setNumberOfSales(msg.getNumberOfSales());

                // ADD/MULTIPLY/SUBTRACT sales for each item of the same type as the adjustment message
                if (Adjustment.ADD.equals(msg.getAdjustment())) {
                    message.setSales(message.getSales().add(salesAdjMessage.getSales().multiply(new BigDecimal(String.valueOf(salesAdjMessage.getNumberOfSales())))));
                }

                if (Adjustment.MULTIPLY.equals(msg.getAdjustment())) {
                    message.setSales(message.getSales().multiply(salesAdjMessage.getSales().multiply(new BigDecimal(String.valueOf(salesAdjMessage.getNumberOfSales())))));
                }
                if (Adjustment.SUBTRACT.equals(msg.getAdjustment())) {
                    message.setSales(message.getSales().subtract(salesAdjMessage.getSales().multiply(new BigDecimal(String.valueOf(salesAdjMessage.getNumberOfSales())))));
                }

            }
        }

    }

    private void processSalesMessage(Message msg) {
        Message salesMessage = new Message();
        if (salesRepository.getMessageLog().get(msg.getItem()) != null) {

            salesMessage.setSales(msg.getSales());
            salesMessage.setAdjustment(msg.getAdjustment());
            salesMessage.setItem(msg.getItem());
            salesMessage.setNumberOfSales(msg.getNumberOfSales());

            LinkedList<Message> messageList = salesRepository.getMessageLog().get(msg.getItem());
            messageList.add(salesMessage);

            salesRepository.getMessageLog().put(msg.getItem(), messageList);

        } else {

            salesMessage.setSales(msg.getSales());
            salesMessage.setAdjustment(msg.getAdjustment());
            salesMessage.setItem(msg.getItem());
            salesMessage.setNumberOfSales(msg.getNumberOfSales());
            LinkedList<Message> newItemList = new LinkedList();
            newItemList.add(salesMessage);
            salesRepository.getMessageLog().put(msg.getItem(), newItemList);
        }
    }

    private void displayMessageSummary() {
            System.out.println(" ======================== ");
            System.out.println(" ItemRepository List ");
            System.out.println(" ======================== ");

            Iterator logIter = salesRepository.getMessageLog().keySet().iterator();
            while (logIter.hasNext()) {
                String key = (String) logIter.next();

                LinkedList salesLog =  salesRepository.getMessageLog().get(key);;
                Iterator salesIter = salesLog.iterator();
                BigDecimal totalItemSales = BigDecimal.ZERO;
                while(salesIter.hasNext()) {
                    Message messageItem = (Message)salesIter.next();
                    totalItemSales = totalItemSales.add(messageItem.getSales().multiply(new BigDecimal(String.valueOf(messageItem.getNumberOfSales()))));

                }

                System.out.println(" item " + key);
                System.out.println(" item count " + salesRepository.getCountByItem().get(key));
                System.out.println(" sales " + totalItemSales.setScale(2,BigDecimal.ROUND_DOWN));

                salesRepository.getMessageStats().put(key,totalItemSales.setScale(2,BigDecimal.ROUND_DOWN));
                System.out.println(" ======================== ");

            }
    }

    private void terminateMessageHandler(int maxMessageCount){

            System.out.println(" **** "+maxMessageCount+" messages processed ******");
            System.out.println(" **** Pausing message processsing ******");
            System.out.println(" ^^^^^ Report of adjustments ^^^^^");
            System.out.println(" ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            Iterator adjustmentLog  = salesRepository.getMessageAdjustmentLog().keySet().iterator();
            while (adjustmentLog.hasNext()){

                String adjMessageKey = (String)adjustmentLog.next();
                System.out.println(" item key :"+adjMessageKey);
                LinkedList salesAdjLog =  salesRepository.getMessageAdjustmentLog().get(adjMessageKey);
                Iterator<Message> salesAdjIter =  salesAdjLog.iterator();

                while(salesAdjIter.hasNext()) {
                    Message messageItem = (Message)salesAdjIter.next();
                    System.out.println(" Sales Adjustments Adjustment -- "+messageItem.getAdjustment()+" -- Sales "+messageItem.getSales());

                }
                System.out.println(" ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            }
            this.runProcess = false;

    }
}
