package ir.aut.ce.cloud.serverlessauthentication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Column(name = "email")
    String email;
    @Column(name = "lastname")
    String lastname;
    @Column(name = "nationalId")
    @Id
    String nationalId;
    @Column(name = "ip")
    String ip;
    @Column(name = "image1")
    String image1;
    @Column(name = "image2")
    String image2;
    @Column(name = "state")
    String state;
}
