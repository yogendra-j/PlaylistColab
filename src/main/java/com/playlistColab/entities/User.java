package com.playlistColab.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_users")
@Builder
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
    @Size(min = 1, max = 40)
	private String name;
	
    @Size(min = 1, max = 60)
	@Column(unique = true, nullable = false)
    @Email
	private String email;
	
    @NotBlank
    @Size(min = 6, max = 255)
	@JsonIgnore
	private String password;

	@Size(max = 255)
	@JsonIgnore
	private String spotifyAccessToken;

	@Size(max = 255)
	@JsonIgnore
	private String spotifyRefreshToken;

	@Size(max = 255)
	@JsonIgnore
	private String spotifyId;

	@JsonIgnore
	private Date spotifyAccessTokenExpiration;

}
