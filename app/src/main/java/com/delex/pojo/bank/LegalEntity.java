package com.delex.pojo.bank;

/**
 * Created by murashid on 29-Aug-17.
 */

public class LegalEntity {

    private Address address;

    public Address getAddress() { return this.address; }

    public void setAddress(Address address) { this.address = address; }

    private boolean business_tax_id_provided;

    public boolean getBusinessTaxIdProvided() { return this.business_tax_id_provided; }

    public void setBusinessTaxIdProvided(boolean business_tax_id_provided) { this.business_tax_id_provided = business_tax_id_provided; }

    private Dob dob;

    public Dob getDob() { return this.dob; }

    public void setDob(Dob dob) { this.dob = dob; }

    private String first_name;

    public String getFirstName() { return this.first_name; }

    public void setFirstName(String first_name) { this.first_name = first_name; }

    private String last_name;

    public String getLastName() { return this.last_name; }

    public void setLastName(String last_name) { this.last_name = last_name; }

    private PersonalAddress personal_address;

    public PersonalAddress getPersonalAddress() { return this.personal_address; }

    public void setPersonalAddress(PersonalAddress personal_address) { this.personal_address = personal_address; }

    private boolean personal_id_number_provided;

    public boolean getPersonalIdNumberProvided() { return this.personal_id_number_provided; }

    public void setPersonalIdNumberProvided(boolean personal_id_number_provided) { this.personal_id_number_provided = personal_id_number_provided; }

    private boolean ssn_last_4_provided;

    public boolean getSsnLast4Provided() { return this.ssn_last_4_provided; }

    public void setSsnLast4Provided(boolean ssn_last_4_provided) { this.ssn_last_4_provided = ssn_last_4_provided; }

    private Verification verification;

    public Verification getVerification() { return this.verification; }

    public void setVerification(Verification verification) { this.verification = verification; }

}
