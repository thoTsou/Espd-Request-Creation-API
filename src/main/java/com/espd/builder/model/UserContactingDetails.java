package com.espd.builder.model;


public class UserContactingDetails {
    private int contactingDetails_id;
    private String contactPointName;
    private String faxNumber;
    private String telephoneNumber;
    private String emailAddress ;


    public UserContactingDetails(){};

    UserContactingDetails(int contactingDetails_id, String contactPointName, String faxNumber, String telephoneNumber , String emailAddress)
    {
        this.setContactingDetails_id(contactingDetails_id);
        this.setContactPointName(contactPointName);
        this.setFaxNumber(faxNumber);
        this.setTelephoneNumber(telephoneNumber);
        this.setEmailAddress(emailAddress);
    }

    public void setContactingDetails_id(int contactingDetails_id) {
        this.contactingDetails_id = contactingDetails_id;
    }

    public int getContactingDetails_id() {
        return contactingDetails_id;
    }

    public void setContactPointName(String contactPointName) {
        this.contactPointName = contactPointName;
    }

    public String getContactPointName() {
        return contactPointName;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }
}
