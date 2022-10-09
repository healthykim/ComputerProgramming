//
// Created by 문보설 on 2020/12/11.
//
#include "view.h"
#include "post.h"

#include <ostream>
#include <istream>
#include <vector>

using namespace std;

View::View(std::istream& is, std::ostream& os) : os(os), is(is) {}

string View::getUserInput(string prompt) {
    os<<prompt;
    try {
        string nextLine;
        getline(is, nextLine);
        return nextLine;
    } catch (exception exception) {
        return "exit";
    }
}

string AuthView::getUserInput(string prompt) {
    string id, passwd;
    os<<prompt;
    os<<"id=";
    string stringBuffer;
    vector<string> inputs;
    inputs.clear();
    getline(is, stringBuffer, '\n');
    id = stringBuffer;
    os<<"passwd=";
    getline(is, stringBuffer, '\n');
    passwd = stringBuffer;
    return id + "\n" + passwd;
}

AuthView::AuthView(std::istream& is, std::ostream& os): View(is, os) {}


pair<string, string> PostView::getPost(string prompt) {
    string title;
    string content;
    os<<"-----------------------------------"<<endl;
    os<<prompt;
    os<<"* Title=";
    getline(is, title, '\n');
    os<<"* Content"<<endl;
    int nextlinecount = 0;
    while(nextlinecount!=1){
        os<<">";
        string contentTmp;
        getline(is, contentTmp, '\n');
        if(contentTmp.empty()){
            nextlinecount++;
            if(nextlinecount !=1) {
                content = content + '\n';
            }
        }
        else{
            content = content + contentTmp + '\n';
            nextlinecount=0;
        }
    }
    content.pop_back(); //remove trailing empty line
    return pair(title, content);
}

PostView::PostView(std::istream& is, std::ostream& os): View(is, os) {}

