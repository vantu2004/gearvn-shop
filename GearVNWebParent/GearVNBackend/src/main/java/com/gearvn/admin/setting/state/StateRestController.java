package com.gearvn.admin.setting.state;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gearvn.admin.setting.country.CountryRepository;
import com.gearvn.common.entity.Country;
import com.gearvn.common.entity.CountryDTO;
import com.gearvn.common.entity.State;
import com.gearvn.common.entity.StateDTO;

@RestController
public class StateRestController {

	@Autowired
	private StateRepository stateRepository;

	@Autowired
	private CountryRepository countryRepository;

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

	@PostMapping("/states/save")
	public ResponseEntity<?> save(@RequestBody StateDTO stateDTO) {
		try {
			Optional<Country> country = this.countryRepository.findById(stateDTO.getCountryDTO().getId());

			if (country.isPresent()) {
				State state = new State();
				if (stateDTO.getId() != null) {
					state = State.builder().id(stateDTO.getId()).name(stateDTO.getName()).country(country.get())
							.build();
				} else {
					state = State.builder().name(stateDTO.getName()).country(country.get()).build();
				}

				State savedState = stateRepository.save(state);
				return ResponseEntity.ok(savedState.getId());
			}
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error saving state: " + e.getMessage());
		}
	}

	@DeleteMapping("/states/delete/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
		try {
			if (!stateRepository.existsById(id)) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("State not found with ID: " + id);
			}
			stateRepository.deleteById(id);
			return ResponseEntity.ok("State deleted successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error deleting state: " + e.getMessage());
		}
	}
}
