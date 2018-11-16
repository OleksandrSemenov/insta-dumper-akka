package scanner.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import scanner.Scanner;
import scanner.rest.exception.ScannerAlreadyWorksException;

@RestController
@RequestMapping("/api/scan/")
public class ScanRestController {
    @Autowired
    private Scanner scanner;

    @RequestMapping(value = "/start-scan/{name}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void startScan(@PathVariable("name") String name){
        if(scanner.isScannerWork()){
            throw new ScannerAlreadyWorksException();
        }

        scanner.startScanWithName(name);
    }
}
