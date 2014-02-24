package de.techdev.trackr.web.api;

import de.techdev.trackr.domain.Company;
import de.techdev.trackr.domain.support.CompanyDataOnDemand;
import de.techdev.trackr.web.MockMvcTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Moritz Schulze
 */
public class CompanyResourceTest extends MockMvcTest {

    private final String companyJson = "{\"companyId\": 12345, \"name\": \"techdev\", \"address\": {\"street\": \"strasse\", \"houseNumber\": \"11\", \"city\": \"Karlsruhe\", \"zipCode\": \"12345\", \"country\": \"Germany\"}}";
    @Autowired
    private CompanyDataOnDemand companyDataOnDemand;

    @Before
    public void setUp() throws Exception {
        companyDataOnDemand.init();
    }

    @Test
    public void root() throws Exception {
        mockMvc.perform(
                get("/companies")
                        .session(basicSession()))
               .andExpect(status().isOk())
               .andExpect(content().contentType(standardContentType));
    }

    @Test
    public void one() throws Exception {
        Company company = companyDataOnDemand.getRandomObject();
        mockMvc.perform(
                get("/companies/" + company.getId())
                        .session(basicSession()))
               .andExpect(status().isOk())
               .andExpect(content().contentType(standardContentType));
    }

    @Test
    public void findByCompanyId() throws Exception {
        Company company = companyDataOnDemand.getRandomObject();
        mockMvc.perform(
                get("/companies/search/findByCompanyId")
                        .param("companyId", company.getCompanyId().toString())
                        .session(basicSession()))
               .andExpect(status().isOk())
               .andExpect(content().contentType(standardContentType));
    }

    @Test
    public void postAdmin() throws Exception {
        mockMvc.perform(
                post("/companies")
                        .session(adminSession())
                        .content(companyJson))
               .andExpect(status().isCreated());
    }

    @Test
    public void postForbiddenForSupervisor() throws Exception {
        mockMvc.perform(
                post("/companies")
                        .session(supervisorSession())
                        .content(companyJson))
               .andExpect(status().isForbidden());
    }

    @Test
    public void constraintViolation() throws Exception {
        mockMvc.perform(
                post("/companies")
                        .session(adminSession())
                        .content("{ \"companyId\": \"1234\" }"))
               .andExpect(status().isBadRequest());
    }
}