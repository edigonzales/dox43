
package ch.so.agi.dox43;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class MainController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        logger.info("ping");        
        return new ResponseEntity<String>("dox43", HttpStatus.OK);
    }    
}
