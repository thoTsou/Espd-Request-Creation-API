package com.espd.builder.model;


public class UserPostalAddress {
    private int postalAddress_Id;
    private String addressLine1;
    private String city;
    private String postCode;
    private String countryCode ;


    public UserPostalAddress(){};

    UserPostalAddress(int postalAddress_Id, String addressLine1, String city, String postCode , String countryCode)
    {
        this.setPostalAddress_Id(postalAddress_Id);
        this.setAddressLine1(addressLine1);
        this.setCity(city);
        this.setPostCode(postCode);
        this.setCountryCode(countryCode);
    }

    public void setPostalAddress_Id(int postalAddress_Id) {
        this.postalAddress_Id = postalAddress_Id;
    }

    public int getPostalAddress_Id() {
        return postalAddress_Id;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getPostCode() {
        return postCode;
    }
}
