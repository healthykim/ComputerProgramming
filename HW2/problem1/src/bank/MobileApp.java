package bank;

import security.Encryptor;
import security.Encrypted;
import security.Message;
import security.key.BankPublicKey;
import security.key.BankSymmetricKey;

public class MobileApp {

    private String randomUniqueStringGen(){
        return Encryptor.randomUniqueStringGen();
    }
    private final String AppId = randomUniqueStringGen();
    public String getAppId() {
        return AppId;
    }
    private BankSymmetricKey symKey;

    String id, password;
    public MobileApp(String id, String password){
        this.id = id;
        this.password = password;
    }

    public Encrypted<BankSymmetricKey> sendSymKey(BankPublicKey publickey){
        //TODO: Problem 1.3
        symKey = new BankSymmetricKey(randomUniqueStringGen());
        Encrypted Ekey = new Encrypted(symKey, publickey);
        return Ekey;
    }

    public Encrypted<Message> deposit(int amount){
        //TODO: Problem 1.3
        Message Dmsg = new Message("deposit", id, password, amount);
        Encrypted<Message> EncryptedMsg = new Encrypted<Message>(Dmsg, symKey);
        return EncryptedMsg;
    }

    public Encrypted<Message> withdraw(int amount){
        //TODO: Problem 1.
        Message Wmsg = new Message("withdraw", id, password, amount);
        Encrypted<Message> EncryptedMsg = new Encrypted<Message>(Wmsg, symKey);
        return EncryptedMsg;
    }

    public boolean processResponse(Encrypted<Boolean> obj) {
        //TODO: Problem 1.3
        if (obj == null)
            return false;
        else {
            if(obj.decrypt(symKey)==null){
                return false;
            }
            else
                return true;
        }
    }

}

