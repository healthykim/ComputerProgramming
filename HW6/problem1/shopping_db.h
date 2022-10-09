#ifndef PROBLEM1_SHOPPING_DB_H
#define PROBLEM1_SHOPPING_DB_H

#include <string>
#include <vector>
#include "user.h"
#include "product.h"

class ShoppingDB {
public:
    ShoppingDB();
    void set_product(Product* product);
    void set_user(User* user);
    std::vector<Product*>& get_products();
    std::vector<User*>& get_users();

private:
    std::vector<User*> users;
    std::vector<Product*> products;
};

#endif //PROBLEM1_SHOPPING_DB_H
