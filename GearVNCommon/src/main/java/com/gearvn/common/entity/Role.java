package com.gearvn.common.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length = 32, nullable = false, unique = true)
	private String name;
	
	@Column(length = 128, nullable = false)
	private String description;

	public Role(String name) {
		this.name = name;
	}
	
	public Role(String name, String desciption) {
		this.name = name;
		this.description = desciption;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
