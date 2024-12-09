  package org.example.realtime_event_ticketing_system.models;

  import jakarta.persistence.*;
  import lombok.Data;
  import lombok.EqualsAndHashCode;
  import lombok.NoArgsConstructor;

  import java.util.ArrayList;
  import java.util.List;

  @Table(name = "customer")
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
