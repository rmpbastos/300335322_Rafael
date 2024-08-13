package org.example.sw_300335322_rafael.web;

import org.example.sw_300335322_rafael.entities.Customer;
import org.example.sw_300335322_rafael.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setCustomerName("John Doe");
        customer.setSeatNumber("1A");
        customer.setTransactionDate(new Date());
    }

    @Test
    void findAll() throws Exception {
        List<Customer> customerList = new ArrayList<>();
        customerList.add(customer);
        customerList.add(customer);

        when(customerRepository.findAll()).thenReturn(customerList);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("customers", hasSize(2)))
                .andExpect(view().name("index"));

        // Verify that findAll() was called at least once
        verify(customerRepository, atLeastOnce()).findAll();
    }

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private Controller controller;

    @BeforeEach
    public void setUpMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveCustomer() {
        List<Customer> customers = new ArrayList<>();
        when(customerRepository.findAll()).thenReturn(customers);

        String viewName = controller.saveCustomer(customer, model, redirectAttributes);

        assertEquals("redirect:/", viewName);
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    public void testSaveCustomerWithEmptyName() {
        customer.setCustomerName("");
        List<Customer> customers = new ArrayList<>();
        when(customerRepository.findAll()).thenReturn(customers);

        String viewName = controller.saveCustomer(customer, model, redirectAttributes);

        assertEquals("redirect:/", viewName);
        verify(customerRepository, never()).save(customer);
        verify(redirectAttributes, times(1)).addFlashAttribute("errorMessage", "Seat or name cannot be blank");
    }

    @Test
    public void testEditCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.ofNullable(customer));

        String viewName = controller.showEditForm(1L, model);

        assertEquals("editForm", viewName);
        verify(model, times(1)).addAttribute("customer", customer);
    }

    @Test
    public void testDeleteCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.ofNullable(customer));

        String viewName = controller.deleteCustomer(1L, redirectAttributes);

        assertEquals("redirect:/", viewName);
        verify(customerRepository, times(1)).delete(customer);
    }
}
