//
// Created by 문보설 on 2020/12/11.
//

#ifndef PROBLEM2_POST_H
#define PROBLEM2_POST_H
#include <iostream>
#include <ctime>
#include <time.h>

using namespace std;

class Post{
public:
    Post();
    int id;
    string title;
    string contents;
    string time;
    void setID(int id);
    void settitle(string title);
    void setcontent(string contents);
    void settime(string time);

private:
};

#endif //PROBLEM2_POST_H
