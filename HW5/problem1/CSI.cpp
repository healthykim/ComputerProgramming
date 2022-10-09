#include <fstream>
#include <iostream>
#include "CSI.h"

Complex::Complex(): real(0), imag(0) {}

CSI::CSI(): data(nullptr), num_packets(0), num_channel(0), num_subcarrier(0) {}

CSI::~CSI() {
    if(data) {
        for(int i = 0 ; i < num_packets; i++) {
            delete[] data[i];
        }
        delete[] data;
    }
}

int CSI::packet_length() const {
    return num_channel * num_subcarrier;
}

void CSI::print(std::ostream& os) const {
    for (int i = 0; i < num_packets; i++) {
        for (int j = 0; j < packet_length(); j++) {
            os << data[i][j] << ' ';
        }
        os << std::endl;
    }
}

std::ostream& operator<<(std::ostream &os, const Complex &c) {
    // TODO: problem 1.1
    os<<c.real<<"+"<<c.imag<<"i";
    return os;
}

void read_csi(const char* filename, CSI* csi) {
    // TODO: problem 1.2

    std::string str;
    std::string str2;
    std::string input;
    std::ifstream readFile(filename);
    getline(readFile, str);     csi->num_packets = std::stoi(str);
    getline(readFile, str);     csi->num_channel = std::stoi(str);
    getline(readFile, str);     csi->num_subcarrier = std::stoi(str);

    int rows = csi->num_packets; //Any Expression
    int cols = csi->num_channel*csi->num_subcarrier;
    Complex **array;
    array = new Complex*[rows];

    for(int i = 0; i < rows; ++i)
    {
        array[i] = new Complex[cols];
    }

    int i =0;
    int sc = 0;
    int packet_number = 0;

    while(!readFile.eof()){
        getline(readFile, str);

        if(readFile.eof()){
            break;
        }
        getline(readFile, str2);


        //std::cout<<" "<<i<<" "<<sc<<std::endl;

        array[packet_number][i+sc*(csi->num_subcarrier)] = Complex();
        array[packet_number][i+sc*(csi->num_subcarrier)].real = std::stoi(str);
        array[packet_number][i+sc*(csi->num_subcarrier)].imag = std::stoi(str2);

        if((i==(csi->num_subcarrier-1))&&(sc==(csi->num_channel-1))){
            packet_number++;
            i=0;
        }

        else if(sc==(csi->num_channel-1)){
            i++;
        }

        sc++;
        sc = sc%(csi->num_channel);

        //
        /*
        if(sc == (csi->num_channel)){
            //std::cout<<i<<std::endl;
            sc=0;
        }
         */


        if(readFile.eof()){
            break;
        }
    }

    readFile.close();
    csi->data = array;

}

float** decode_csi(CSI* csi) {
    // TODO: problem 1.3
    float** array;
    int row = csi->num_packets;
    int col = csi->num_channel*csi->num_subcarrier;

    //std::cout<<row<<" "<<col<<std::endl;
    array = new float*[row];
    for(int i=0; i<row; i++){
        array[i] = new float[col];
    }

    for(int i=0; i<row; i++){
        for(int j=0; j<col; j++){
            array[i][j]= sqrt(csi->data[i][j].real*csi->data[i][j].real + csi->data[i][j].imag*csi->data[i][j].imag);
        }
    }

    return array;
}

float* get_std(float** decoded_csi, int num_packets, int packet_length) {
    // TODO: problem 1.4
    float* array;
    array = new float[num_packets];
    for(int i=0; i<num_packets; i++){
        array[i] = standard_deviation(decoded_csi[i], packet_length);
    }
    return array;
}

void save_std(float* std_arr, int num_packets, const char* filename) {
    // TODO: problem 1.5
    std::ofstream writefile;
    writefile.open(filename);

    if(writefile.is_open()){
        //std::cout<< "there was file";
        for(int i=0; i<num_packets; i++) {
            writefile << std_arr[i] <<" ";
        }
        writefile<<"\n";
        writefile.flush();
        writefile.close();
    }


}

// convenience functions
float standard_deviation(float* data, int array_length) {
    float mean = 0, var = 0;
    for (int i = 0; i < array_length; i++) {
        mean += data[i];
    }
    mean /= array_length;
    for (int i = 0; i < array_length; i++) {
        var += pow(data[i]-mean,2);
    }
    var /= array_length;
    return sqrt(var);
}