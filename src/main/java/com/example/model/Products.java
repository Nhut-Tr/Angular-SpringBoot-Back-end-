package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="ProductNG")
public class Products  extends Base<String>{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="Name")
	private String name;
	
	@Column(name="Image")
	private String img;
	
	@Column(name="Price")
	private Double price;
	
	@Column(name="Description")
	private String description;

//	@Column(name = "Added_on")
//	private String added_on;

	@Column(name="Quantity")
	private Double quantity;

	@Column(name="Status")
	private Boolean status;



	@JsonIgnore
	@OneToMany(mappedBy = "products")
	List <Cart> carts ;

	@JsonIgnore
	@OneToMany(mappedBy = "products")
	List<CheckoutCart> checkoutCarts;





	
	
	
	

}
