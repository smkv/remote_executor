package ee.smkv.executor.remote.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/applications")
public class Applications {

    @RequestMapping("")
    public String index(){
        return "applications";
    }




}