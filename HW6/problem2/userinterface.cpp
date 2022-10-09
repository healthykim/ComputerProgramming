//
// Created by 문보설 on 2020/12/11.
//

#include "userinterface.h"
#include <ostream>
#include <istream>
#include <iostream>
#include <sstream>
#include <vector>
#include "view.h"
#include "frontend.h"

UserInterface::UserInterface(std::istream& is, std::ostream& os): is(is), os(os), frontEnd(is, os), authView(is, os), postView(is, os) {}

void UserInterface::run() {

    string command;
    string authInfo = authView.getUserInput("------ Authentication ------\n");
    //cout<<authInfo<<endl;
    if (frontEnd.auth(authInfo)) {
        //cout<<"login success"<<endl;
        do {
            command = postView.getUserInput(
                    "-----------------------------------\n" +
                    frontEnd.getUser().id + "@sns.com\n" +
                    "post : Post contents\n" +
                    "recommend : recommend interesting posts\n" +
                    "search <keyword> : List post entries whose contents contain <keyword>\n" +
                    "exit : Terminate this program\n" +
                    "-----------------------------------\n" +
                    "Command=");
        } while (query(command));
    }
    else{
        os<<"Failed Authentication.";
    }
}


bool UserInterface::query(string command){
    string instruction = parseInstruction(command);
    if(instruction=="exit") {
        return false;
    }
    else if(instruction=="post") {
        post();
    }
    else if(instruction=="search") {
        search(command);
    }
    else if(instruction=="recommend") {
        recommend();
    }
    else{
        //cout<<"Illegal Command Format : " << command<<endl;
    }
    return true;
}

string UserInterface::parseInstruction(string command){
//    string[] commandSlices = command.split(" ");
//    String instruction = commandSlices[0];
//    return instruction;

    istringstream ss(command);
    string stringBuffer;
    vector<string> commandSlices;
    commandSlices.clear();
    while (getline(ss, stringBuffer, ' ')){
        commandSlices.push_back(stringBuffer);
    }
    string instruction = commandSlices.front();
    return instruction;
}


void UserInterface::post() {
    frontEnd.post(postView.getPost("New Post\n"));
}

void UserInterface::recommend() {
    frontEnd.recommend();
}



void UserInterface::search(string command) {
    frontEnd.search(command);
}
