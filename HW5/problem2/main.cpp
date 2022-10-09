#include <iostream>
#include <string>
#include <utility>
#include <set>
#include <vector>
#include <tuple>
#include <algorithm>
#include <cmath>

/* =======START OF PRIME-RELATED HELPERS======= */
/*
 * The code snippet below AS A WHOLE does the primality
 * test and integer factorization. Feel free to move the
 * code to somewhere more appropriate to get your codes
 * more structured.
 *
 * You don't have to understand the implementation of it.
 * But if you're curious, refer to the sieve of Eratosthenes
 *
 * If you want to just use it, use the following 2 functions.
 *
 * 1) bool is_prime(int num):
 *     * `num` should satisfy 1 <= num <= 999999
 *     - returns true if `num` is a prime number
 *     - returns false otherwise (1 is not a prime number)
 *
 * 2) std::multiset<int> factorize(int num):
 *     * `num` should satisfy 1 <= num <= 999999
 *     - returns the result of factorization of `num`
 *         ex ) num = 24 --> result = { 2, 2, 2, 3 }
 *     - if `num` is 1, it returns { 1 }
 */

const int PRIME_TEST_LIMIT = 999999;
int sieve_of_eratosthenes[PRIME_TEST_LIMIT + 1];
bool sieve_calculated = false;

void make_sieve() {
    sieve_of_eratosthenes[0] = -1;
    sieve_of_eratosthenes[1] = -1;
    for(int i=2; i<=PRIME_TEST_LIMIT; i++) {
        sieve_of_eratosthenes[i] = i;
    }
    for(int i=2; i*i<=PRIME_TEST_LIMIT; i++) {
        if(sieve_of_eratosthenes[i] == i) {
            for(int j=i*i; j<=PRIME_TEST_LIMIT; j+=i) {
                sieve_of_eratosthenes[j] = i;
            }
        }
    }
    sieve_calculated = true;
}

bool is_prime(int num) {
    if (!sieve_calculated) {
        make_sieve();
    }
    return sieve_of_eratosthenes[num] == num;
}

std::multiset<int> factorize(int num) {
    if (!sieve_calculated) {
        make_sieve();
    }
    std::multiset<int> result;
    while(num > 1) {
        result.insert(sieve_of_eratosthenes[num]);
        num /= sieve_of_eratosthenes[num];
    }
    if(result.empty()) {
        result.insert(1);
    }
    return result;
}

/* =======END OF PRIME-RELATED HELPERS======= */

/* =======START OF std::string LITERALS======= */
/* Use this code snippet if you want */

const std::string MAXIMIZE_GAIN = "Maximize-Gain";
const std::string MINIMIZE_LOSS = "Minimize-Loss";
const std::string MINIMIZE_REGRET = "Minimize-Regret";

/* =======END OF std::string LITERALS======= */


/* =======START OF TODOs======= */

std::pair<int, int> number_fight(int a, int b) {
    // TODO 2-1
    std::multiset<int> FA;
    std::multiset<int> FB;

    FA = factorize(a);
    FB = factorize(b);
    std::vector<int> FG(FA.size()+ FB.size());

    auto it1 = std::set_intersection(FA.begin(), FA.end(), FB.begin(), FB.end(), FG.begin());
    FG.resize(it1-FG.begin());
    std::sort(FG.begin(), FG.end());
    FG.erase(std::unique(FG.begin(), FG.end()), FG.end());

    int G=1;
    auto it2 = FG.begin();
    while (it2!=FG.end()){
        G = G**it2;
        it2++;
    }

    int changedA, changedB;
    changedA = a/G;
    changedB = b/G;

    return std::pair<int, int>(changedA, changedB);
}


