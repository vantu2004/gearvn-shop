package com.gearvn.site.state;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.gearvn.common.entity.Country;
import com.gearvn.common.entity.CountryDTO;
import com.gearvn.common.entity.State;
import com.gearvn.common.entity.StateDTO;

@RestController
public class StateRestController {

	@Autowired
	private StateRepository stateRepository;

	@GetMapping("/states/list_by_country/{id}")
	public ResponseEntity<?> listByCountry(@PathVariable("id") Integer countryId) {
		try {
			List<State> listStates = stateRepository
					.findByCountryOrderByNameAsc(Country.builder().id(countryId).build());
			List<StateDTO> result = listStates.stream()
					.map(state -> new StateDTO(state.getId(), state.getName(), new CountryDTO(
							state.getCountry().getId(), state.getCountry().getName(), state.getCountry().getCode())))
					.collect(Collectors.toList());
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving states: " + e.getMessage());
		}
	}

}
