#include <iostream>
#include "app.h"
#include "userinterface.h"

App::App(std::istream& is, std::ostream& os): is(is), os(os) {
    // TODO
}

void App::run() {
    // TODO
    UserInterface ui = UserInterface(is, os);
    ui.run();
}
