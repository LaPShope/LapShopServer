package com.example.demo.common;

import lombok.Getter;

public class Enums {
    public enum Role {
        Admin, Customer;
    }

    public enum laptopStatus{
        Available,SoldOut,Maintenance
    }

    public enum PaymentType{
        VNPay,Momo,ZaloPay,CashOnDelivery
    }

    public enum PaymentStatus{
        Pending,Success,Failed
    }

    public enum OrderStatus{
        Pending,Shipped,Delivered,Cancelled
    }

    public enum Color{
        Red,Blue,Green,Black,White,Silver,Gold
    }

    public enum ErrorKey {
        ErrorInternal,
        ErrorWrongCreds,
        ErrorNoPermission
    }
}
