//
// Created by 문보설 on 2020/12/11.
//

#ifndef PROBLEM2_BACKEND_H
#define PROBLEM2_BACKEND_H
#include <istream>
#include <ostream>
#include <iostream>
#include <vector>
#include <unordered_set>
#include "user.h"
#include "post.h"

using namespace std;

class BackEnd {
public:
    BackEnd(std::istream& is, std::ostream& os);
    int fileNumber = 0;
    string getPassword(string id);
    int getLastPostId();
    void makePost(string userid, Post* post);
    vector<string> getFriends(string userid);
    vector<string> getUserPost(string userid);
    void printPost(string path);
    vector<string> getKeywordPost(unordered_set<string> keywords);
    static int parseDatetoInt(string date);
    static int parseTimetoInt(string time);
    static bool compare(pair<string, int> p1, pair<string, int> p2);
    void printSummary(string path);

private:
    std::istream& is;
    std::ostream& os;
};

#endif //PROBLEM2_BACKEND_H
