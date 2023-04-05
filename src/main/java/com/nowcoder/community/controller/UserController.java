package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger= LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domainPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){  //Springmvc给我们提供了一个专有的数据类型来接受文件 MultipartFile
        if(headerImage==null) {
            model.addAttribute("error","您还没有选择图片");
            return "/site/setting";
        }

        String filename = headerImage.getOriginalFilename();//用户传的时候 原始文件名
        String suffix = filename.substring(filename.lastIndexOf("."));//从最后一个点的索引位置往后截取，也就是取到了文件类型的后缀
        if(StringUtils.isBlank(suffix)) {
            model.addAttribute("error","文件格式不正确");
            return "/site/setting";
        }

        //生成随机文件名
        filename=CommunityUtil.generateUUID()+suffix;
        //确定文件存放路径
        File dest=new File(uploadPath+"/"+filename);
        try {
            //存储文件
            headerImage.transferTo(dest);  //MultipartFile提供的方法  把当前文件内容写到目标文件
        } catch (IOException e) {
            logger.error("上传文件失败："+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常",e);
        }

        //更新当前用户头像路径(web访问路径)
        //http://localhost:8080/community/user/header/xxx.png
        User user=hostHolder.getUser();
        String headerUrl=domainPath+contextPath+"/user/header/"+filename;
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //服务器存放路径
        fileName=uploadPath+"/"+fileName;
        //文件后缀
        String suffix=fileName.substring(fileName.lastIndexOf("."));
        //响应图片，向浏览器输出图片
        response.setContentType("image/"+suffix); //声明图片类型
        try(
                //要输出文件，要创建一个文件的输入流，一边读一边才能输出
                FileInputStream fis=new FileInputStream(fileName); //输入流是我们自己创建的，spring不管，我们得手动关闭
        ) {
            OutputStream os = response.getOutputStream(); //输出流 springmvc会帮我们自动关闭，因为response是由其管理的
            byte[] buffer=new byte[1024]; //缓冲
            int b=0; //游标
            while((b=fis.read(buffer))!=-1) {
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败"+e.getMessage());
        }
    }
}
