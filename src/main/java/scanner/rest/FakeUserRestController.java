package scanner.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import scanner.entities.FakeUser;
import scanner.repository.FakeUserRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fakeUsers")
public class FakeUserRestController {
    @Autowired
    private FakeUserRepository fakeUserRepository;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<FakeUser>> getAllFakeUsers() {
        List<FakeUser> fakeUsers = fakeUserRepository.findAll();

        if(fakeUsers.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(fakeUsers, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<FakeUser> save(@RequestBody @Valid FakeUser fakeUser) {
        if(fakeUser == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        fakeUserRepository.save(fakeUser);
        return new ResponseEntity<>(fakeUser, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<FakeUser> update(@RequestBody @Valid FakeUser fakeUser) {
        if(fakeUser == null || fakeUser.getId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        fakeUserRepository.save(fakeUser);
        return new ResponseEntity<>(fakeUser, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<FakeUser> delete(@PathVariable("id") Integer id) {
        FakeUser fakeUser = fakeUserRepository.getOne(id);

        if(fakeUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        fakeUserRepository.delete(fakeUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<FakeUser> getById(@PathVariable("id") Integer id) {
        Optional<FakeUser> optional = fakeUserRepository.findById(id);
        FakeUser fakeUser = optional.get();
        
        if(fakeUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(fakeUser, HttpStatus.OK);
    }
}
