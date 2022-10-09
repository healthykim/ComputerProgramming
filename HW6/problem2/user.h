//
// Created by 문보설 on 2020/12/11.
//

#ifndef PROBLEM2_USER_H
#define PROBLEM2_USER_H
#include <iostream>

using namespace std;

class User{
public:
    string id;
    User();
    User(string id, string password);

private:
    string password;
};

#endif //PROBLEM2_USER_H
