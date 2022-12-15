package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import static javax.persistence.TemporalType.TIMESTAMP;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
public abstract class Base<U> implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @CreatedBy
  @Column(name = "created_by")
  protected U createdBy;

  @CreationTimestamp
  @Temporal(TIMESTAMP)
  @Column(name = "created_at")
  protected Date createdAt;

  @LastModifiedBy
  @Column(name = "updated_by")
  protected U updatedBy;

  @UpdateTimestamp
  @Temporal(TIMESTAMP)
  @Column(name = "updated_at")
  protected Date updatedAt;
}
