#include <vector>
#include <string>
#include <unordered_set>
#include "client_ui.h"
#include "product.h"
#include "./user.h"
#include <algorithm>
#include <cmath>

using namespace std;

ClientUI::ClientUI(ShoppingDB &db, std::ostream& os) : UI(db, os), current_user() { }

ClientUI::~ClientUI() {
    delete current_user;
}

void ClientUI::signup(std::string username, std::string password, bool premium) {
    // TODO: For problem 1-2
    if(premium){
        PremiumUser* userp = new PremiumUser(username, password);
        //PremiumUser* userp = &user;
        db.set_user(userp);
    }
    else {
        NormalUser* userp = new NormalUser(username, password);
        //NormalUser* userp = &user;
        db.set_user(userp);
    }
    os<<"CLIENT_UI: "<<username<<" is signed up."<<endl;
}

void ClientUI::login(std::string username, std::string password) {
    // TODO: For problem 1-2
    if(current_user != nullptr){
        os<<"CLIENT_UI: Please logout first."<<endl;
    }
    else{
        bool validID = false;
        std::vector<User*> users = db.get_users();
        auto it = users.begin();
        while(it != users.end()){
            if((*it)->name==username){
                if((*it)->passwordcheck(password)){
                    os<<"CLIENT_UI: "<<username<<" is logged in."<<endl;
                    current_user = *it;
                    validID = true;
                }
                else {
                    os << "CLIENT_UI: Invalid username or password." << endl;
                    validID = true;
                }
            }
            if(validID)
                break;
            it++;
        }
        if(!validID){
            os<<"CLIENT_UI: Invalid username or password."<<endl;
        }
    }
}

void ClientUI::logout() {
    // TODO: For problem 1-2
    if(current_user != nullptr) {
        os << "CLIENT_UI: " << current_user->name << " is logged out." << endl;
        current_user = nullptr;
    }
    else
        os<<"CLIENT_UI: There is no logged-in user."<<endl;

}

void ClientUI::add_to_cart(std::string product_name) {
    // TODO: For problem 1-2
    if(current_user == nullptr){
        os<<"CLIENT_UI: Please login first."<<endl;
    }
    else{
        bool name_valid = false;
        std::vector<Product *> products = db.get_products();
        std::vector<Product *>::iterator it = products.begin();
        while (it != products.end()) {
            if ((*it)->name == product_name) {
                name_valid = true;
                break;
            }
            it++;
        }
        if (name_valid == false) {
            os << "CLIENT_UI: Invalid product name." << endl;
        }
        else {
            current_user->cart.push_back(*it);
            os <<  "CLIENT_UI: "<< (*it)->name << " is added to the cart." << endl;
        }
    }

}

void ClientUI::list_cart_products() {
    // TODO: For problem 1-2.
    if(current_user == nullptr){
        os<<"CLIENT_UI: Please login first."<<endl;
    }
    else{
        bool premium = current_user->type==0;
        os<<"CLIENT_UI: "<<"Cart: [";
        if(current_user->cart.empty()){
            os<<"]"<<endl;
        }
        else {
            auto it = (current_user->cart).begin();
            while (it != (current_user->cart).end() - 1) {
                if (premium) {
                    double discounted_price = ((*it)->price) * 0.9;
                    os << '(' << (*it)->name << ", " << (int)floor(discounted_price + 0.5) << "), ";
                } else {
                    os << '(' << (*it)->name << ", " << (*it)->price << "), ";
                }
                it++;
            }
            if (premium) {
                double discounted_price = ((*it)->price) * 0.9;
                os << '(' << (*it)->name << ", " << (int)floor(discounted_price + 0.5) << ")";
            } else {
                os << '(' << (*it)->name << ", " << (*it)->price << ")";
            }
            os << "]" << endl;
        }
    }
}

void ClientUI::buy_all_in_cart() {
    // TODO: For problem 1-2
    if(current_user == nullptr){
        os<<"CLIENT_UI: Please login first."<<endl;
    }
    else{
        if(current_user->cart.empty()){
            os<<"CLIENT_UI: Cart purchase completed. Total price: "<<0<<"."<<endl;
        }
        else{
            bool premium = current_user->type==0;
            os<<"CLIENT_UI: "<<"Cart purchase completed. Total price: ";
            double total_price_tmp=0;
            int total_price;
            auto it = (current_user->cart).begin();
           // current_user->history.insert(end(current_user->history), begin(current_user->cart),
            //                             end(current_user->cart));
            while (it != (current_user->cart).end()) {
                current_user->history.push_back(*it);
                total_price_tmp = total_price_tmp + (*it)->price;

                it++;
            }

            if(premium)
                total_price = (int)floor(total_price_tmp*0.9+0.5);
            else
                total_price = (int)total_price_tmp;

            /*
            int total_price=0;
            auto it = (current_user->cart).begin();
            while (it != (current_user->cart).end()) {
                if(premium){
                    double discounted_price = ((*it)->price)*0.9;
                    current_user->history.insert(end(current_user->history), begin(current_user->cart), end(current_user->cart));
                    total_price = total_price +floor(discounted_price+0.5);
                }
                else {
                    current_user->history.insert(end(current_user->history), begin(current_user->cart), end(current_user->cart));
                    total_price = total_price +(*it)->price;
                }
                it++;
            }
             */
            os<<total_price<<'.'<<endl;
            current_user->cart.clear();
        }
    }
}

