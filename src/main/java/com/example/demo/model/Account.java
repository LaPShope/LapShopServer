package com.example.demo.model;

import com.example.demo.common.Enums;
import com.example.demo.common.RoleConverter;
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
@Table(name="account",
        indexes = {
                @Index(name = "idx_email", columnList = "email")
        }
)
public class Account  implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(unique = true,nullable = false)
    private String email;

    private String name;

    private String password;
    @Column(name = "is_active")
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,columnDefinition = "VARCHAR(255) DEFAULT 'CUSTOMER'")
    private Enums.Role role;

    @OneToOne(mappedBy = "account",cascade = CascadeType.ALL)
    @JsonIgnore
    private Customer customer;

    @OneToOne(mappedBy = "account",cascade = CascadeType.ALL)
    @JsonIgnore
    private Admin admin;

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
        return this.name;
    }
}
