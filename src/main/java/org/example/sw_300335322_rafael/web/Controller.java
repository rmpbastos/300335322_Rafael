package org.example.sw_300335322_rafael.web;


// GITHUB REPOSITORY: https://github.com/rmpbastos/300335322_Rafael.git

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.example.sw_300335322_rafael.entities.Customer;
import org.example.sw_300335322_rafael.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.regex.Pattern;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Controller
@AllArgsConstructor
public class Controller {

    @Autowired
    private CustomerRepository customerRepository;

    private static final int TOTAL_SEATS = 20;
    private static final Pattern SEAT_CODE_PATTERN = Pattern.compile("^[1-4][A-E]$");

    @GetMapping(path = "/")
    public String displayIndexPage(Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        model.addAttribute("currentDate", LocalDate.now().format(formatter));
        model.addAttribute("sale", new Customer());
        List<Customer> customers = customerRepository.findAll();
        model.addAttribute("customers", customers);

        Map<String, String> seatMap = new HashMap<>();
        for (Customer customer : customers) {
            seatMap.put(customer.getSeatNumber(), customer.getCustomerName());
        }
        model.addAttribute("seatMap", seatMap);

        int remainingSeats = TOTAL_SEATS - customers.size();
        model.addAttribute("remainingSeats", remainingSeats);

        return "index";
    }

//    @GetMapping(path = "/")
//    public String displayIndexPage(Model model) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        model.addAttribute("currentDate", LocalDate.now().format(formatter));
//        model.addAttribute("sale", new Customer());
//        List<Customer> customers = customerRepository.findAll();
//        model.addAttribute("customers", customers);
//
//        Map<String, String> seatMap = new HashMap<>();
//        for (Customer customer : customers) {
//            seatMap.put(customer.getSeatNumber(), customer.getCustomerName());
//        }
//        model.addAttribute("seatMap", seatMap);
//
//        int remainingSeats = TOTAL_SEATS - customers.size();
//        model.addAttribute("remainingSeats", remainingSeats);
//
//        return "index";
//    }

    @PostMapping("/save")
    public String saveCustomer(@ModelAttribute("sale") Customer customer, Model model, RedirectAttributes redirectAttributes) {
        List<Customer> customers = customerRepository.findAll();

        // Validation checks
        if (customer.getCustomerName() == null || customer.getCustomerName().isEmpty() ||
                customer.getSeatNumber() == null || customer.getSeatNumber().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Seat or name cannot be blank");
            return "redirect:/";
        }

        if (!SEAT_CODE_PATTERN.matcher(customer.getSeatNumber()).matches()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please follow the seat code format");
            return "redirect:/";
        }

        if (customers.stream().anyMatch(c -> c.getSeatNumber().equals(customer.getSeatNumber()))) {
            redirectAttributes.addFlashAttribute("errorMessage", "The seat is already taken");
            return "redirect:/";
        }

        customerRepository.save(customer);
        return "redirect:/";
    }

//    @PostMapping("/save")
//    public String saveCustomer(@ModelAttribute Customer customer) {
//        customerRepository.save(customer);
//        return "redirect:/";
//    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid customer Id:" + id));
        model.addAttribute("customer", customer);
        return "editForm";
    }

    @PostMapping("/update/{id}")
    public String updateCustomer(@PathVariable("id") Long id, @ModelAttribute("customer") Customer customer, RedirectAttributes redirectAttributes) {
        Customer existingCustomer = customerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid customer Id:" + id));
        existingCustomer.setCustomerName(customer.getCustomerName());
        existingCustomer.setSeatNumber(customer.getSeatNumber());
        existingCustomer.setTransactionDate(customer.getTransactionDate());

        customerRepository.save(existingCustomer);
        redirectAttributes.addFlashAttribute("message", "Customer updated successfully");
        return "redirect:/";
    }


    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid customer Id:" + id));
        customerRepository.delete(customer);
        redirectAttributes.addFlashAttribute("message", "Customer deleted successfully");
        return "redirect:/";
    }

}
