#include "ui.h"
#include <string>


UI::UI(ShoppingDB &db, std::ostream& os): db(db), os(os) {

}

std::ostream & UI::get_os() const {
    return os;
}