std::pair<int, int> fight(int a, int b, bool aGonnaFight, bool bGonnaFight) {
    // Implemented for 2-2
    //1. decide whether to fight or not, respectively
    // A
    if(aGonnaFight&&bGonnaFight){
        return number_fight(a, b);
    }
    else if(aGonnaFight&&!bGonnaFight){
        int damage = b-number_fight(a,b).second;
        std::multiset<int> FB = factorize(b);
        if(FB.find(7)!=FB.end()){
            int newA;
            if(a-floor(damage/2)<0){
                newA = 1;
            }
            else
                newA = a-floor(damage/2);
            return std::pair<int, int>(newA, b-floor(damage/2));
        }
        else
            return std::pair<int, int>(a, b-damage);

    }
    else if(!aGonnaFight&&bGonnaFight){
        int damage = a-number_fight(a,b).first;
        std::multiset<int> FA = factorize(a);
        if(FA.find(7)!=FA.end()){
            int newB;
            if(b-floor(damage/2)<0){
                newB = 1;
            }
            else
                newB = b-floor(damage/2);
            return std::pair<int, int>(a-floor(damage/2), newB);
        }
        else
            return std::pair<int, int>(a-damage, b);

    }
    else
        return std::pair<int, int>(a, b);
}

bool decision_maker(int me, int opponent){
    bool GonnaFight = false;
    bool Decision1 = false;
    bool Decision2 = false;
    if(fight(me,opponent,1,1).first>=fight(me,opponent,0,1).first)
        Decision1 = true;
    else
        Decision1 = false;


    if(fight(me,opponent,1,0).first>=fight(me,opponent,0,0).first)
        Decision2 = true;
    else
        Decision2 = false;

    if(Decision1 == Decision2)
        GonnaFight = Decision1;
    else{
        if(opponent>me)
            GonnaFight = true;
        else
            GonnaFight = false;
    }
    return GonnaFight;

}


std::pair<int, int> number_vs_number(int a, int b) {
    // TODO 2-2
    //1. decide whether to fight or not
    bool aGonnaFight = decision_maker(a, b);
    bool bGonnaFight = decision_maker(b, a);
    return fight(a, b, aGonnaFight, bGonnaFight);
}

int player_decision_maker (std::string mytype, std::multiset<int> me, std::multiset<int> opponent){
    if(mytype == MAXIMIZE_GAIN){
        int max_gain;
        int max_gain_bool = false;
        int card = -1;
        auto itr1 = me.begin();
        while(itr1 != me.end()){
            auto itr2 = opponent.begin();
            int max_among_row;
            bool max_among_row_bool = false;
            while (itr2 != opponent.end()){
                int result = number_vs_number(*itr1, *itr2).first-*itr1;
                if(!max_among_row_bool) {
                    max_among_row = result;
                    max_among_row_bool = true;
                }
                else if(result>max_among_row)
                    max_among_row=result;
                itr2++;
            }
            if(card==-1){
                card = *itr1;
            }
            if(!max_gain_bool){
                max_gain_bool = true;
                max_gain = max_among_row;
            }
            else if(max_among_row>=max_gain){
                if(max_among_row==max_gain&&max_gain_bool)
                    card = std::min(*itr1, card);
                else
                    card = *itr1;
                max_gain = max_among_row;
            }
            itr1++;
        }

        return card;
    }
    else if(mytype == MINIMIZE_LOSS){
        int min_loss;
        bool min_loss_bool = false;
        int card = -1;
        auto itr1 = me.begin();
        while(itr1 != me.end()){
            auto itr2 = opponent.begin();
            int worst_among_row;
            bool worst_among_bool = false;
            while (itr2 != opponent.end()){
                int result = number_vs_number(*itr1, *itr2).first-*itr1;
                if(!worst_among_bool) {
                    worst_among_row = result;
                    worst_among_bool = true;
                }
                else if(worst_among_row>result)
                    worst_among_row=result;
                itr2++;
            }
            if(card==-1){
                card = *itr1;
            }
            if(!min_loss_bool){
                min_loss = worst_among_row;
                min_loss_bool = true;
            }
            else if(min_loss<=worst_among_row){
                if(worst_among_row==min_loss&&min_loss!=1)
                    card = std::min(*itr1, card);
                else
                    card = *itr1;
                min_loss = worst_among_row;
            }
            itr1++;
        }
        return card;
    }
    else if(mytype==MINIMIZE_REGRET){
        int best_result = 1;
        bool best_result_bool = false;
        int card;
        int card_bool=false;
        // iterate to find minimum regret.
        // 1. find the worst case of this row
        // 2. find the best result of other row
        // 3. calculate regret and find the minimun regret
        int minimum_regret;
        bool minimum_regret_bool=false;
        auto itr1 = me.begin();
        while(itr1 != me.end()){
            best_result;
            best_result_bool = false;
            auto itr2 = me.begin();
            while (itr2 != me.end()){
                if(itr1==itr2) {
                    itr2++;
                    continue;
                }
                auto itr3 = opponent.begin();
                int max_among_row;
                bool max_among_row_bool = false;
                while (itr3 != opponent.end()){
                    int result =number_vs_number(*itr2, *itr3).first-*itr2;
                    if(!max_among_row_bool) {
                        max_among_row = result;
                        max_among_row_bool = true;
                    }
                    else if(result>max_among_row)
                        max_among_row=result;
                    itr3++;
                }
                if(!best_result_bool|| max_among_row>best_result){
                    best_result = max_among_row;
                    best_result_bool = true;
                }
                itr2++;
            }

            int regret;
            int worst_among_row;
            bool worst_among_row_bool = false;
            auto itr3 = opponent.begin();
            while (itr3 != opponent.end()){
                int result =number_vs_number(*itr1, *itr3).first-*itr1;
                if(!worst_among_row_bool) {
                    worst_among_row = result;
                    worst_among_row_bool = true;
                }
                else if(result<worst_among_row)
                    worst_among_row=result;
                itr3++;
            }
            regret = best_result - worst_among_row;
            if(!card_bool){
                card = *itr1;
                card_bool = true;
            }
            if(!minimum_regret_bool){
                minimum_regret=regret;
                minimum_regret_bool=true;
            }
            else if(minimum_regret>=regret){
                if(minimum_regret==regret)
                    card = std::min(*itr1, card);
                else
                    card = *itr1;
                minimum_regret= regret;
            }
            itr1++;
        }
        return card;

    }
    return 0;

}

