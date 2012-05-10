package org.ei.drishti.contract;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.LocalDate;

import java.util.Date;

public class AnteNatalCareEnrollmentInformation {
    private String caseId;
    private String thaayiCardNumber;
    private String name;
    private int age;
    private String village;
    private String anmPhoneNumber;
    private String anmIdentifier;
    private Date lmp;
    private String ecNumber;

    public AnteNatalCareEnrollmentInformation(String caseId, String thaayiCardNumber, String name, String village, String anmPhoneNumber, String anmIdentifier, Date lmp, String ecNumber) {
        this.caseId = caseId;
        this.thaayiCardNumber = thaayiCardNumber;
        this.name = name;
        this.village = village;
        this.anmPhoneNumber = anmPhoneNumber;
        this.anmIdentifier = anmIdentifier;
        this.lmp = lmp;
        this.ecNumber = ecNumber;
        this.age = 20;
    }

    public String caseId() {
        return caseId;
    }

    public String thaayiCardNumber() {
        return thaayiCardNumber;
    }

    public String name() {
        return name;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    public String anmPhoneNumber() {
        return anmPhoneNumber;
    }

    public LocalDate lmpDate() {
        return lmp == null ? null : LocalDate.fromDateFields(lmp);
    }

    public String ecNumber() {
        return ecNumber;
    }

    public String village() {
        return village;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
