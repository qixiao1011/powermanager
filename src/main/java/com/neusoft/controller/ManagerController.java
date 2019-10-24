package com.neusoft.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.neusoft.bean.Manager;
import com.neusoft.bean.ManagerRoleRF;
import com.neusoft.dao.ManagerMapper;
import com.neusoft.dao.ManagerRoleRFMapper;
import com.neusoft.util.Contents;
import com.neusoft.util.ResultBean;
import com.neusoft.util.poi.PoiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

import static java.awt.SystemColor.info;

@Controller
@RequestMapping("admin/manager")
public class ManagerController {
   @Autowired
    private ManagerMapper mapper;
    @Autowired
    private ManagerRoleRFMapper rfMapper;

    @Autowired
    private RedisTemplate redisTemplate;


   @RequestMapping("html")
   public String page(){
       return "admin/manager/list";
   }

    /**
     *
     * @param index 当前页面码
     * @param size  每页显示数量
     * @param param  模糊查询参数
     * @param phone  根据手机号查询
     * @return
     */

   @RequestMapping("list")
    @ResponseBody
    public Object findall(
            Integer index,
            Integer size,
            String order,
            String prop,
            String param,
            String phone
   ){
       ResultBean bean=null;
       try{
           index=index==null?1:index;
           size=size==null?20:size;
           Map map=new HashMap();
           map.put("param",param);
           map.put("phone",phone);
           map.put("orderx",order);
           map.put("prop", Contents.to_col(prop));
//分页
           PageHelper.startPage(index,size);
           List<Manager> list=mapper.selectAll(map);
           PageInfo<Manager> info=new PageInfo<>(list);
           bean=new ResultBean(ResultBean.Code.SUCCESS);
           bean.setObj(info);
       }catch(Exception e){
           e.printStackTrace();
           bean=new ResultBean(ResultBean.Code.EXCEPTION);
           bean.setMsg(e.getMessage());
       }
           return  bean;
   }

    @RequestMapping("savepower")
    @ResponseBody
    public Object savepower(
            String roleids,
            Integer managerid
    ){

        ResultBean bean=null;
        try{
            String[] idsx=roleids.split("[,]");
            List<ManagerRoleRF> list =new ArrayList<>();
            for (String id:idsx
            ) {
                if(id==""){
                    break;
                }else{
                ManagerRoleRF rf=new ManagerRoleRF();
                rf.setRfCreatetime(new Date());
                rf.setRfLastmodify(new Date());
                rf.setRfMangerid(managerid);
                rf.setRfRoleid(Integer.valueOf(id));
                list.add(rf);}
            }
            int rows=0;
            rows=rfMapper.deletebyManagerid(managerid);
            if(list.size()>0){
                rows=rfMapper.insertAll(list);
            }

            if(rows>0){
                bean=new ResultBean(ResultBean.Code.SUCCESS);
            }else{
                bean=new ResultBean(ResultBean.Code.FAIL);
            }

        }catch(Exception e){
            e.printStackTrace();
            bean=new ResultBean(ResultBean.Code.EXCEPTION);
            bean.setMsg(e.getMessage());
        }
        return  bean;
    }
    @RequestMapping("deleteAll")
    @ResponseBody
    public Object deleteAll(
           @RequestParam("ids") String ids
    ){
        ResultBean bean=null;
        try{
            String[] idsx=ids.split("[,]");
            List<Integer> list =new ArrayList<>();
            for (String id:idsx
                 ) {
                list.add(Integer.valueOf(id));
            }


            int rows=mapper.deleteall(list);
            if(rows>0){
                bean=new ResultBean(ResultBean.Code.SUCCESS);
            }else{
                bean=new ResultBean(ResultBean.Code.FAIL);
            }

        }catch(Exception e){
            e.printStackTrace();
            bean=new ResultBean(ResultBean.Code.EXCEPTION);
            bean.setMsg(e.getMessage());
        }
        return  bean;
    }


