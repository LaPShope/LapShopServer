package com.example.demo.Common;

public class Enums {
    public enum role{
        ADMIN,CUSTOMER
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
        ErrorInternal, ErrorWrongCreds, ErrorNoPermission
    }
}
