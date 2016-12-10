package com.easycore.stromecek.model;

public final class Donation {

    private final String projectName;
    private final String projectDesc;
    private final String companyName;
    private final String companyDesc;
    private final String picture;
    private final String smsCode;

    public Donation(String projectName, String projectDesc, String companyName,
                    String companyDesc, String picture, String smsCode) {
        this.projectName = projectName;
        this.projectDesc = projectDesc;
        this.companyName = companyName;
        this.companyDesc = companyDesc;
        this.picture = picture;
        this.smsCode = smsCode;
    }

    @Override
    public String toString() {
        return "Donation{" +
                "projectName='" + projectName + '\'' +
                ", companyName='" + companyName + '\'' +
                '}';
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
        return picture == null ? "" : picture;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public static final class Builder {

        private String projectName;
        private String companyName;
        private final StringBuilder projectDesc;
        private final StringBuilder companyDesc;
        private String picture;
        private String smsCode;

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

        public void setSmsCode(String code) {
            this.smsCode = code;
        }

        public Donation build() {
            if (smsCode == null) {
                throw new IllegalArgumentException("You must set at least one SMS code");
            }
            return new Donation(projectName, projectDesc.toString(),
                    companyName, companyDesc.toString(), picture, smsCode);
        }
    }
}
