package hand.market;

import hand.agent.Buyer;
import hand.agent.Seller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

class Pair<K,V> {
    public K key;
    public V value;
    Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}

public class Market {
    public ArrayList<Buyer> buyers;
    public ArrayList<Seller> sellers;

    public Market(int nb, ArrayList<Double> fb, int ns, ArrayList<Double> fs) {
        buyers = createBuyers(nb, fb);
        sellers = createSellers(ns, fs);
    }
    
    private ArrayList<Buyer> createBuyers(int n, ArrayList<Double> f) {
        // TODO sub-problem 3
        ArrayList<Buyer> buyers = new ArrayList<>(n);

        for(int i=0; i<n; i++){
            double poly =0;
            for(int idx =0; idx<f.size(); idx++){
                poly = poly + f.get(idx)*Math.pow((double)(i+1)/n, f.size() - idx-1);
            }
            buyers.add(i, new Buyer(poly));
        }
        return buyers;
    }

    private ArrayList<Seller> createSellers(int n, ArrayList<Double> f) {
        // TODO sub-problem 3
        ArrayList<Seller> sellers = new ArrayList<>(n);
        for(int i=0; i<n; i++){
            double poly =0;
            for(int idx =0; idx<f.size(); idx++){
                poly = poly + f.get(idx)*Math.pow((double)(i+1)/n, f.size() - idx-1);
            }
            sellers.add(i, new Seller(poly));
        }
        return sellers;
    }

    private ArrayList<Pair<Seller, Buyer>> matchedPairs(int day, int round) {
        ArrayList<Seller> shuffledSellers = new ArrayList<>(sellers);
        ArrayList<Buyer> shuffledBuyers = new ArrayList<>(buyers);
        Collections.shuffle(shuffledSellers, new Random(71 * day + 43 * round + 7));
        Collections.shuffle(shuffledBuyers, new Random(67 * day + 29 * round + 11));
        ArrayList<Pair<Seller, Buyer>> pairs = new ArrayList<>();
        for (int i = 0; i < shuffledBuyers.size(); i++) {
            if (i < shuffledSellers.size()) {
                pairs.add(new Pair<>(shuffledSellers.get(i), shuffledBuyers.get(i)));
            }
        }
        return pairs;
    }

    public double simulate() {
        // TODO sub-problem 2 and 3
        double sumOfExchanges = 0;
        int numOfTransactions =0;
        for (int day = 1; day <= 1000; day++) { // do not change this line
            sumOfExchanges=0;
            numOfTransactions=0;
            for (int round = 1; round <= 10; round++) { // do not change this line
                ArrayList<Pair<Seller, Buyer>> pairs = matchedPairs(day, round); // do not change this line
                for(int i=0; i<pairs.size(); i++) {
                    if (pairs.get(i).value.willTransact(pairs.get(i).key.getExpectedPrice()) && pairs.get(i).key.willTransact(pairs.get(i).key.getExpectedPrice())) {
                        pairs.get(i).key.makeTransaction();
                        pairs.get(i).value.makeTransaction();
                    }
                }
            }
            for(int i=0; i<buyers.size(); i++)
                buyers.get(i).reflect();
            for(int i=0; i<sellers.size(); i++) {
                if(!sellers.get(i).willTransact(sellers.get(i).getExpectedPrice())) {
                    sumOfExchanges = sumOfExchanges + sellers.get(i).getExpectedPrice();
                    numOfTransactions++;
                }
                sellers.get(i).reflect();
            }
        }
        return sumOfExchanges/numOfTransactions;
    }
}
