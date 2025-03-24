package com.example.demo.model;

import com.example.demo.common.Enums;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name="account")
public class Account  implements UserDetails {
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


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return "";
    }
}
