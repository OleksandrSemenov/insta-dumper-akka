package scanner.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import scanner.entities.User;
import scanner.repository.UserRepository;
import scanner.rest.exception.NotFoundException;

import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/username/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public User getByUserName(@PathVariable("name") String name) {
        User user = userRepository.findByUserName(name);

        if(user == null) {
            throw new NotFoundException();
        }

        return user;
    }

    @RequestMapping(value = "/email/{email}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public User getByEmail(@PathVariable("email") String email) {
        User user = userRepository.findByEmail(email);

        if(user == null) {
            throw new NotFoundException();
        }

        return user;
    }

    @RequestMapping(value = "/phone/{code}/{phone}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public User getByPhone(@PathVariable("code") String code, @PathVariable("phone") String phone) {
        User user = userRepository.findByPhoneCountryCodeAndPhoneNumber(code, phone);

        if(user == null) {
            throw new NotFoundException();
        }

        return user;
    }
}
