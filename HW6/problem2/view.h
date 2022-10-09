//
// Created by 문보설 on 2020/12/11.
//

#ifndef PROBLEM2_VIEW_H
#define PROBLEM2_VIEW_H

#include <ostream>
#include <istream>
#include "post.h"

using namespace std;

class View{
public:
    View(std::istream& is, std::ostream& os);
    string getUserInput(string prompt);

protected:
    std::istream& is;
    std::ostream& os;
};

class AuthView : public View{
public:
    AuthView(std::istream& is, std::ostream& os);
    AuthView(std::istream& is, std::ostream& os, string input);
    string getUserInput(string prompt);
};


class PostView : public View{
public:
    PostView(std::istream& is, std::ostream& os);
    PostView(std::istream& is, std::ostream& os, string input);
    pair<string, string> getPost(string prompt);
};

#endif //PROBLEM2_VIEW_H
