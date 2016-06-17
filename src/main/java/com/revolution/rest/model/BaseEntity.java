 package com.revolution.rest.model;
 
 import javax.persistence.Column;
 import javax.persistence.GeneratedValue;
 import javax.persistence.GenerationType;
 import javax.persistence.Id;
 import javax.persistence.MappedSuperclass;
 
 @MappedSuperclass
 public abstract class BaseEntity<PrimaryKey>
 {
 
   @Id
   @Column(name="id", unique=true)
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private PrimaryKey id;
 
   public PrimaryKey getId()
   {
     return this.id;
   }
 
   public void setId(PrimaryKey id) {
     this.id = id;
   }
 }

