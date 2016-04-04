package org.baddev.currency.exchange;

import org.baddev.currency.exchange.impl.nbu.NBUScheduledExchanger;
import org.baddev.currency.exchange.job.ExchangeJob;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
public class Test {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("exchanger-applicationContext.xml");
        ExchangeJob c = ctx.getBean("NBUExchanger", NBUScheduledExchanger.class);
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
