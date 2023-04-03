package com.nowcoder.community.controller;

import com.nowcoder.community.service.AService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

//下面这俩是SpringMVC的注解   /alpha是给这个类取一个访问的名
@Controller
@RequestMapping("/alpha")
public class AController {

    //依赖注入具体实例 controller处理浏览器请求，调用service完成相应业务，service会调用dao完成数据库操作，就可以通过依赖注入实现
    @Autowired
    private AService aService;


    @RequestMapping("/hello")  //给这个方法也取一个名/访问路径
    @ResponseBody //正常应该返回一个页面，但你只返回了一个String，所以要加这个注解
    public String sayHello(){
        return "Hello";
    }


    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return aService.find();
    }

    //SpringMVC框架中，DispatcherServlet自动运行不用变成，model就是数据，不太用写代码，我们要写的就是controller和view  view是在templates文件下写
    //Spring框架下获得请求对象 相应对象  里面分别封装请求数据  相应数据，分别能处理请求和响应

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        //想获取请求、相应对象，只需要在方法上加以声明即可，声明这两个类型后，DispatcherServlet在调用这个方法时，会自动把对象传给你（它在底层就把这俩对象创建好了）
        //没有返回值，因为通过response对象，我们直接可以向浏览器输入任何值，就不依赖于返回值了
        //获取请求数据
        System.out.println(request.getMethod());//位于第一行  起始行/请求行
        System.out.println(request.getServletPath());//返回请求路径  位于第一行  起始行/请求行

        Enumeration<String> enumeration=request.getHeaderNames();//得到所有请求行的Key  请求行是Key Value结构  得到的是一个迭代器
        //迭代器遍历
        while(enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value=request.getHeader(name);
            System.out.println(name+":"+value);
        }
        //这一部分获得的是请求消息头的数据  若干行

        System.out.println(request.getParameter("code"));//请求体  包含业务数据，也就是参数 就是在url后面加?code=.. 这样加参数

        //返回响应数据
        response.setContentType("text/html;charset=utf-8");//设置返回数据的类型  网页类型的文本  charset字符集 支持中文
        try(
                PrintWriter writer = response.getWriter(); //响应就是通过输出流向浏览器输出   获取输出流
                ) {
            writer.write("<h1>Niuke</h1>");//通过writer向浏览器打印网页
        } catch (IOException e) {
            throw new RuntimeException(e);
        }//在小括号里创建writer，这样就不用在后面写finally writer，close了
        //以上是底层处理响应和请求的方式，很麻烦
    }
    //封装后处理请求和响应的方式  更简单  处理请求和响应方式分别是获取请求数据，返回响应数据

    //GET请求  假设一个查询学生分页的情景  /students?current=1&limit=20
    @RequestMapping(path = "/students",method = RequestMethod.GET) //声明请求方式，强制这个方法必须是GET请求才能访问到
    @ResponseBody
    public String getStudents(@RequestParam(name = "current",required = false,defaultValue = "1") int current,
                              @RequestParam(name = "limit",required = false,defaultValue = "20")int limit){
        //重点关注怎么得到请求数据中的参数值  也就是current=1&limit=20这个信息
        //其实只需要在方法里加入相应的参数，只要你这个方法里的参数名，和你传过来的url？后面的参数名一样   DispatcherServlet检测到这个参数时，会把request当中与之匹配的参数直接赋值给它
        //但有些时候。url？后面是没有参数的  比如第一次访问学生页面，往往是没有？以及后面参数的
        //这时候就要加入注解来进一步进行更详尽的操作 通过RequestParam这个注解，对参数的注入进行更详尽的声明
        //注解意思就是，request中的名为current的参数，要赋值给current这个参数
        //required = false表明你也可以不传这个参数进来  不传时defaultValue = "1"，默认值为1
        System.out.println(current);
        System.out.println(limit);;
        return "some students";
    }

    //有些时候，参数会成为路径中一部分  比如我要访问id为123的学生  /students?id=123可以，有些时候也会/students/123，这时上面的方法就不行了
    @RequestMapping(path = "/student/{id}",method = RequestMethod.GET)  //这个id是变量，用{}括起来
    @ResponseBody
    public String getStudent(@PathVariable("id")int id){
        //这是我们就要用另一个注解了,注解会从路径中得到这个变量，然后赋值给方法参数
        System.out.println(id);
        return "a student";
    }
    //GET请求，传参有两种方式，分别是?和直接在路径中，要用以上两种对应的注解来获得参数

    //POST请求  向服务器提交数据  其实还有其他请求，但GET POST就够了
    //浏览器要想向服务器提交数据，浏览器首先得打开一个带有表单的网页
    //templates存放模板，里面是有表达式的，能够被model中数据替换的，所以是一种动态的
    //静态资源（网页 图片 css文件等）要放到static中
    //GET请求也能向服务器传参，但是GET传参，参数是在明面上，会在路径上带上?后面跟一长串参数  并且路径长度不是无限制的， 如果很多参数，也传不下，数据量有限
    //所以提交数据一般不用GET请求，用POST
    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name,int age){
        //怎么获得post请求中的参数呢，只需要在方法里加入/声明相应的参数，只要你这个方法里的参数名，和你提交表单的参数名一样，就把post request当中与之匹配的参数直接赋值给它
        System.out.println(name);
        System.out.println(age);
        //也可以像上面GET一样用@RequestParam注解来传参
        return "success";
    }

    //响应动态HTML数据
    //假设，我们要查询老师数据
    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    //不要加@ResponseBody注解了，不加这个注解默认就是返回HTML
    public ModelAndView getTeacher(){ //ModelAndView其实就是Model和View，两份数据
        ModelAndView mav=new ModelAndView();//实例化
        mav.addObject("name","张三");//向里面传值
        mav.addObject("age",30);
        mav.setViewName("/demo/view");  //往对象里设置一个模板的路径和名字  模板放在templates目录下，templates目录在路径中不用写，它的下级目录要写
        //templates目录下模板文件有固定格式，得是html类型的   所以路径只写文件名就行，后面的后缀.html不用写
        //这个就相当于把Model和View封装到一个对象中了，然后送给模板引擎进行渲染
        return mav;
    }
    //还有一种响应HTML数据的方式
    @RequestMapping(path = "/school",method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","北京大学");
        model.addAttribute("age",80);
        return "/demo/view";//注意，这里return的是view的路径
        //但这里没Model了，就不是动态HTML了，model必须得有，所以要让Model作为方法参数加入
        //这个Model对象不是我们自己创建的，是DispatcherServlet调用这个方法时，发现有Model对象参数，就会自动实例化这个Model对象，传进来
        //DispatcherServlet持有该对象的引用，在方法里对model加东西，DispatcherServlet也能看到
        //再加上返回的view给了DispatcherServlet，所以DispatcherServlet照样还是能得到Model+View，然后可以送到模板引擎进行视图渲染
        //这种方式返回的视图名字，更方便一些
    }

    //除了向浏览器响应String HTML数据，其实还能返回任意数据  比如json数据
    //响应JSON数据 一般是在异步请求会响应JSON  异步请求：整个网页没刷新，但它访问了一下服务器  比如检测用户名是否占用时，并没有刷新网页，所以不可能返回HTML，这时其实就返回了JSON
    //Java对象返回给浏览器，浏览器解析时用的JS语言，JS语言渴望得到的是JS对象  但Java对象没法与JS对象进行转换  而JSON可以在二者之间进行兼容
    //JSON实际上就是具有特定形式的字符串  这样Java对象可以转成JSON字符串，字符串可以随意流通，流通到JS语言中，转成JS对象
    //JSON字符串是我们常用的跨语言方案之一  JSON字符串格式：  {”name“:”张三“,”age“:23,”salary“:8000.0}
    @RequestMapping(path = "/emp",method = RequestMethod.GET)  //查询一个员工
    @ResponseBody //不加的话，会认为返回的是HTML，只有加了，才会认为返回的是字符串
    public Map<String,Object> getEmp(){  //用Map对其封装，也可以新声明一个Emp员工类，返回Emp类型  效果一样
        Map<String,Object> emp=new HashMap<>();
        emp.put("name","张三");
        emp.put("age",23);
        emp.put("salary",8000.00);
        return emp;
        //DS调用时一看你加了@ResponseBody注解，返回的是Map<String,Object>类型，它自动会将map转换成一个JSON字符串，发送给浏览器
        //请求响应的类型是application/json
    }

    //返回一组员工
    @RequestMapping(path = "/emps",method = RequestMethod.GET)  //查询一个员工
    @ResponseBody //不加的话，会认为返回的是HTML，只有加了，才会认为返回的是字符串
    public List<Map<String,Object>> getEmps(){  //用Map对其封装，也可以新声明一个Emp员工类，返回Emp类型  效果一样
        List<Map<String,Object>> emps=new ArrayList<>();
        Map<String,Object> emp=new HashMap<>();
        emp.put("name","张三");
        emp.put("age",23);
        emp.put("salary",8000.00);
        emps.add(emp);

        emp=new HashMap<>();
        emp.put("name","李四");
        emp.put("age",24);
        emp.put("salary",7000.00);
        emps.add(emp);



        return emps;
        //DS调用时一看你加了@ResponseBody注解，返回的是Map<String,Object>类型，它自动会将map转换成一个JSON字符串，发送给浏览器
        //请求响应的类型是application/json
    }



}
