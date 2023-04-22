package com.nowcoder.community.controller;

import com.nowcoder.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
public class DataController {

    @Autowired
    private DataService dataService;

    // 访问统计页面
    @RequestMapping(path = "/data", method = {RequestMethod.GET, RequestMethod.POST})
    public String getDataPage() {
        return "/site/admin/data";
    }

    // 统计网站UV
    @RequestMapping(path = "/data/uv", method = RequestMethod.POST)
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,  //表单提交的其实是String，想转换成日期时要指明日期格式，否则没法转换成Data
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model) { //@DateTimeFormat能指明日期格式
        long uv = dataService.calculateUV(start, end);
        model.addAttribute("uvResult", uv);
        model.addAttribute("uvStartDate", start);
        model.addAttribute("uvEndDate", end);
        return "forward:/data";//return "/site/admin/data"; 也行  这里是返回一个模板，模板返回给DispatcherServlet，再做后续处理
        //forward转发，是我当前方法请求我只能做一半，我还需要另一个方法接着做请求，这个方法得是和我平级的，也是一个方法，不能是模板
        //转发，虽然程序跳到上面那个方法了，但这还是一个请求，转发是在一个请求内完成的，而我这里是post，转给/data时还是post请求，所以这就要求上面的方法得能支持post请求
    }

    // 统计活跃用户
    @RequestMapping(path = "/data/dau", method = RequestMethod.POST)
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                         @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model) {
        long dau = dataService.calculateDAU(start, end);
        model.addAttribute("dauResult", dau);
        model.addAttribute("dauStartDate", start);
        model.addAttribute("dauEndDate", end);
        return "forward:/data";
    }

}
