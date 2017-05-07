import com.test.message.Adjustment;
import com.test.message.Message;
import com.test.message.MessageHandler;
import com.test.message.SalesRepository;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import static org.junit.Assert.assertEquals;

/**
 * Created by ramanujanuppili on 28/04/2017.
 */

public class TestMessageHandler {


    // test to check if the sales are correctly summed for each item across different message
    @Test
    public void testValidSales(){

        Queue<Message> messageQueue = new PriorityQueue(idComparator);

        Message message = new Message("test1");
        message.setMessageBody("test1 body");
        message.setItem("test1");
        message.setNumberOfSales(1);
        message.setSales(new BigDecimal(21.2));
        messageQueue.add(message);

        message = new Message("test1");
        message.setMessageBody("test1 body");
        message.setItem("test1");
        message.setNumberOfSales(1);
        message.setSales(new BigDecimal(100.2));
        messageQueue.add(message);

        SalesRepository salesRepository = new SalesRepository();
        MessageHandler msgHdlr = new MessageHandler(messageQueue,50);
        msgHdlr.getSalesRepository().getMessageStats().put("test1",null);

        msgHdlr.run();

        assertEquals( new BigDecimal(121.40).setScale(2,BigDecimal.ROUND_DOWN),msgHdlr.getSalesRepository().getMessageStats().get("test1"));

    }

    // test  item types and MULTIPLY adjustment, The message with the MULTIPLY adjustment will be applied to evey
    // other message that was previously processed of the same type.
    @Test
    public void testValidSalesMultiply(){
        Queue<Message> messageQueue = new PriorityQueue(idComparator);

        Message message = new Message("test1");
        message.setMessageBody("test1 body");
        message.setItem("test1");
        message.setNumberOfSales(1);
        message.setSales(new BigDecimal(21.2));
        messageQueue.add(message);

        message = new Message("test1");
        message.setMessageBody("test1 body");
        message.setItem("test1");
        message.setNumberOfSales(1);
        message.setSales(new BigDecimal(100.2));
        message.setAdjustment(Adjustment.MULTIPLY);
        messageQueue.add(message);

        MessageHandler msgHdlr = new MessageHandler(messageQueue,50);
        msgHdlr.getSalesRepository().getMessageStats().put("test1",null);
        msgHdlr.run();
        assertEquals(new BigDecimal( 2124.23).setScale(2,BigDecimal.ROUND_DOWN),msgHdlr.getSalesRepository().getMessageStats().get("test1"));

    }

    // test  item types and ADD adjustment
    @Test
    public void testValidSalesAdd(){
        Queue<Message> messageQueue = new PriorityQueue(idComparator);

        Message message = new Message("test1");
        message.setMessageBody("test1 body");
        message.setItem("test1");
        message.setNumberOfSales(1);
        message.setSales(new BigDecimal(21.2));
        messageQueue.add(message);

        message = new Message("test1");
        message.setMessageBody("test1 body");
        message.setItem("test1");
        message.setNumberOfSales(1);
        message.setSales(new BigDecimal(100.2));
        message.setAdjustment(Adjustment.ADD);
        messageQueue.add(message);

        MessageHandler msgHdlr = new MessageHandler(messageQueue,50);

        msgHdlr.getSalesRepository().getMessageStats().put("test1",null);
        msgHdlr.run();
        assertEquals(new BigDecimal( 121.4).setScale(2,BigDecimal.ROUND_DOWN),msgHdlr.getSalesRepository().getMessageStats().get("test1"));

    }

