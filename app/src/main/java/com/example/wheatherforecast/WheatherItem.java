package com.example.wheatherforecast;

/**
 * Class to hold the values from the API call
 */
public class WheatherItem {


    private String _name;
    private double _temp;
    private double _minTemp;
    private double _maxTemp;
    private String _type;
    private String _description;
    private int _humidity;
    private int _clouds;

    /**
     * Constructor
     * @param _name
     * @param _temp
     * @param _minTemp
     * @param _maxTemp
     * @param _type
     * @param _description
     * @param _humidity
     * @param _clouds
     */
    public WheatherItem(String _name, double _temp, double _minTemp, double _maxTemp, String _type, String _description, int _humidity, int _clouds) {
        this._name = _name;
        this._temp = _temp;
        this._minTemp = _minTemp;
        this._maxTemp = _maxTemp;
        this._type = _type;
        this._description = _description;
        this._humidity = _humidity;
        this._clouds = _clouds;
    }

    /**
     * Constructor
     */
    public WheatherItem(){
    }

    /**
     * Setters
     */

    public void set_name(String _name) {
        this._name = _name;
    }

    public void set_temp(double _temp) {
        this._temp = _temp;
    }

    public void set_minTemp(double _minTemp) {
        this._minTemp = _minTemp;
    }

    public void set_maxTemo(double _maxTemo) {
        this._maxTemp = _maxTemo;
    }

    public void set_type(String _type) {
        this._type = _type;
    }

    public void set_description(String _description) {
        this._description = _description;
    }

    public void set_humidity(int _humidity) {
        this._humidity = _humidity;
    }

    public void set_clouds(int _clouds) {
        this._clouds = _clouds;
    }

    /**
     * Getters
     */
    public String get_name() {
        return _name;
    }

    public double get_temp() {
        return _temp;
    }

    public double get_minTemp() {
        return _minTemp;
    }

    public double get_maxTemo() {
        return _maxTemp;
    }

    public String get_type() {
        return _type;
    }

    public String get_description() {
        return _description;
    }

    public int get_humidity() {
        return _humidity;
    }

    public int get_clouds() {
        return _clouds;
    }

}
