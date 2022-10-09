//
// Created by 문보설 on 2020/12/11.
//

#ifndef PROBLEM2_USERINTERFACE_H
#define PROBLEM2_USERINTERFACE_H
#include <iostream>
#include "frontend.h"
#include "view.h"

using namespace std;

class UserInterface {
public:
    UserInterface(std::istream& is, std::ostream& os);
    void run();

private:
    bool query(string command);
    string parseInstruction(string command);
    void post();
    void search(string command);
    void recommend();
    std::istream& is;
    std::ostream& os;
    FrontEnd frontEnd;
    AuthView authView;
    PostView postView;

};

#endif //PROBLEM2_USERINTERFACE_H