    // test  item types and SUBTRACT adjustment
    @Test
    public void testValidSalesSubtract(){
        Queue<Message> messageQueue = new PriorityQueue(idComparator);

        Message message = new Message("test1");
        message.setMessageBody("test1 body");
        message.setItem("test1");
        message.setNumberOfSales(1);
        message.setSales(new BigDecimal(100.4));
        messageQueue.add(message);

        message = new Message("test1");
        message.setMessageBody("test1 body");
        message.setItem("test1");
        message.setNumberOfSales(1);
        message.setSales(new BigDecimal(50.2));
        message.setAdjustment(Adjustment.SUBTRACT);
        messageQueue.add(message);

        MessageHandler msgHdlr = new MessageHandler(messageQueue,50);

        msgHdlr.getSalesRepository().getMessageStats().put("test1",null);
        msgHdlr.run();
        assertEquals(new BigDecimal( 50.2).setScale(2,BigDecimal.ROUND_DOWN),msgHdlr.getSalesRepository().getMessageStats().get("test1"));

    }
    // test multiple item types and the total sales with single sale each
    @Test
    public void testValidSalesMultipleMessages(){

        Queue<Message> messageQueue = new PriorityQueue(idComparator);
;
        Message message = new Message("test1");
        message.setMessageBody("test1 body");
        message.setItem("test1");
        message.setNumberOfSales(1);
        message.setSales(new BigDecimal(21.2));
        messageQueue.add(message);

        message = new Message("test1");
        message.setMessageBody("test1 body");
        message.setItem("test1");
        message.setNumberOfSales(1);
        message.setSales(new BigDecimal(100.2));
        message.setAdjustment(Adjustment.ADD);
        messageQueue.add(message);

        message = new Message("test2");
        message.setMessageBody("test2 body");
        message.setItem("test2");
        message.setNumberOfSales(1);
        message.setSales(new BigDecimal(234.2));
        messageQueue.add(message);

        message = new Message("test2");
        message.setMessageBody("test2 body");
        message.setItem("test2");
        message.setNumberOfSales(1);
        message.setSales(new BigDecimal(100.2));

        messageQueue.add(message);

        MessageHandler msgHdlr = new MessageHandler(messageQueue,50);

        msgHdlr.getSalesRepository().getMessageStats().put("test1",null);

        msgHdlr.run();

        assertEquals( new BigDecimal(121.4).setScale(2,BigDecimal.ROUND_DOWN),msgHdlr.getSalesRepository().getMessageStats().get("test1"));
        assertEquals(new BigDecimal( 334.394).setScale(2,BigDecimal.ROUND_DOWN),msgHdlr.getSalesRepository().getMessageStats().get("test2"));

    }

    // test multiple item types and the total sales with multiple sales each
    @Test
    public void testValidSalesMultipleSalesInSameMessage(){


        Queue<Message> messageQueue = new PriorityQueue(idComparator);

        Message message = new Message("test1");
        message.setMessageBody("test1 body");
        message.setItem("test1");
        message.setNumberOfSales(2);
        message.setSales(new BigDecimal(21.2));
        messageQueue.add(message);

        message = new Message("test1");
        message.setMessageBody("test1 body");
        message.setItem("test1");
        message.setNumberOfSales(1);
        message.setSales(new BigDecimal(100.2));;
        messageQueue.add(message);

        message = new Message("test2");
        message.setMessageBody("test2 body");
        message.setItem("test2");
        message.setNumberOfSales(2);
        message.setSales(new BigDecimal(234.2));
        messageQueue.add(message);

        message = new Message("test2");
        message.setMessageBody("test2 body");
        message.setItem("test2");
        message.setNumberOfSales(1);
        message.setSales(new BigDecimal(100.2));

        messageQueue.add(message);

        MessageHandler msgHdlr = new MessageHandler(messageQueue,50);

        msgHdlr.run();

        assertEquals( new BigDecimal(142.604).setScale(2,BigDecimal.ROUND_DOWN),msgHdlr.getSalesRepository().getMessageStats().get("test1"));
        assertEquals(new BigDecimal( 568.59).setScale(2,BigDecimal.ROUND_DOWN),msgHdlr.getSalesRepository().getMessageStats().get("test2"));

    }

    //test should terminate the process after 50 messages and not process the 51st message
    @Test
    public void testTermnationOnThreshold(){
;
        Queue<Message> messageQueue = new PriorityQueue(idComparator);
        Message message = new Message("test1");

        for(int i = 1 ; i <=60 ; i++) {
            message.setMessageBody("test1 body");
            message.setItem("test1");
            message.setNumberOfSales(1);
            message.setSales(new BigDecimal(1));
            messageQueue.add(message);

            message = new Message("test1");
        }

        MessageHandler msgHdlr = new MessageHandler(messageQueue,50);

        msgHdlr.getSalesRepository().getMessageStats().put("test1",null);
        msgHdlr.run();
        assertEquals( new BigDecimal(50).setScale(2,BigDecimal.ROUND_DOWN),msgHdlr.getSalesRepository().getMessageStats().get("test1"));

    }
    public static Comparator<Message> idComparator = new Comparator<Message>(){

        @Override
        public int compare(Message c1, Message c2) {
            return  (c1.messageID().compareTo( c2.messageID()));
        }
    };

}
