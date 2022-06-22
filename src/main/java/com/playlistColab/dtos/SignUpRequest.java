package com.playlistColab.dtos;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Data
public class SignUpRequest {

    @Size(min = 1, max = 40)
	private String name;
	
    @Size(min = 1, max = 60)
	@Column(unique = true, nullable = false)
    @Email
	private String email;
	
    @NotBlank
    @Size(min = 6, max = 40)
	private String password;
}
