package cn.rongcloud.im.niko.event;

public class CitySelectEvent {
    private String city;
    private String cityCode;
    private String provinceCode;
    private String countryCode;

    public CitySelectEvent(String city, String cityCode, String provinceCode, String countryCode) {
        this.city = city;
        this.cityCode = cityCode;
        this.provinceCode = provinceCode;
        this.countryCode = countryCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
