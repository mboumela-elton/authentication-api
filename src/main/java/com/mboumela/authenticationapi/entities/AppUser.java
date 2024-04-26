package com.mboumela.authenticationapi.entities;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name", nullable = false)
    @Size(max = 100)
    private String username;

    @Column(nullable = false, unique = true)
    @Size(max = 100)
    private String email;

    @Column(nullable = false)
    @Size(max = 100)
    private String password;
    
    @Column(name = "date_created", nullable = false)
    private String dateCreated;
    
  //all roles are changed
    @ManyToMany(fetch = FetchType.EAGER)
    @Size(max = 200)
    private List<AppRole> roles;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @Size(max = 200)
    private List<Form> forms;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @Size(max = 200)
    private List<Cv> cvs;

}
