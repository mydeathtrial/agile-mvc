package com.agile.mvc;

import com.agile.common.base.Constant;
import com.agile.common.base.RETURN;
import com.agile.common.base.poi.ExcelFile;
import com.agile.common.base.poi.SheetData;
import com.agile.common.factory.LoggerFactory;
import com.agile.common.mvc.service.MainService;
import com.agile.common.util.POIUtil;
import com.agile.common.util.RandomStringUtil;
import com.agile.mvc.entity.SysTaskEntity;
import com.agile.mvc.entity.SysTaskTargetEntity;
import com.agile.mvc.entity.SysUsersEntity;
import org.apache.commons.logging.Log;
import org.apache.logging.log4j.Level;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by 佟盟 on 2018/10/18
 */
@Service
public class Test extends MainService {
    private Log log = LoggerFactory.createLogger("自己的测试文件",this.getClass(), Level.ERROR,Level.INFO);

    private void test_log(){
        log.error("错误日志");
        log.info("提示日志");
    }
//    /**
//     * ES查询测试
//     */
//    private Object test_es(){
//        return esDao.getRepository(UserEntity.class).findAll();
//    }

    /**
     * POI测试
     */
    private RETURN test_poi(){
        List<MultipartFile> fileList = ((List<MultipartFile>) (getInParam("file1")));
        if(fileList==null)return RETURN.PARAMETER_ERROR;

        List<LinkedHashMap<String, Object>> data = POIUtil.readExcel(fileList.get(0));

        Workbook w = POIUtil.creatExcel(POIUtil.VERSION.V2008, SheetData.builder().setName("我的SHEET").setData(data).build());
        setOutParam(Constant.ResponseAbout.RESULT,new ExcelFile("测试文件",w));
        return RETURN.SUCCESS;
    }

    private String test_plain(){
        return "我是测试字符串";
    }

    private Object test_return(){
        return SysUsersEntity.builder().setName("土豆").setSaltKey("123").build();
    }

    private RETURN test_head(){
        if(!containsKey("id")){
            return RETURN.PARAMETER_ERROR;
        }

        setOutParam("id",getInParam("id"));
        return RETURN.SUCCESS;
    }

    private RETURN test_in_param(){
        if(!containsKey("id")){
            return RETURN.PARAMETER_ERROR;
        }

        setOutParam("id_source",getInParam("id"));
        setOutParam("id_string",getInParam("id",String.class));
        setOutParam("id_int",getInParam("id",int.class));
        setOutParam("id_array_source",getInParamOfArray("id"));
        setOutParam("id_array_int",getInParamOfArray("id",int.class));
        return RETURN.SUCCESS;
    }

    private Object test_auth(){
        return getUser();
    }


    private void test_query(){
        setOutParam("query1",dao.findAll(SysTaskEntity.class));
        setOutParam("query2",dao.findAll(SysTaskEntity.class,0,10));
        setOutParam("query3",dao.findAll(SysTaskTargetEntity.builder().setTargetClass("com.agile.mvc.Task").build()));
        setOutParam("query4",dao.findAll("select * from sys_users where salt_key = ?","admin"));
        setOutParam("query5",dao.findAllBySQL("select target_class from sys_task_target group by target_class","SELECT count(1) from (select target_class from sys_task_target group by target_class) t",6,10));
    }

    private Object test_save(){
        dao.save(SysUsersEntity.builder().setName("土豆泥").setSaltKey("123123123").setSysUsersId(RandomStringUtil.getRandom(8, RandomStringUtil.Random.NUMBER)).build());
        return RETURN.SUCCESS;
    }

    public Object test(){
        if(!containsKey("type")){
            return RETURN.PARAMETER_ERROR;
        }
        switch (getInParam("type",String.class)){
            case "1":
                test_log();
                return RETURN.SUCCESS;
//            case "2":
//                return test_es();
            case "3":
                return test_poi();
            case "4":
                return test_plain();
            case "5":
                return test_return();
            case "6":
                return test_head();
            case "7":
                return test_in_param();
            case "8":
                return test_auth();
            case "9":
                test_query();
                return RETURN.SUCCESS;
            default:
                return RETURN.SUCCESS;
        }
    }
}
