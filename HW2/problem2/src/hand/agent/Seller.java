package hand.agent;

public class Seller extends Agent {

    public Seller(double minimumPrice) {
        super(minimumPrice);
    }

    @Override
    public boolean willTransact(double price) {
        // TODO sub-problem 1
        if(hadTransaction==false && price>=expectedPrice)
            return true;
        else
            return false;
    }

    @Override
    public void reflect() {
        // TODO sub-problem 1
        if(hadTransaction==true)
            expectedPrice=expectedPrice+adjustment;
        else {
            expectedPrice = expectedPrice - adjustment;
        }
        if(expectedPrice<=priceLimit)
            expectedPrice=priceLimit;
        hadTransaction=false;
        return;
    }
}
