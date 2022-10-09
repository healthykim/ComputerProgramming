#ifndef PROBLEM1_PRODUCT_H
#define PROBLEM1_PRODUCT_H

#include <string>

struct Product {
    Product(std::string name, int price);
    const std::string name;
    int price;
};

std::ostream& operator<<(std::ostream& os, const Product& product);


#endif //PROBLEM1_PRODUCT_H