    @RequestMapping("delete")
    @ResponseBody
    public Object delete(
          Integer id
    ){
        ResultBean bean=null;
        try{
            int rows=mapper.deleteByPrimaryKey(id);
            if(rows>0){
                bean=new ResultBean(ResultBean.Code.SUCCESS);
            }else{
                bean=new ResultBean(ResultBean.Code.FAIL);
            }

        }catch(Exception e){
            e.printStackTrace();
            bean=new ResultBean(ResultBean.Code.EXCEPTION);
            bean.setMsg(e.getMessage());
        }
        return  bean;
    }

    @RequestMapping("upload")
    @ResponseBody
    public Object upload(
            @RequestParam(value = "file",required = true) MultipartFile file,
         HttpServletRequest request
    ){
        ResultBean bean=null;
        try{
             if(file!=null){
String filepath=request.getSession().getServletContext().getRealPath("/uploadfiles");

                 Calendar calendar=Calendar.getInstance();
                 int year=calendar.get(Calendar.YEAR);
                 int month=calendar.get(Calendar.MONTH);
                 int day=calendar.get(Calendar.DATE);
                 String dirName="/"+year+"/"+month+"/"+day;

                 File file1=new File(filepath,dirName);
                 if(!file1.exists()){
                     boolean flag=file1.mkdirs();
                 }
                 //原始名称
                 String orgname=file.getOriginalFilename();
                  String fileName=UUID.randomUUID().toString()+orgname.substring(orgname.lastIndexOf("."));
                 //存储在数据库中的地址
                 String dbpath="/uploadfiles"+dirName+"/"+fileName;
                 //目标文件
                 File targetFile=new File(file1,fileName);
                 file.transferTo(targetFile);//copy进去

                 bean=new ResultBean(ResultBean.Code.SUCCESS);
                 bean.setObj(dbpath);

             }else {
                 bean=new ResultBean(ResultBean.Code.FAIL);
             }


        }catch(Exception e){
            e.printStackTrace();
            bean=new ResultBean(ResultBean.Code.EXCEPTION);
            bean.setMsg(e.getMessage());
        }
        return  bean;
    }


//批量导入
    @RequestMapping("import")
    @ResponseBody
    public Object importManager(
            @RequestParam(value = "file",required = true) MultipartFile file,
            HttpServletRequest request
    ){
        ResultBean bean=null;
        try{
          if(file!=null)
          {
             List<Manager> list= PoiUtil.importExcel(file,0,1,Manager.class);
              for (Manager m:list
                   ) {
                  m.setManagerCreatetime(new Date());
                  m.setManagerPassword("000000");
                  m.setManagerLastmodify(new Date());
              }
             int rows=mapper.insertAll(list);
             bean=new ResultBean(ResultBean.Code.SUCCESS);

            }else {
                bean=new ResultBean(ResultBean.Code.FAIL);
            }


        }catch(Exception e){
            e.printStackTrace();
            bean=new ResultBean(ResultBean.Code.EXCEPTION);
            bean.setMsg(e.getMessage());
        }
        return  bean;
    }

    /**
     * 保存和更新
     * @param manager
     * @return
     */
    @RequestMapping("saveOrupdate")
    @ResponseBody
    public Object save(
            Manager manager
    ){
        ResultBean bean=null;
        try{
            int rows=0;
            if(manager.getManagerId()!=null){
                manager.setManagerLastmodify(new Date());
                rows=mapper.updateByPrimaryKeySelective(manager);
            }else{
                manager.setManagerCreatetime(new Date());
                manager.setManagerLastmodify(new Date());
                manager.setManagerPassword("000000");
                rows=mapper.insertSelective(manager);
            }


            if(rows>0){
                manager=mapper.selectByPrimaryKey(manager.getManagerId());
                redisTemplate.opsForValue().set(manager.getManagerPhone(),manager);

                bean=new ResultBean(ResultBean.Code.SUCCESS);
            }else{
                bean=new ResultBean(ResultBean.Code.FAIL);
            }

        }catch(Exception e){
            e.printStackTrace();
            bean=new ResultBean(ResultBean.Code.EXCEPTION);
            bean.setMsg(e.getMessage());
        }
        return  bean;
    }

}
