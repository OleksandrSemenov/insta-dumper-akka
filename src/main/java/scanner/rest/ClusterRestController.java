package scanner.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import scanner.ClusterService;

@RestController
@RequestMapping("/api/cluster/")
public class ClusterRestController {
    @Autowired
    private ClusterService clusterService;

    @RequestMapping(value = "/start-node/{port}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void startNode(@PathVariable("port") String port){
        clusterService.startNode(port);
    }
}