void ClientUI::buy(std::string product_name) {
    // TODO: For problem 1-2
    if(current_user == nullptr){
        os<<"CLIENT_UI: Please login first."<<endl;
    }
    else {
        bool name_valid = false;
        std::vector<Product *> products = db.get_products();
        std::vector<Product *>::iterator it = products.begin();
        while (it != products.end()) {
            if ((*it)->name == product_name) {
                name_valid = true;
                break;
            }
            it++;
        }
        if (name_valid == false) {
            os << "CLIENT_UI: Invalid product name." << endl;
        } else {
            // buy
            bool premium = current_user->type==0;
            if(premium){
                double discounted_price = ((*it)->price)*0.9;
                current_user->history.push_back(*it);
                os << "CLIENT_UI: Purchase completed. Price: "<<(int)floor(discounted_price+0.5)<<"."<< endl;
            }
            else {
                current_user->history.push_back(*it);
                os << "CLIENT_UI: Purchase completed. Price: " << (*it)->price <<"."<< endl;
            }
        }
    }
}

bool compare(pair<User*, int>a, pair<User*, int>b) {
    return a.second> b.second;
}

void ClientUI::recommend_products() {
    // TODO: For problem 1-3.
    if(current_user == nullptr){
        os<<"CLIENT_UI: Please login first."<<endl;
    }
    else if(current_user->type == 1){
        //normal
        if(current_user->history.empty()){
            os << "CLIENT_UI: Recommended products: []"<<endl;
        }
        else {
            std::vector<Product *> history = current_user->history;
            std::vector<Product *> recommend;
            std::vector<Product *>::reverse_iterator it = history.rbegin();
            while (recommend.size() != 3 && it != history.rend()) {
                if (!recommend.empty()) {
                    if (find(recommend.begin(), recommend.end(), *it) == recommend.end()) {
                        recommend.push_back(*it);
                    }
                } else {
                    recommend.push_back(*it);
                }
                ++it;
            }
            os << "CLIENT_UI: Recommended products: [";
            if (recommend.empty()) {
                os << "]" << endl;
            } else {
                auto it2 = recommend.begin();
                while (it2 != recommend.end() - 1) {
                    os << **it2 << ", ";
                    it2++;
                }
                os << **it2;
                os << "]" << endl;
            }
        }
    }

    else{
        //premium

        vector<pair<User*, int>> similarity_list;
        vector<User*> user_list = db.get_users();
        vector<Product *> recommend;
        //cout<<"added"<<endl;

        if((current_user->history).empty()){
            auto it = user_list.begin();
            while(it!=user_list.end()&&recommend.size()!=3){
                if(!(*it)->history.empty()) {
                    if (recommend.empty()) {
                        recommend.push_back((*it)->history.back());
                    } else if (find(recommend.begin(), recommend.end(), (*it)->history.back()) == recommend.end()) {
                        recommend.push_back((*it)->history.back());
                    }
                    //cout << "added" << endl;
                }
                it++;
            }
        }
        else {
            //1. user sort
            auto it = user_list.begin();
            //erase duplicated product in current user's purchase list.
            //not to track duplicated product
            //unordered_set<Product*> myset(current_user->history.begin(), current_user->history.end());

            vector<Product *> myset;
            auto iter = current_user->history.begin();
            while (iter != current_user->history.end()) {
                if (find(myset.begin(), myset.end(), *iter) == myset.end()) {
                    myset.push_back(*iter);
                }
                iter++;
            }

            while (it != user_list.end()) {
                if (*it == current_user) {
                    it++;
                }
                else {
                    int similarity = 0;
                    auto its = (*it)->history.begin();
                    auto my = myset.begin();
                    while (my != myset.end()) {
                        if (find((*it)->history.begin(), (*it)->history.end(), *my) != (*it)->history.end()) {
                            similarity++;
                        }
                        /*
                        for(its= (*it)->history.begin();its!=(*it)->history.end();its++){
                            if(*its==*my){
                                //if once the product is matched, do not track another duplicated product.
                                //not to track duplicated product
                                similarity++;
                                break;
                            }
                        }
                         */
                        my++;
                    }
                    similarity_list.push_back(pair<User *, int>(*it, similarity));
                    it++;
                }
            }
            similarity_list.shrink_to_fit();
            stable_sort(similarity_list.begin(), similarity_list.end(), compare);
            //reverse(similarity_list.begin(), similarity_list.end());

            //2. 3 user recommend
            auto it2 = similarity_list.begin();
            while (recommend.size() != 3 && it2 != similarity_list.end()) {
                if(!(*it2->first).history.empty()) {
                    if (!recommend.empty()) {
                        if (find(recommend.begin(), recommend.end(), (*it2->first).history.back()) == recommend.end()) {
                            //cout<<(*it2->first).name<<endl;
                            //cout<<((*it2).second)<<endl;
                            recommend.push_back((*it2->first).history.back());
                        }
                    } else {
                        //cout<<(*it2->first).name<<endl;
                        //cout<<((*it2).second)<<endl;
                        recommend.push_back((*it2->first).history.back());
                    }
                }
                it2++;
            }
        }

        os<<"CLIENT_UI: Recommended products: [";
        if(recommend.empty()){
            os<<"]"<<endl;
        }
        else {
            auto it3 = recommend.begin();
            while (it3 != recommend.end() - 1) {
                double discounted_price = ((*it3)->price) * 0.9;
                os << '(' << (*it3)->name << ", " << (int)floor(discounted_price + 0.5) << "), ";
                it3++;
            }
            double discounted_price = ((*it3)->price) * 0.9;
            os << '(' << (*it3)->name << ", "<< (int)floor(discounted_price + 0.5) << ')';
            os << "]" << endl;
        }

    }

    //print

}


