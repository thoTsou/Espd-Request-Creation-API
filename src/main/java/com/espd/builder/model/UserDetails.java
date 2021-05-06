package com.espd.builder.model;


public class UserDetails {
    private int user_id;
    private String electronicAddressID;
    private String webSiteURI;
    private int postalAddress_Id;
    private int contactingDetails_id ;
    private String caofficialName ;
    private String cacountry ;

    public UserDetails(){};

    UserDetails(int user_id,
            String electronicAddressID,
            String webSiteURI,
            int postalAddress_Id,
            int contactingDetails_id ,
            String caofficialName ,
            String cacountry ){

        this.setUser_id(user_id);
        this.setElectronicAddressID(electronicAddressID);
        this.setWebSiteURI(getWebSiteURI());
        this.setPostalAddress_Id(postalAddress_Id);
        this.setContactingDetails_id(contactingDetails_id);
        this.setCacountry(cacountry);
        this.setCaofficialName(caofficialName);
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setElectronicAddressID(String electronicAddressID) {
        this.electronicAddressID = electronicAddressID;
    }

    public String getElectronicAddressID() {
        return electronicAddressID;
    }

    public void setWebSiteURI(String webSiteURI) {
        this.webSiteURI = webSiteURI;
    }

    public String getWebSiteURI() {
        return webSiteURI;
    }

    public void setPostalAddress_Id(int postalAddress_Id) {
        this.postalAddress_Id = postalAddress_Id;
    }

    public int getPostalAddress_Id() {
        return postalAddress_Id;
    }

    public void setContactingDetails_id(int contactingDetails_id) {
        this.contactingDetails_id = contactingDetails_id;
    }

    public int getContactingDetails_id() {
        return contactingDetails_id;
    }

    public void setCacountry(String cacountry) {
        this.cacountry = cacountry;
    }


    public String getCacountry() {
        return cacountry;
    }

    public void setCaofficialName(String caofficialName) {
        this.caofficialName = caofficialName;
    }

    public String getCaofficialName() {
        return caofficialName;
    }
}
