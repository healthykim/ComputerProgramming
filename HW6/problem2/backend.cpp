//
// Created by 문보설 on 2020/12/11.
//

#include "backend.h"
#include "config.h"
#include <fstream>
#include <vector>
#include <filesystem>
#include <time.h>
#include <ctype.h>
#include <sstream>
#include <algorithm>

using namespace std;

BackEnd::BackEnd(std::istream &is, std::ostream &os) : is(is),os(os){}

string BackEnd::getPassword(string id) {
    ifstream readFile;
    int filesize = 0;
    string passwdPath = SERVER_STORAGE_DIR+id+'/'+"password.txt";
    readFile.open(passwdPath);
    string password = "";
    if(readFile.is_open())
    {
        readFile.seekg(0, ios::end);
        filesize = readFile.tellg();
        password.resize(filesize);
        readFile.seekg(0, ios::beg);
        readFile.read(&password[0], filesize);
    }
    readFile.close();

    return password;
}

int BackEnd::getLastPostId() {
    ifstream postFile;
    int max =-1;
    postFile.open(SERVER_STORAGE_DIR);
    for (const filesystem::directory_entry& entry :
            filesystem::recursive_directory_iterator(filesystem::current_path() / SERVER_STORAGE_DIR)) {
        string filename = entry.path().filename().string();
        if(filename.find(".txt") == string::npos){
            //directory name
            // do nothing
        }
        else if(filename == "password.txt" || filename == "friend.txt"){
            //not a post
            //do nothing
        }
        else{
            int fileid = stoi(filename);
            if(fileid>max){
                max = fileid;
            }
        }
    }
    return max;
}



void BackEnd::makePost(string userid, Post* post) {
    ofstream writefile;
    writefile.open(SERVER_STORAGE_DIR+userid+"/post/"+to_string(post->id)+".txt");
    //struct tm *pLocal = localtime(&(post->time));
    time_t rawtime;
    time(&rawtime);
    struct tm *pLocal = localtime(&rawtime);
    char timeformat[80];
    //localtimeformat = to_string(pLocal->tm_year + 1900) + '/' + to_string(pLocal->tm_mon + 1) + '/'+to_string(pLocal->tm_mday);
    //localtimeformat = localtimeformat + ' '+to_string(pLocal->tm_hour)+':'+to_string(pLocal->tm_min)+':'+to_string(pLocal->tm_sec);
    strftime(timeformat, 80, "%H:%M:%S", pLocal);
    string time(timeformat);
    string date = to_string(pLocal->tm_year + 1900) + '/' + to_string(pLocal->tm_mon + 1) + '/'+to_string(pLocal->tm_mday);
    if(writefile.is_open()){
        writefile<<date<<' '<<time<<endl;
        writefile<<post->title<<endl;
        writefile<<'\n';
        writefile<<post->contents;
    }

}

vector<string> BackEnd::getFriends(string userid) {
    vector<string> friends;
    ifstream friendfile;
    string path = SERVER_STORAGE_DIR+userid+"/friend.txt";
    friendfile.open(path);
    if(friendfile.is_open()){
        while(!friendfile.eof()){
            string friendName;
            getline(friendfile, friendName, '\n');
            if(friendName.empty()){ }
            else friends.push_back(friendName);
        }
    }
    return friends;
}

vector<string> BackEnd::getUserPost(string userid) {
    vector<string> postIDs;
    ifstream posts;
    string path = SERVER_STORAGE_DIR+userid+"/post";
    posts.open(path);
    filesystem::directory_iterator itr(filesystem::current_path() / path);
    while (itr != filesystem::end(itr)) {
        const filesystem::directory_entry& entry = *itr;
        string filepath = entry.path().string();
        if(filepath.find(".txt") != string::npos){
            postIDs.push_back(filepath);
        }
        //std::cout << filepath << std::endl;
        itr++;
    }
    return postIDs;
}


void BackEnd::printPost(string path) {
    ifstream post;
    post.open(path);
    int id;
    string time, title;
    string content;
    if(post.is_open()){
        /*
        int filesize=0;
        post.seekg(0, ios::end);
        filesize = post.tellg();
        content.resize(filesize);
        post.seekg(0, ios::beg);
        //filesize get
         */

        string idtmp;
        idtmp = filesystem::path(path).filename();
        id = stoi(idtmp);
        getline(post, time, '\n');
        getline(post, title, '\n');
        string tmp;
        getline(post, tmp, '\n');
        while(getline(post, tmp, '\n')){
            content = content + tmp + '\n';
        }
        content.pop_back();

/*
        post.read(&content[0], filesize);
        // after question. ildan O naoge ham
        content.shrink_to_fit();


        while(isspace(content.back())){
            //cout<<"space!"<<endl;
            content.pop_back();
        }
        */
    }
    post.close();
    os<<"-----------------------------------"<<endl;
    os<<"id: "<<id<<endl;
    os<<"created at: "<<time<<endl;
    os<<"title: "<<title<<endl;
    os<<"content:"<<endl;
    os<<content<<endl;
}



