//Ueli Haltner [B00526617]
package com.example.weatherapp;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CountryCodes {

    private Map<String, String> countryMap;


    public CountryCodes() {

        this. countryMap = new HashMap<>();

        String[] isoCountryCodes = Locale.getISOCountries();

        Locale locale;
        String name;

        for(String code : isoCountryCodes){

            locale = new Locale("",code);
            name = locale.getDisplayCountry().toUpperCase();

            this.countryMap.put(name, code);
        }
    }


    /**
     * Return the country code if the country name exists; else return empty
     * @param name
     * @return
     */
    public String getCountryCode(String name) {

        //create an empty code as default
        String code = "";

        //if the country name exists
        if(this.countryMap.containsKey(name)) {
            //set the country code for the country name
            code = this.countryMap.get(name);
        }

        return code;
    }


    /**
     * returns if the country code is valid
     * @param code
     * @return true if code is a value; otherwise false
     */
    public boolean isValidCountryCode(String code){

        return this.countryMap.containsValue(code);
    }

    /**
     * returns if the city is a valid city
     * @param city
     * @return true if city is a valid key; otherwise false
     */
    public boolean isValidCity(String city){
        return this.countryMap.containsKey(city);
    }

}
