package org.example.realtime_event_ticketing_system.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "user_id")
public class Customer extends User {
  private boolean isVIP;

  @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
  private List<Purchase> purchases = new ArrayList<>();

  @Override
  protected void onCreate() {
    super.onCreate();
    setRole("ROLE_CUSTOMER");
  }
}


//import java.io.Serializable;
//@Entity
//public class Customer implements Serializable {
//  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
//  @Column(nullable = false, updatable = false)
//    private Long id;
//    private String name;
//    private String email;
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public Long getId() {
//        return id;
//    }
//}


