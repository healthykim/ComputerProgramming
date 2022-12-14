package bank;

public class Session {

    private String sessionKey;
    private Bank bank;
    private boolean valid;
    Session(String sessionKey,Bank bank){
        this.sessionKey = sessionKey;
        this.bank = bank;
        valid = true;
    }
    public void setValid(boolean valid){
        this.valid = valid;
    }

    public boolean deposit(int amount) {
        //TODO: Problem 1.2
        if(valid) {
            bank.deposit(sessionKey, amount);
            return true;
        }
        else
            return false;
    }

    public boolean withdraw(int amount) {
        //TODO: Problem 1.2
        if(valid){
            bank.withdraw(sessionKey, amount);
            return true;
        }
        else
            return false;
    }

    public boolean transfer(String targetId, int amount) {
        //TODO: Problem 1.2
        if(valid){
            bank.transfer(sessionKey,targetId,amount);
            return true;
        }
        else
            return false;
    }

}
