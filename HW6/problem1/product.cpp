#include "product.h"
#include <iostream>

Product::Product(std::string name, int price): name(name), price(price) { }

std::ostream& operator<<(std::ostream& os, const Product& product){
    os<<'('<<product.name<<", "<<product.price<<')';
    return os;
}
