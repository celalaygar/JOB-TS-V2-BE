package com.tracker.job_ts.auth.entity;

public enum UserDepartment {
    ENGINEERING("Engineering", "Mühendislik"),
    PRODUCT("Product", "Ürün"),
    DESIGN("Design", "Tasarım"),
    MARKETING("Marketing", "Pazarlama"),
    SALES("Sales", "Satış"),
    SUPPORT("Support", "Destek"),
    HR("HR", "İnsan Kaynakları"),
    FINANCE("Finance", "Finans"),
    OPERATIONS("Operations", "Operasyonlar"),
    OTHER("Other", "Diğer");

    private final String enValue;
    private final String trValue;

    UserDepartment(String enValue, String trValue) {
        this.enValue = enValue;
        this.trValue = trValue;
    }

    public String getEnValue() {
        return enValue;
    }

    public String getTrValue() {
        return trValue;
    }
}