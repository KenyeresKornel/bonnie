package com.cgi.bonnie.cli;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class PlaceOrderApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(PlaceOrderApplication.class, args);
    }

    @Autowired
    private PlaceOrderKafkaProducer placeOrderKafkaProducer;

    @Override
    public void run(String... args) throws Exception {
        String goods = getValue("Goods");
        while (!goods.isBlank() && !goods.equals("q")) {
            String quantity = getValue("Quantity");
            placeOrderKafkaProducer.placeOrder(goods, Integer.parseInt(quantity));
            goods = getValue("Goods");
        }
    }

    private static String getValue(String message) {
        System.out.print(message + ": ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
