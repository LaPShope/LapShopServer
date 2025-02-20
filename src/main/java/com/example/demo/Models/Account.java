package com.example.demo.Models;

import com.example.demo.Common.Enums;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name="account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(unique = true,nullable = false)
    private String email;

    private String name;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,columnDefinition = "VARCHAR(255) DEFAULT 'CUSTOMER'")
    private Enums.role role;

    @OneToOne(mappedBy = "customerId",cascade = CascadeType.ALL)
//    @JsonIgnore
    private Customer customerId;

    @OneToOne(mappedBy = "adminId",cascade = CascadeType.ALL)
//    @JsonIgnore
    private Admin adminId;

    @JsonIgnore
    @OneToMany(mappedBy = "account",cascade = {CascadeType.PERSIST,
                                                CascadeType.DETACH,
                                                CascadeType.MERGE,
                                                CascadeType.REFRESH})
    private List<Comment> commentList;

    @JsonIgnore
    @OneToMany(mappedBy = "senderId",cascade = {CascadeType.PERSIST,
                                                CascadeType.DETACH,
                                                CascadeType.MERGE,
                                                CascadeType.REFRESH})
    private List<Chat> chatSend;


    @JsonIgnore
    @OneToMany(mappedBy = "receiverId",cascade = {CascadeType.PERSIST,
                                                CascadeType.DETACH,
                                                CascadeType.MERGE,
                                                CascadeType.REFRESH})
    private List<Chat> chatReceive;


}