bool BackEnd::compare(pair<string, int> p1, pair<string, int> p2){
    if(p1.second>p2.second){
        return true;
    }
    else if(p1.second==p2.second){
        ifstream file1;
        ifstream file2;
        file1.open(p1.first);
        file2.open(p2.first);
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

        if(date1>date2){
            //cout<<date1 <<" " << date2<<endl;
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
    else
        return false;


}
int BackEnd::parseDatetoInt(string date) {
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

int BackEnd::parseTimetoInt(string time) {
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

void BackEnd::printSummary(string path) {
    int id;
    string time, title;
    ifstream post;
    post.open(path);
    if(post.is_open()){
        getline(post, time, '\n');
        getline(post, title, '\n');
    }
    id = stoi(filesystem::path(path).filename());


    os<<"id: "<<id<<", ";
    os<<"created at: "<<time<<", ";
    os<<"title: "<<title<<endl;
}

//4
vector<string> BackEnd::getKeywordPost(unordered_set<string> keywords) {
    //1. number of occurrences
    // 1-1 pair<string path, int occurrence> for larger than zero occurrence
    vector<pair<string, int>> occurrencelist;
    occurrencelist.clear();
    for (const filesystem::directory_entry& entry :
            filesystem::recursive_directory_iterator(filesystem::current_path() / SERVER_STORAGE_DIR)) {
        string filename = entry.path().filename().string();
        if(filename.find(".txt") == string::npos){
            //directory name
            // do nothing
        }
        else if(filename == "password.txt" || filename == "friend.txt"){
            //not a post
            //do nothing
        }
        else{
            /*if(filename=="4930.txt")
                cout<<"잡았다 요놈 "<<endl;*/
            //!check occurrence!!
            ifstream post;
            post.open(entry.path());
            int occurrence = 0;
            string stringbuffer;
            string titleAndcontent;
            int filesize=0;
            post.seekg(0, ios::end);
            filesize = post.tellg();
            titleAndcontent.resize(filesize);
            post.seekg(0, ios::beg);
            getline(post, stringbuffer, '\n'); // for date
            post.read(&titleAndcontent[0], filesize);
            bool is_keyword = false;
            auto setit = keywords.begin();
            while (setit!=keywords.end()) {
                /*if(filename=="4930.txt")
                    cout<<"또 잡았다 요놈 "<<*setit<<" 찾아봅시다 "<<endl;*/
                //??????????is_keyword = false;
                if (titleAndcontent.find(*setit)!=titleAndcontent.npos) {
                    //if(filename=="5357.txt")
                     //   cout<<"잡았다 요놈 "<<*setit<<" 어딘가에 있다 "<<endl;
                    // file includes keyword. count the occurence
                    string tmp;
                    stringstream sstitleAndcontent(titleAndcontent);

                    /* if(filename=="4930.txt")
                         cout<<titleAndcontent<<endl;*/
/*
                    while (getline(sstitleAndcontent, tmp, ' ')) {
                        //cout << tmp << endl;
                        if (tmp == *setit) {
                            is_keyword = true;
                            occurrence++;
                        }
                    }
                    tmp.erase(std::remove(tmp.begin(), tmp.end(), '\0'), tmp.end());
                    if (tmp == *setit) {
                        is_keyword = true;
                        occurrence++;
                    }
                }

*/
                    //cout<<filename<<endl;
                    while(getline(sstitleAndcontent, tmp, '\n')) {
                        if (tmp.find(*setit) != tmp.npos) {
                            stringstream st(tmp);
                            string tmptmp;
                            while (getline(st, tmptmp, ' ')) {
                                if (tmptmp == *setit) {
                                    is_keyword = true;
                                    occurrence++;
                                    //cout<<tmp<<endl;
                                    //cout<<"occurrence :"<<occurrence<<endl;
                                }
                            }
                            if(tmptmp != *setit) {
                                tmptmp.erase(std::remove(tmptmp.begin(), tmptmp.end(), '\0'), tmptmp.end());
                                if (tmptmp == *setit) {
                                    //cout<<tmp<<endl;
                                    is_keyword = true;
                                    occurrence++;
                                }
                            }
                        }
                    }
                }

                /*
                else {
                    is_keyword = false;
                    occurrence = 0;
                    break;
                }
                */
                setit++;
            }

/*
            if(filename=="7298.txt") {
                cout << "7298: " << occurrence << endl;
                cout<< entry.path() << endl;
            }
            if(filename=="3528.txt") {
                cout << "3528: " << occurrence << endl;
                cout << entry.path() << endl;
            }
            if(filename=="4314.txt") {
                cout << "4314: " << occurrence << endl;
                cout << entry.path() << endl;
            }
            if(filename=="6405.txt") {
                cout << "6405: " << occurrence << endl;
                cout << entry.path() << endl;
            }
*/

            if((occurrence != 0)&&is_keyword){
                //cout<<entry.path().filename()<<endl;
                occurrencelist.push_back(pair(entry.path(), occurrence));
            }
            else if(occurrence == 0){

            }
        }
    }

    // 1-2 sort with occurrence. if there is same occurrence, with date and time.
    stable_sort(occurrencelist.begin(), occurrencelist.end(), compare);


    vector<string> topten;
    topten.clear();
    int cnt =0;
    auto it = occurrencelist.begin();
    while(it!=occurrencelist.end()&&cnt<10){
        topten.push_back(it->first);

        it++;
        cnt++;
    }

    return topten;

}