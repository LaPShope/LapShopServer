package com.example.demo.common;

public class Enums {
    public enum Role {
        Admin("Admin"), Customer("Customer");

        private final String role;

        Role(String role) {
            this.role = role;
        }

        public String value() {
            return role;
        }
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
