package bank;

import bank.event.*;

import java.util.LinkedList;
/*
On creating a personal savings account, a BankAccount object is created to
manage the account information of a client.
a client cannot directly access the BankAccount object.
Implement six member methods of the BankAccount class in the bank package (i.e.,
BankAccount , authenticate , deposit , withdraw , receive , send )
 */

class BankAccount {
    private String id;
    private String password;
    private int balance=0;
    LinkedList<Event> eventList = new LinkedList<Event>();

    BankAccount(String id, String password, int balance) {
        this.id = id;
        this.password = password;
        this.balance = balance;
        //balance will be zero without input
        //TODO: Problem 1.1
    }

    boolean authenticate(String password) {
        if (this.password == password)
            return true;
        return false;
    }

    void deposit(int amount) {
        this.balance += amount;
        eventList.addLast(new DepositEvent());
        //TODO: Problem 1.1
    }

    boolean withdraw(int amount) {
        //TODO: Problem 1.1
        if(balance<amount)
            return false;
        else {
            this.balance -= amount;
            eventList.addLast(new WithdrawEvent());
            return true;
        }
    }

    void receive(int amount) {
        //TODO: Problem 1.1
        this.balance += amount;
        eventList.addLast(new ReceiveEvent());
    }

    boolean send(int amount) {
        //TODO: Problem 1.1
        if(balance<amount)
            return false;
        else {
            this.balance -= amount;
            eventList.addLast(new SendEvent());
            return true;
        }
    }

    Event[] eventList(){
        Event[] events = eventList.toArray(new Event[eventList.size()]);
        return events;
    }

    int getBalance(){
        return balance;
    }

}
