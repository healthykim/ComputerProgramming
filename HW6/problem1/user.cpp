#include "user.h"
#include <string>



User::User(std::string name, std::string password): name(name), password(password), cart(), history(), type(){

}

bool User::passwordcheck(std::string password) {
    if(password == this->password){
        return true;
    }
    else
        return false;
}

NormalUser::NormalUser(std::string name, std::string password) : User(name, password) {this->type = normal;}

PremiumUser::PremiumUser(std::string name, std::string password) : User(name, password) {this->type = premium;}