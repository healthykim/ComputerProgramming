//
// Created by 문보설 on 2020/12/11.
//
#include "post.h"

Post::Post() : time(), title(), contents(), id(){}

void Post::setID(int id) {this->id = id;}
void Post::setcontent(string contents) {this->contents=contents;}
void Post::settitle(string title) {this->title = title;}
void Post::settime(string time) {this-> time = time;}

