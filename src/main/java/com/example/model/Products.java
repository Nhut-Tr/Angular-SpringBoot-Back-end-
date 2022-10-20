package com.example.model;

import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name="ProductNG")
public class Products {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="Name")
	private String name;
	
	@Column(name="Image")
	private String img;
	
	@Column(name="Price")
	private double price;
	
	@Column(name="Description")
	private String description;
	public Products() {
		// TODO Auto-generated constructor stub
	}
	
	

	public Products(long id, String name, String img, double price, String description) {
		this.id = id;
		this.name = name;
		this.img = img;
		this.price = price;
		this.description = description;
	}



	public long getId() {
		return id;
	}



	public void setId(long id) {
		this.id = id;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getImg() {
		return img;
	}



	public void setImg(String img) {
		this.img = img;
	}



	public double getPrice() {
		return price;
	}



	public void setPrice(double price) {
		this.price = price;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Products[id="+id+",name="+name+",img="+img+",price="+price+",desc="+description+"]";
	}
	
	
	
	

}
