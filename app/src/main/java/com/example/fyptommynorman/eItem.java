package com.example.fyptommynorman;

public class eItem {
    private String itemName;
    private double amount;

    private double amountPerPerson;



    private String userId;
    private String groupPin;

    public eItem(){

    }


    public eItem(String itemName, double amount, double amountPerPerson, String userId, String groupPin){
        this.itemName = itemName;
        this.amount = amount;
        this.amountPerPerson = amountPerPerson;
        this.userId = userId;
        this.groupPin = groupPin;
    }

    //getter methods

    public String getItemName(){
        return itemName;
    }


    public double getAmount(){
        return amount;
    }

    public double getAmountPerPerson(){
        return amount;
    }

    public  String getGroupPin() {
        return groupPin;
    }

    public  String getUserId() {
        return userId;
    }

    //setter methods

    public  void  setItemName(String itemName){
        this.itemName = itemName;
    }

    public  void  setAmount(double amount){
        this.amount = amount;
    }

    public  void  setAmountPerPerson(double amountPerPerson){
        this.amountPerPerson = amountPerPerson;
    }

    public  void  setGroupPin(String groupPin){
        this.groupPin = groupPin;
    }

    public  void  setUserId(String userId){
        this.userId = userId;
    }


    public String toString(){

        return "£" + amount + " for " + itemName;
//        return "eItem{" + "itemName = " + itemName + '\'' +
//                ", amount = " + amount +
//                ", amountPerPerson = " + amountPerPerson +
//
//                ", userId = " + userId + '\'' +
//                ", groupPin = " + groupPin + '\'' +
//                '}' ;



    }

}