std::pair<std::multiset<int>, std::multiset<int>> player_battle(
        std::string type_a, std::multiset<int> a, std::string type_b, std::multiset<int> b) {
    // TODO 2-3

    int a_choice = player_decision_maker(type_a, a, b);
    int b_choice = player_decision_maker(type_b, b, a);
    a.erase(a.find(a_choice));
    b.erase(b.find(b_choice));

    std::pair<int, int> result =number_vs_number(a_choice, b_choice);
    a.insert(result.first);
    b.insert(result.second);

    return std::pair<std::multiset<int>, std::multiset<int>>(a,b);
}

std::pair<std::multiset<int>, std::multiset<int>> player_vs_player(
    std::string type_a, std::multiset<int> a, std::string type_b, std::multiset<int> b
) {
    while(true){
        std::multiset<int> a_copy (a);
        std::multiset<int> b_copy (b);
        std::pair<std::multiset<int>, std::multiset<int>> pair = player_battle(type_a, a, type_b, b);
        a = pair.first;
        b = pair.second;
        if(a_copy==a&&b_copy==b) {
            break;
        }
    }
    // TODO 2-4
    return std::pair<std::multiset<int>, std::multiset<int>>(a, b);
}

int tournament(std::vector<std::pair<std::string, std::multiset<int>>> players) {
    // TODO 2-5
    std::vector<int> winners;
    for(int i=0; i<players.size(); i++){
        winners.push_back(i);
    }
    while(winners.size()!=1){
        sort(winners.begin(), winners.end());
        std::vector<int> next_round_players_copy;
        int bujeonsung;
        bool bujeon=false;
        if(winners.size()%2==1){
            bujeon = true;
            bujeonsung = winners.back();
            winners.pop_back();
        }
        auto itr = winners.begin();
        while(itr!=winners.end()) {
            auto itrsecond = itr+1;
            std::pair<std::multiset<int>, std::multiset<int>> result;
            result = player_vs_player(players.at(*itr).first, players.at(*itr).second, players.at(*itrsecond).first,
                                      players.at(*itrsecond).second);

            auto itr1 = result.first.begin();
            auto itr2 = result.second.begin();
            int first_result = 0;
            int second_result = 0;
            while (itr1 != result.first.end()) {
                first_result = first_result + *itr1;
                itr1++;
            }
            while (itr2 != result.second.end()) {
                second_result = second_result + *itr2;
                itr2++;
            }
            if (first_result >= second_result) {
                next_round_players_copy.push_back(*itr);
            } else if (first_result < second_result) {
                next_round_players_copy.push_back(*itrsecond);
            }
            itr++;
            if(itr==winners.end()){
                break;
            }
            itr++;
            if(itr==winners.end()){
                break;
            }
        }

        winners.clear();
        for(int i : next_round_players_copy){
            winners.push_back(i);
        }
        winners.resize(next_round_players_copy.size());
        if(bujeon) {
            winners.push_back(bujeonsung);
            winners.resize(next_round_players_copy.size()+1);
        }
    }
    int winner = winners.front();
    return winner;
}

