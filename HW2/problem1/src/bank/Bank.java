package bank;

import bank.event.Event;
import security.*;
import security.key.*;

import java.util.ArrayList;

/*
The Bank class is responsible for storing and managing multiple BankAccount objects for all clients.
A client can createAccount , deposit , withdraw and transfer from his/her account through a Bank object;
Implement five member methods of the Bank class in the bank package (i.e., deposit ,
withdraw , transfer , getEvents , getBalance )
 */

public class Bank {
    private int numAccounts = 0;
    final static int maxAccounts = 100;
    private BankAccount[] accounts = new BankAccount[maxAccounts];
    private String[] ids = new String[maxAccounts];

    public void createAccount(String id, String password) {
        createAccount(id, password, 0);
    }

    public void createAccount(String id, String password, int initBalance) {
        int accountId = numAccounts;
        accounts[accountId] = new BankAccount(id, password, initBalance);
        ids[accountId] = id;
        numAccounts+=1;
    }

    public boolean deposit(String id, String password, int amount) {
        //TODO: Problem 1.1
        BankAccount account = find(id);
        if(find(id)==null)
            return false;
        else if(account.authenticate(password)){
            account.deposit(amount);
            return true;
        }
        else
            return false;
    }

    public boolean withdraw(String id, String password, int amount) {
        //TODO: Problem 1.1
        BankAccount account = find(id);
        if(find(id)==null)
            return false;
        else if(account.authenticate(password)){
            account.withdraw(amount);
            return true;
        }
        else
            return false;
    }

    public boolean transfer(String sourceId, String password, String targetId, int amount) {
        //TODO: Problem 1.1
        BankAccount sourceAccount = find(sourceId);
        if(find(sourceId)==null)
            return false;
        else if(!sourceAccount.authenticate(password))
            return false;
        else if(find(targetId) == null)
            return false;
        else {
            BankAccount targetAccount = find(targetId);
            if (sourceAccount.send(amount)){
                targetAccount.receive(amount);
                return true;
            }
            else
                return false;
        }
    }

    public Event[] getEvents(String id, String password) {
        //TODO: Problem 1.1
        BankAccount account = find(id);
        if(find(id)==null)
            return null;
        else if(account.authenticate(password))
            return account.eventList();
        else
            return null;
    }

    public int getBalance(String id, String password) {
        //TODO: Problem 1.1
        BankAccount account = find(id);
        if(find(id)==null)
            return -1;
        else if(account.authenticate(password))
            return account.getBalance();
        else
            return -1;
    }

    private static String randomUniqueStringGen(){
        return Encryptor.randomUniqueStringGen();
    }
    private BankAccount find(String id) {
        for (int i = 0; i < numAccounts; i++) {
            if(ids[i].equals(id)){return accounts[i];};
        }
        return null;
    }
    final static int maxSessionKey = 100;
    int numSessionKey = 0;
    String[] sessionKeyArr = new String[maxSessionKey];
    BankAccount[] bankAccountmap = new BankAccount[maxSessionKey];
    String generateSessionKey(String id, String password){
        BankAccount account = find(id);
        if(account == null || !account.authenticate(password)){
            return null;
        }
        String sessionkey = randomUniqueStringGen();
        sessionKeyArr[numSessionKey] = sessionkey;
        bankAccountmap[numSessionKey] = account;
        numSessionKey += 1;
        return sessionkey;
    }
    BankAccount getAccount(String sessionkey){
        for(int i = 0 ;i < numSessionKey; i++){
            if(sessionKeyArr[i] != null && sessionKeyArr[i].equals(sessionkey)){
                return bankAccountmap[i];
            }
        }
        return null;
    }

    boolean deposit(String sessionkey, int amount) {
        //TODO: Problem 1.2
        if(getAccount(sessionkey)==null)
            return false;
        else {
            getAccount(sessionkey).deposit(amount);
            return true;
        }
    }

    boolean withdraw(String sessionkey, int amount) {
        //TODO: Problem 1.2
        if(getAccount(sessionkey)==null)
            return false;
        else{
            getAccount(sessionkey).withdraw(amount);
            return true;
        }
    }

    boolean transfer(String sessionkey, String targetId, int amount) {
        //TODO: Problem 1.2
        if(getAccount(sessionkey)==null)
            return false;
        else if(find(targetId)==null)
            return false;
        else {
            BankAccount targetAccount = find(targetId);
            BankAccount sourceAccount = getAccount(sessionkey);
            if (sourceAccount.send(amount)) {
                targetAccount.receive(amount);
                return true;
            }
            return false;
        }
    }


    private BankSecretKey secretKey;
    ArrayList<BankSymmetricKey> symKeys = new ArrayList<>(0);
    ArrayList<String> AppIds = new ArrayList<>(0);

    public BankPublicKey getPublicKey(){
        BankKeyPair keypair = Encryptor.publicKeyGen();
        secretKey = keypair.deckey;
        return keypair.enckey;
    }

    public void fetchSymKey(Encrypted<BankSymmetricKey> encryptedKey, String AppId){
        //TODO: Problem 1.3
        if(encryptedKey.decrypt(secretKey)==null||encryptedKey==null){

        }
        else if(AppIds.contains(AppId)){
            int idx = AppIds.indexOf(AppId);
            symKeys.remove(idx);
            symKeys.add(idx, encryptedKey.decrypt(secretKey));
        }
        else{
            symKeys.add(symKeys.size(),encryptedKey.decrypt(secretKey));
            AppIds.add(AppIds.size(),AppId);
        }
    }

    public Encrypted<Boolean> processRequest(Encrypted<Message> messageEnc, String AppId){
        //TODO: Problem 1.3
        if(!AppIds.contains(AppId))
            return null;
        else if(messageEnc == null)
            return null;
        else{
            int idx = AppIds.indexOf(AppId);
            BankSymmetricKey CorrectSymKey = symKeys.get(idx);
            Message messageDec = messageEnc.decrypt(CorrectSymKey);
            if(messageDec==null)
                return null;
            else{
                boolean success=false;
                if(messageDec.getRequestType()=="deposit")
                    success = deposit(messageDec.getId(),messageDec.getPassword(),messageDec.getAmount());
                else if(messageDec.getRequestType()=="withdraw")
                    success = withdraw(messageDec.getId(),messageDec.getPassword(),messageDec.getAmount());
                Encrypted<Boolean> su = new Encrypted<Boolean>(success, CorrectSymKey);
                return su;
            }

        }
    }


}