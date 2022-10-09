#include "shopping_db.h"
#include <algorithm>
#include <iostream>

ShoppingDB::ShoppingDB(): products(), users() {}

void ShoppingDB::set_product(Product* product){
    products.push_back(product);
}

void ShoppingDB::set_user(User *user){
    users.push_back(user);
}

std::vector<Product*>& ShoppingDB::get_products(){
    return products;
}


std::vector<User*>& ShoppingDB::get_users(){
    return users;
}

/* trash
Product* ShoppingDB::get_product(Product *product) {
    std::vector<Product*>::iterator it;
    it = find(products.begin(), products.end(), *product);
    return *it;
}

User* ShoppingDB::get_user(User *user) {
    std::vector<User*>::iterator it;
    it = find(users.begin(), users.end(), *user);
    return *it;
}
 */