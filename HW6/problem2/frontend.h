//
// Created by 문보설 on 2020/12/11.
//

#ifndef PROBLEM2_FRONTEND_H
#define PROBLEM2_FRONTEND_H
#include <iostream>
#include <time.h>
#include "backend.h"
#include "user.h"
#include "post.h"

using namespace std;

class FrontEnd {
public:
    FrontEnd(std::istream& is, std::ostream& os);
    bool auth(string authInfo);
    void post(pair<string, string> pair);
    void recommend();
    void search(string command);
    User getUser();
    BackEnd backEnd;
    User user;
    static int parseDatetoInt(string date);
    static int parseTimetoInt(string time);
    static bool compare(string f1, string f2);

private:
    std::istream& is;
    std::ostream& os;

};
#endif //PROBLEM2_FRONTEND_H
