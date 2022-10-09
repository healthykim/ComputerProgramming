//
// Created by 문보설 on 2020/12/11.
//
#include "frontend.h"
#include <algorithm>
#include <iostream>
#include <time.h>
#include <sstream>
#include <vector>
#include <unordered_set>
#include <fstream>
#include "backend.h"
#include "config.h"
//#include "UserInterface.h"

using namespace std;

FrontEnd::FrontEnd(std::istream& is, std::ostream& os): user(), is(is), os(os), backEnd(is, os) {}


bool FrontEnd::auth(string authInfo) {
    // TODO sub-problem 1
    istringstream s(authInfo);
    string stringBuffer;
    vector<string> idPasswd;
    idPasswd.clear();
    while (getline(s, stringBuffer, '\n')){
        idPasswd.push_back(stringBuffer);
    }
    string password = backEnd.getPassword(idPasswd[0]);
    //cout<<"real password: "<<password<<endl;
    //cout<<"input password: "<<idPasswd[1]<<endl;
    if(password==(idPasswd[1])) {
        user = User(idPasswd[0], idPasswd[1]);
        return true;
    }
    else
        return false;
}

User FrontEnd::getUser() {return user;}

void FrontEnd::post(pair<string, string> pair) {
    //getLastId
    //localtime to string
    // from backend
    Post post;
    int lastid = backEnd.getLastPostId();
    //time_t rawtime;
    //time(&rawtime);
    //post.settime(rawtime);
    post.setID(lastid+1);
    post.settitle(pair.first);
    post.setcontent(pair.second);

    backEnd.makePost(user.id, &post);
}
void FrontEnd::recommend() {
    //1. find friends
    vector<string> friends = backEnd.getFriends(user.id);

    //2. sort
    // 2-1 get all ids of friends posts
    vector<string> friendsPostId;
    auto it = friends.begin();
    while(it!=friends.end()) {
        vector<string> oneFriendsPostId = backEnd.getUserPost(*it);
        friendsPostId.insert(friendsPostId.end(), oneFriendsPostId.begin(), oneFriendsPostId.end());
        it++;
    }
    //cout<<friendsPostId.size()<<endl;

    // 2-2 sort
    sort(friendsPostId.begin(), friendsPostId.end(), compare);
    //cout<<friendsPostId.size()<<endl;

    //3. 10 posts summary
    int recommendPosts=0;
    auto it3 = friendsPostId.begin();

    while(recommendPosts<10&&it3!=friendsPostId.end()){
        backEnd.printPost(*it3);
        /*
        os<<"-----------------------------------"<<endl;
        os<<"id: "<<post->id<<endl;
        os<<"created at: "<<post->time<<endl;
        os<<"title: "<<post->title<<endl;
        os<<"content: "<<post->contents<<endl;
         */
        recommendPosts++;
        it3++;
    }
}

int FrontEnd::parseDatetoInt(string date) {
    string trimmedDate ="";
    auto it = date.begin();
    while(it != date.end()){
        if(*it == '/'){

        }
        else{
            trimmedDate = trimmedDate + *it;
        }
        it++;
    }
    return stoi(trimmedDate);
}

int FrontEnd::parseTimetoInt(string time) {
    string trimmedTime ="";
    auto it = time.begin();
    while(it != time.end()){
        if(*it == ':'){

        }
        else{
            trimmedTime = trimmedTime + *it;
        }
        it++;
    }
    return stoi(trimmedTime);

}

bool FrontEnd::compare(string f1, string f2) {
    ifstream file1;
    ifstream file2;
    file1.open(f1);
    file2.open(f2);
    string sdate1;
    string stime1;
    string sdate2;
    string stime2;
    if(file1.is_open()){
        getline(file1, sdate1, ' ');
        getline(file1, stime1, '\n');
    }
    if(file2.is_open()){
        getline(file2, sdate2, ' ');
        getline(file2, stime2, '\n');
    }
    int date1 = parseDatetoInt(sdate1);
    int time1 = parseTimetoInt(stime1);
    int date2 = parseDatetoInt(sdate2);
    int time2 = parseTimetoInt(stime2);
    //cout<<"let's compare"<<endl;

    if(date1>date2){
        return true;
    }
    else if(date1 == date2){
        if(time1>time2){
            return true;
        }
        else
            return false;
    }
    else
        return false;
}

void FrontEnd::search(string command) {
    istringstream ss(command);
    string stringBuffer;
    unordered_set<string> keywords;
    keywords.clear();
    getline(ss, stringBuffer, ' '); // remove 'search'
    //cout<<"keyword: ";
    while (getline(ss, stringBuffer, ' ')){
        if(stringBuffer =="\r"||stringBuffer ==" "||stringBuffer==""||stringBuffer=="\0"){
        }
        else {
            keywords.insert(stringBuffer);
            //cout << stringBuffer << ",";
        }
    }
    //cout<<endl;


    //1. get the post paths that include keywords
    vector<string> topten = backEnd.getKeywordPost(keywords);
    auto toptenit = topten.begin();
    os<<"-----------------------------------"<<endl;
    while(toptenit!=topten.end()){
        backEnd.printSummary(*toptenit);
        toptenit++;
    }


}

