package com.gearvn.admin.setting.state;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.gearvn.common.entity.Country;
import com.gearvn.common.entity.State;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class StateRepositoryTests {
	@Autowired
	private StateRepository stateRepository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testCreateStatesInIndia() {
		Integer countryId = 1;
		Country country = entityManager.find(Country.class, countryId);

		State state1 = stateRepository.save(State.builder().name("West Bengal").country(country).build());
		State state2 = stateRepository.save(State.builder().name("Karnataka").country(country).build());
		State state3 = stateRepository.save(State.builder().name("Punjab").country(country).build());
		State state4 = stateRepository.save(State.builder().name("Uttar Pradesh").country(country).build());

		List<State> states = this.stateRepository.saveAll(List.of(state1, state2, state3, state4));
		
		assertThat(states).isNotNull();
		assertThat(states.size()).isGreaterThan(0);
	}

	@Test
	public void testCreateStatesInUS() {
		Integer countryId = 2;
		Country country = entityManager.find(Country.class, countryId);

		State state1 = stateRepository.save(State.builder().name("Washington").country(country).build());
		State state2 = stateRepository.save(State.builder().name("California").country(country).build());
		State state3 = stateRepository.save(State.builder().name("Texas").country(country).build());
		State state4 = stateRepository.save(State.builder().name("New York").country(country).build());

		List<State> states = this.stateRepository.saveAll(List.of(state1, state2, state3, state4));
		
		assertThat(states).isNotNull();
		assertThat(states.size()).isGreaterThan(0);
	}

	@Test
	public void testListStatesByCountry() {
		Integer countryId = 2;
		Country country = entityManager.find(Country.class, countryId);
		List<State> listStates = stateRepository.findByCountryOrderByNameAsc(country);

		listStates.forEach(System.out::println);

		assertThat(listStates.size()).isGreaterThan(0);
	}

	@Test
	public void testUpdateState() {
		Integer stateId = 3;
		String stateName = "Tamil Nadu";
		State state = stateRepository.findById(stateId).get();

		state.setName(stateName);
		State updatedState = stateRepository.save(state);

		assertThat(updatedState.getName()).isEqualTo(stateName);
	}

	@Test
	public void testGetState() {
		Integer stateId = 1;
		Optional<State> findById = stateRepository.findById(stateId);
		assertThat(findById.isPresent());
	}

	@Test
	public void testDeleteState() {
		Integer stateId = 8;
		stateRepository.deleteById(stateId);

		Optional<State> findById = stateRepository.findById(stateId);
		assertThat(findById.isEmpty());
	}
}
