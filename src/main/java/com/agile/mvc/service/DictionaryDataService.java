package com.agile.mvc.service;

import com.agile.common.annotation.Mapping;
import com.agile.common.base.RETURN;
//import com.agile.common.base.ResponseData;
import com.agile.common.base.ResponseFile;
import com.agile.common.mvc.service.BusinessService;
import com.agile.mvc.entity.Asd;
import org.springframework.stereotype.Service;
import com.agile.mvc.entity.DictionaryDataEntity;

import java.io.FileNotFoundException;

/**
 * Created by 佟盟
 */
@Service
public class DictionaryDataService extends BusinessService<DictionaryDataEntity> {
    @Mapping(value = "/test")
    public Object test() throws FileNotFoundException {
        setOutParam("asd",new ResponseFile("我得文件","application/octet-stream",this.getClass().getResourceAsStream("/com/agile/conf/agile1.yml")));
        return new ResponseFile("我得文件","application/octet-stream",this.getClass().getResourceAsStream("/com/agile/conf/agile.yml"));
    }

    @Mapping(value = "/test2")
    public Object test2() {
        setOutParam("a","123");
        setOutParam("b","123123");
        return RETURN.SUCCESS;
    }

//    @Mapping(value = "/test3")
//    public Object test3() {
////        return new ResponseData(ResponseData.STATUS.success,"message","12","content");
//    }
//
//    @Mapping(value = "/test4")
//    public Object test4() {
////        return ResponseData.build(ResponseData.STATUS.success,"content","agile.exception.CustomException","1000","message");
//    }
//
//    @Mapping(value = "/test5")
//    public Object test5(){
//        return dao.findAll("select sys_Users_Id as sysUsersId,salt_Key as saltKey from sys_users", Asd.class);
//    }

    @Mapping(value = "/test6")
    public Object test6(){
        return dao.findParameter("update sys_users set salt_value = 'tudou' where name = '大土豆'");
    }
}
