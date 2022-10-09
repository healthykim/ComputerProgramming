#include "admin_ui.h"
#include <iostream>
#include <string>


using namespace std;

AdminUI::AdminUI(ShoppingDB &db, std::ostream& os): UI(db, os) {
}

void AdminUI::add_product(std::string name, int price) {
    // TODO: For problem 1-1
    if(price<=0){
        os<<"ADMIN_UI: Invalid price."<<endl;
    }
    else {
        Product* productp = new Product(name, price);
        db.set_product(productp);
        os << "ADMIN_UI: " << name << " is added to the database." << endl;
    }
}

void AdminUI::edit_product(std::string name, int price) {
    // TODO: For problem 1-1
     bool name_valid = false;
     std::vector<Product *> products = db.get_products();
     auto it = products.begin();
     while (it != products.end()) {
         if ((*it)->name == name) {
             name_valid = true;
             break;
         }
         it++;
     }
     if(name_valid == false){
         os<<"ADMIN_UI: Invalid product name."<<endl;
     }
     else{
         // modify
         if(price<=0){
             os<<"ADMIN_UI: Invalid price."<<endl;
         }
         else if(price>0) {
             (*it)->price = price;
             os << "ADMIN_UI: " << name << " is modified from the database." << endl;
         }
     }
}

void AdminUI::list_products() {
    // TODO: For problem 1-1
    os<<"ADMIN_UI: "<<"Products: [";
    std::vector<Product *> products = db.get_products();

    if(products.empty()){
        os<<"]"<<endl;
    }
    else {
        auto it = products.begin();
        while (it != products.end() - 1) {
            os << **it << ", ";
            it++;
        }
        os << **it;
        os << "]" << endl;
    }


}
/*
ostream& operator<<(ostream& os, const AdminUI& ad)
{
    os<<"ADMIN_UI :";
    return os;
}
 */