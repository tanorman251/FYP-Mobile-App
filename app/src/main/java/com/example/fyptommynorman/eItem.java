package com.example.fyptommynorman;

public class eItem {
    private String itemName;
    private double amount;

    private String desc;

    private String userId;
    private String groupPin;
    public eItem(String itemName, double amount, String desc, String userId, String usersGpin){
        this.itemName = itemName;
        this.amount = amount;
        this.desc = desc;
        this.userId = userId;
        this.groupPin = groupPin;
    }

    public String getItemName(){
        return itemName;
    }
    public double getAmount(){
        return amount;
    }
    public String getDesc(){
        return desc;
    }

    public  String getGroupPin() {
        return groupPin;
    }


}
