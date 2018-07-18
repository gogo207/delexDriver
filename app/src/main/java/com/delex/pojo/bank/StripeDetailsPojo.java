package com.delex.pojo.bank;

/**
 * Created by murashid on 29-Aug-17.
 */

public class StripeDetailsPojo {

    private LegalEntity legal_entity;
    private ExternalAccounts external_accounts;


    public LegalEntity getLegal_entity() {
        return legal_entity;
    }

    public void setLegal_entity(LegalEntity legal_entity) {
        this.legal_entity = legal_entity;
    }

    public ExternalAccounts getExternal_accounts() {
        return external_accounts;
    }

    public void setExternal_accounts(ExternalAccounts external_accounts) {
        this.external_accounts = external_accounts;
    }
}
