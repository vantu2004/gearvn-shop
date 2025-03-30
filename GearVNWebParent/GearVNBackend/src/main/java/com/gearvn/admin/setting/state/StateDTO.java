package com.gearvn.admin.setting.state;

import com.gearvn.admin.setting.country.CountryDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class StateDTO {
	private Integer id;
	private String name;
	private CountryDTO countryDTO;
}
