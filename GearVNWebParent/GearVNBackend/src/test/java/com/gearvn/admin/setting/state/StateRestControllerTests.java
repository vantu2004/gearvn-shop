package com.gearvn.admin.setting.state;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gearvn.admin.setting.country.CountryDTO;
import com.gearvn.admin.setting.country.CountryRepository;
import com.gearvn.common.entity.Country;
import com.gearvn.common.entity.State;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Rollback(false)
public class StateRestControllerTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private StateRepository stateRepository;

	@Autowired
	private CountryRepository countryRepository;

	@Test
	@WithMockUser(username = "Rolo", password = "rolo123", roles = "Admin")
	public void testGetAllStates() throws Exception {
		String url = "/states/list_by_country/1";

		MvcResult mvcResult = mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print()).andReturn();

		String jsonData = mvcResult.getResponse().getContentAsString();
		System.out.println(jsonData);

		StateDTO[] stateDTOs = objectMapper.readValue(jsonData, StateDTO[].class);
		for (StateDTO stateDTO : stateDTOs) {
			System.out.println(stateDTO);
		}
	}

	@Test
	@Transactional
	@WithMockUser(username = "nam", password = "something", roles = "Admin")
	public void testCreateState() throws Exception {
		String url = "/states/save";
		Integer countryId = 3;
		
		Country country = countryRepository.findById(countryId).get();
		CountryDTO countryDTO = new CountryDTO(country.getId(), country.getName(), country.getCode());
		
		// State state = State.builder().name("Robert").country(country).build();
		StateDTO stateDTO = StateDTO.builder().name("Robert").countryDTO(countryDTO).build();

		MvcResult result = mockMvc.perform(
				post(url)
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(stateDTO))
				.with(csrf()))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		String response = result.getResponse().getContentAsString();
		Integer stateId = Integer.parseInt(response);
		Optional<State> findById = stateRepository.findById(stateId);

		assertThat(findById.isPresent());
	}

	@Test
	@WithMockUser(username = "nam", password = "something", roles = "Admin")
	@Transactional
	public void testUpdateState() throws Exception {
		String url = "/states/save";
		Integer stateId = 16;
		String stateName = "Washington";

		State state = stateRepository.findById(stateId).get();
		Country country = state.getCountry();
		CountryDTO countryDTO = new CountryDTO(country.getId(), country.getName(), country.getCode());
		StateDTO stateDTO = StateDTO.builder().id(stateId).name(stateName).countryDTO(countryDTO).build();

		mockMvc.perform(
				post(url)
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(stateDTO))
				.with(csrf()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(String.valueOf(stateId)));

		Optional<State> findById = stateRepository.findById(stateId);
		assertThat(findById.isPresent());

		State updatedState = findById.get();
		assertThat(updatedState.getName()).isEqualTo(stateName);
	}

	@Test
	@WithMockUser(username = "nam", password = "something", roles = "Admin")
	public void testDeleteState() throws Exception {
	    Integer stateId = 6;
	    String uri = "/states/delete/" + stateId;

	    mockMvc.perform(delete(uri)
	            .with(csrf()))
	            .andExpect(status().isOk());

	    Optional<State> findById = stateRepository.findById(stateId);

	    assertThat(findById).isNotPresent();
	}

}
