/*=========================================================================
 
     Copyright (C) 2018  Jurij Zelic, GoUnitis s.p.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
=========================================================================
    v1.0  - First release 12.11.2018
    
=========================================================================*/
#include <Wire.h>
#include <Adafruit_ADS1015.h>

/*=========================================================================
      Configuration parameters
=========================================================================*/
//#define ADS_GAIN            GAIN_ONE // ±4.096 V
//#define ADS_RESOLUTION      0.125
//#define ADS_GAIN            GAIN_TWO // ±2.048 V
//#define ADS_RESOLUTION      0.0625
//#define ADS_GAIN            GAIN_FOUR // ±1.024 V
//#define ADS_RESOLUTION      0.03125
//#define ADS_GAIN            GAIN_EIGHT // ±0.512 V
//#define ADS_RESOLUTION      0.015625
#define ADS_GAIN            GAIN_SIXTEEN // ±0.256 V 
#define ADS_RESOLUTION      0.0078125

#define ADS_OFFSET          0.12

#define SERIAL_BAUD_RATE    9600
#define MEASURE_DELAY_MS    250


/*=========================================================================
      Macros
=========================================================================*/
#define ADRUINO_RESOLUTION 4.8876 // equals 5000/1023

#define TO_JSON(R,A0,A1,A2,A3,A4,A5,A6,A7) sprintf(R,"{\"ads0mv\":%s,\"ads1mv\":%s,\"ads2mv\":%s,\"ads3mv\":%s,\"adr0mv\":%s,\"adr1mv\":%s,\"adr2mv\":%s,\"adr3mv\":%s}",A0,A1,A2,A3,A4,A5,A6,A7)

/*=========================================================================
      Global variables
=========================================================================*/
Adafruit_ADS1115 ads1115;

/*=========================================================================
      setup function
=========================================================================*/
void setup() {
  // setup USB
  Serial.begin(SERIAL_BAUD_RATE);

  // setup ADS1115
  ads1115.begin();
  ads1115.setGain(ADS_GAIN);

  // setup analog inputs
  pinMode(A0, INPUT);
  pinMode(A1, INPUT);
  pinMode(A2, INPUT);
  pinMode(A3, INPUT);
}

/*=========================================================================
      main loop function
=========================================================================*/
void loop() {
  // read ADC1115
  int16_t ads0 = ads1115.readADC_SingleEnded(0);
  int16_t ads1 = ads1115.readADC_SingleEnded(1);
  int16_t ads2 = ads1115.readADC_SingleEnded(2);
  int16_t ads3 = ads1115.readADC_SingleEnded(3);

  // read adruino ADC
  int adr0 = analogRead(A0);
  int adr1 = analogRead(A1);
  int adr2 = analogRead(A2);
  int adr3 = analogRead(A3);

  // convert everithing to milli volts
  float ads0mV = ads0 * ADS_RESOLUTION + ADS_OFFSET;
  float ads1mV = ads1 * ADS_RESOLUTION + ADS_OFFSET;
  float ads2mV = ads2 * ADS_RESOLUTION + ADS_OFFSET;
  float ads3mV = ads3 * ADS_RESOLUTION + ADS_OFFSET;

  float adr0mV = adr0 * ADRUINO_RESOLUTION;
  float adr1mV = adr1 * ADRUINO_RESOLUTION;
  float adr2mV = adr2 * ADRUINO_RESOLUTION;
  float adr3mV = adr3 * ADRUINO_RESOLUTION;

  // convert everithing to string
  char ads0mVstr[10];
  dtostrf(ads0mV, 4, 2, ads0mVstr);
  char ads1mVstr[10];
  dtostrf(ads1mV, 4, 2, ads1mVstr);
  char ads2mVstr[10];
  dtostrf(ads2mV, 4, 2, ads2mVstr);
  char ads3mVstr[10];
  dtostrf(ads3mV, 4, 2, ads3mVstr);
  char adr0mVstr[10];
  dtostrf(adr0mV, 4, 0, adr0mVstr);
  char adr1mVstr[10];
  dtostrf(adr1mV, 4, 0, adr1mVstr);
  char adr2mVstr[10];
  dtostrf(adr2mV, 4, 0, adr2mVstr);
  char adr3mVstr[10];
  dtostrf(adr3mV, 4, 0, adr3mVstr);

  // format output json
  char json[200];
  TO_JSON(
    json,
    ads0mVstr,
    ads1mVstr,
    ads2mVstr,
    ads3mVstr,
    adr0mVstr,
    adr1mVstr,
    adr2mVstr,
    adr3mVstr
  );

  // print output
  Serial.println(json);

  delay(MEASURE_DELAY_MS);
}
