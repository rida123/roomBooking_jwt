package com.virtualpairprogrammers.roombooking.control;

import com.virtualpairprogrammers.roombooking.data.UserRepository;
import com.virtualpairprogrammers.roombooking.model.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @RequestMapping("")
    public ModelAndView listUsers() {
        return new ModelAndView("users/list", "users", userRepository.findAll());
    }

    @RequestMapping("/add")
    public ModelAndView addRoom() {
        return new ModelAndView("users/edit", "user", new User());
    }

    @RequestMapping("/edit")
    public ModelAndView editUser(@RequestParam Long userId) {
        User user = userRepository.findById(userId).get();
        return new ModelAndView("users/edit", "user", user);
    }

    @PostMapping("/save")
    public Object saveUser(@Valid User user, BindingResult bindingResult, RedirectAttributes attributes) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("users/edit", "user", user);
        }

        //TODO: THE PASSWORD SHOULD OF COURSE BE ENCODED BEFORE IT'S SAVED!
        userRepository.save(user);
        return "redirect:/users";

    }

    @RequestMapping("/delete")
    public String deleteUser(@RequestParam Long userId) {
        userRepository.deleteById(userId);
        return "redirect:/users";
    }

    @RequestMapping("/resetPW")
    public String resetUserPW(@RequestParam Long userId) {
        User user = userRepository.findById(userId).get();
        user.setPassword("secret");
        userRepository.save(user);
        return "redirect:/users";
    }
}
