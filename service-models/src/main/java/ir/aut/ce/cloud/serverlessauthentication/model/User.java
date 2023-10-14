package ir.aut.ce.cloud.serverlessauthentication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @Column(name = "username")
    String username;
    @Column(name = "email")
    String email;
    @Column(name = "lastname")
    String lastname;
    @Column(name = "nationalId")
    String nationalId;
    @Column(name = "ip")
    String ip;
    @Column(name = "imageKey")
    String imageKey;
    @Column(name = "state")
    String state;
}
