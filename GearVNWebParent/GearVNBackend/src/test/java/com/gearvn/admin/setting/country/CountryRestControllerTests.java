package com.gearvn.admin.setting.country;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gearvn.common.entity.Country;

import jakarta.transaction.Transactional;

// chạy ứng dụng trong môi trương kiểm thử, đảm bảo tất cả bean đc load lên phục vụ kiểm thử
@SpringBootTest
// công cụ giúp mô phỏng request Http mà ko cần khởi động server thật
@AutoConfigureMockMvc
@Rollback(false)
public class CountryRestControllerTests {

	// inject đối tượng thực hiện yêu cầu Http
	@Autowired
	private MockMvc mockMvc;

	// ép json về object
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CountryRepository countryRepository;

	// thêm dependency security-test và dùng @WithMockUser để test với security
	@Test
	// có thể fake user
	@WithMockUser(username = "nam@codejava.net", password = "123456", roles = "Admin")
	public void testGetAllCountries() throws Exception {
		String url = "/countries/list";
		MvcResult result = mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print()).andReturn();

		String json = result.getResponse().getContentAsString();
		System.out.println(json);

		/*
		 * dùng readValue với chuỗi Json thô, convertValue chỉ dùng cho JsonNode hoặc
		 * kiểu khác
		 */
		Country[] countries = objectMapper.readValue(json, Country[].class);
		for (Country country : countries) {
			System.out.println(country);
		}
	}

	@Test
	@WithMockUser(username = "nam@codejava.net", password = "123456", roles = "Admin")
	public void testCreateCountry() throws JsonProcessingException, Exception {
		String url = "/countries/save";

		Country country = Country.builder().name("Canada").code("CA").build();

		/*
		 * xác định kiểu gửi đi là "application.json" và ép Country về json để bên kia
		 * nhận Json và ép lại về Object, khi gửi bằng phương thức thì phải kèm theo
		 * csrf token
		 */
		MvcResult result = mockMvc.perform(post(url).contentType("application/json")
				.content(objectMapper.writeValueAsString(country)).with(csrf())).andDo(print())
				.andExpect(status().isCreated()).andReturn();

		String response = result.getResponse().getContentAsString();

		System.out.println(response);
	}

	@Test
	// Đảm bảo method truy vấn Country chạy trong một transaction để tránh mất
	// session
	@Transactional
	public void testGetCountry() {
		Optional<Country> country = this.countryRepository.findById(1);

		assertThat(country).isPresent();

		System.out.println(country.get());
	}

	@Test
	@WithMockUser(username = "nam@codejava.net", password = "123456", roles = "Admin")
	@Transactional
	public void testUpdateCountry() throws JsonProcessingException, Exception {
		String url = "/countries/save";

		Country country = this.countryRepository.findById(1).orElse(null);
		if (country != null) {

			Country newCountry = Country.builder().id(country.getId()).name("Thailand").code(country.getCode())
					.build();

			MvcResult result = mockMvc.perform(post(url).contentType("application/json")
					.content(objectMapper.writeValueAsString(newCountry)).with(csrf())).andDo(print())
					.andExpect(status().isCreated()).andReturn();

			String response = result.getResponse().getContentAsString();

			System.out.println(response);
		}
	}
	
	@Test
	@WithMockUser(username = "nam@codejava.net", password = "123456", roles = "Admin")
	public void testDeleteCountry() throws Exception {
		String url = "/countries/delete/6";
		mockMvc.perform(get(url)).andExpect(status().isOk());
		
		Country country = this.countryRepository.findById(6).orElse(null);
		assertThat(country).isNull();
	}
}
