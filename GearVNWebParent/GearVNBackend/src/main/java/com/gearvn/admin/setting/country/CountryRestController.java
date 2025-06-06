package com.gearvn.admin.setting.country;

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

import com.gearvn.common.entity.Country;
import com.gearvn.common.entity.CountryDTO;

@RestController
public class CountryRestController {

	@Autowired
	private CountryRepository countryRepository;

	@GetMapping("/countries/list")
	public ResponseEntity<?> getAllCountry() {
		try {
			List<Country> countries = countryRepository.findAllByOrderByNameAsc();
			List<CountryDTO> result = countries.stream()
					.map(country -> new CountryDTO(country.getId(), country.getName(), country.getCode()))
					.collect(Collectors.toList());
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving countries: " + e.getMessage());
		}
	}

	@PostMapping("/countries/save")
	public ResponseEntity<String> saveCountry(@RequestBody Country country) {
		try {
			Country savedCountry = countryRepository.save(country);
			return ResponseEntity.status(HttpStatus.CREATED).body("Saved: " + savedCountry.getId());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving country");
		}
	}

	@DeleteMapping("/countries/delete/{id}")
	public ResponseEntity<String> deleteCountry(@PathVariable Integer id) {
		try {
			Optional<Country> country = countryRepository.findById(id);
			if (country.isPresent()) {
				countryRepository.deleteById(id);
				return ResponseEntity.ok("Deleted country with ID: " + id);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Country not found");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting country");
		}
	}
}
