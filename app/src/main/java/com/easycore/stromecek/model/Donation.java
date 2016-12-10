package com.easycore.stromecek.model;

import java.util.ArrayList;
import java.util.List;

public final class Donation {

    private final String projectName;
    private final String projectDesc;
    private final String companyName;
    private final String companyDesc;
    private final String picture;
    private final List<String> SMSCodes;

    public Donation(String projectName, String projectDesc, String companyName,
                    String companyDesc, String picture, List<String> SMSCodes) {
        this.projectName = projectName;
        this.projectDesc = projectDesc;
        this.companyName = companyName;
        this.companyDesc = companyDesc;
        this.picture = picture;
        this.SMSCodes = SMSCodes;
    }

    @Override
    public String toString() {
        return "Donation{" +
                "projectName='" + projectName + '\'' +
                ", companyName='" + companyName + '\'' +
                '}';
    }

    public boolean hasMultipleSMSCodes() {
        return SMSCodes.size() > 1;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getProjectDescription() {
        return projectDesc;
    }

    public String getCompanyDescription() {
        return companyDesc;
    }

    public String getPicture() {
        return picture;
    }

    public List<String> getSMSCodes() {
        return SMSCodes;
    }

    public String getSMSCode() {
        if (hasMultipleSMSCodes()) {
            throw new IllegalArgumentException("Donation has multiple SMS codes. " +
                    "You must pick one");
        }
        return SMSCodes.get(0);
    }

    public static final class Builder {

        private String projectName;
        private String companyName;
        private final StringBuilder projectDesc;
        private final StringBuilder companyDesc;
        private String picture;
        private List<String> SMSCodes;

        public Builder() {
            this.projectDesc = new StringBuilder();
            this.companyDesc = new StringBuilder();
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public void appendProjectDesc(String description) {
            projectDesc.append(description);
        }

        public void appendCompanyDesc(String description) {
            companyDesc.append(description);
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public void setSMSCodes(List<String> SMSCodes) {
            this.SMSCodes = SMSCodes;
        }

        public void setSMSCode(String code) {
            this.SMSCodes = new ArrayList<>(1);
            this.SMSCodes.add(code);
        }

        public Donation build() {
            if (SMSCodes == null) {
                throw new IllegalArgumentException("You must set at least one SMS code");
            }
            return new Donation(projectName, projectDesc.toString(),
                    companyName, companyDesc.toString(), picture, SMSCodes);
        }
    }
}