int steady_winner(std::vector<std::pair<std::string, std::multiset<int>>> players) {
    // TODO 2-6
    std::vector<int> IDs;
    for(int i=0; i<players.size(); i++){
        IDs.push_back(i);
    }
    std::vector<int> IDs_rotate(IDs);
    int winners[players.size()];
    while(true){
        int winner = tournament(players);
        winners[IDs_rotate.front()] = (winner+IDs_rotate.front())%(IDs_rotate.size());

        int last_id = IDs_rotate.back();
        IDs_rotate.pop_back();
        IDs_rotate.insert(IDs_rotate.begin(), last_id);
        std::pair<std::string, std::multiset<int>> last_player = players.back();
        players.pop_back();
        players.insert(players.begin(), last_player);
        if(IDs_rotate==IDs){
            break;
        }
        //std::cout<<IDs_rotate.back()<<std::endl;
        //std::cout<<(winner+IDs_rotate.back())%(IDs_rotate.size())<<std::endl;
    }
    /*
    for(int i : winners){
        std::cout<<i<<std::endl;
    }
     */

    int MAX_Count=0;
    int MAX_Count_id=0;
    auto itr = IDs.begin();
    while (itr != IDs.end()){
        //std::cout<<*itr<<std::endl;
        int count = std::count(winners, winners+players.size(), *itr);
        if(count>MAX_Count) {
            MAX_Count = count;
            MAX_Count_id = *itr;
            //std::cout<<count<<std::endl;
            //std::cout<<*itr<<std::endl;
        }
        if(count==MAX_Count){
            if(MAX_Count_id>*itr)
                MAX_Count_id = *itr;
        }
        itr++;
    }

    return MAX_Count_id;
}

/* =======START OF THE MAIN CODE======= */
/* Please do not modify the code below */

typedef std::pair<std::string, std::multiset<int>> player;

player scan_player() {
    std::multiset<int> numbers;
    std::string player_type; int size;
    std::cin >> player_type >> size;
    for(int i=0;i<size;i++) {
        int t; std::cin >> t; numbers.insert(t);
    }
    return make_pair(player_type, numbers);
}

void print_multiset(const std::multiset<int>& m) {
    for(int number : m) {
        std::cout << number << " ";
    }
    std::cout << std::endl;
}

int main() {
    int question_number; std::cin >> question_number;
    if (question_number == 1) {
        int a, b; std::cin >> a >> b;
        std::tie(a, b) = number_fight(a, b);
        std::cout << a << " " << b << std::endl;
    } else if (question_number == 2) {
        int a, b; std::cin >> a >> b;
        std::tie(a, b) = number_vs_number(a, b);
        std::cout << a << " " << b << std::endl;
    } else if (question_number == 3 || question_number == 4) {
        auto a = scan_player();
        auto b = scan_player();
        std::multiset<int> a_, b_;
        if (question_number == 3) {
            tie(a_, b_) = player_battle(
                    a.first, a.second, b.first, b.second
            );
        } else {
            tie(a_, b_) = player_vs_player(
                    a.first, a.second, b.first, b.second
            );
        }
        print_multiset(a_);
        print_multiset(b_);
    } else if (question_number == 5 || question_number == 6) {
        int num_players; std::cin >> num_players;
        std::vector<player> players;
        for(int i=0;i<num_players;i++) {
            players.push_back(scan_player());
        }
        int winner_id;
        if (question_number == 5) {
            winner_id = tournament(players);
        } else {
            winner_id = steady_winner(players);
        }
        std::cout << winner_id << std::endl;
    }
    return 0;
}

/* =======END OF MAIN CODE======= */