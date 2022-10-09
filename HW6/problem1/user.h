#ifndef PROBLEM1_USER_H
#define PROBLEM1_USER_H

#include <string>
#include <vector>
#include "product.h"

enum ClientType {
    premium = 0,
    normal = 1
};

class User {
public:
    User(std::string name, std::string password);
    const std::string name;
    ClientType type;
    bool passwordcheck(std::string password);
    std::vector<Product*> cart;
    std::vector<Product*> history;
private:
    std::string password;
};

class NormalUser : public User {
public:
    NormalUser(std::string name, std::string password);
};

class PremiumUser : public User {
public:
    PremiumUser(std::string name, std::string password);
};

#endif //PROBLEM1_USER_H
