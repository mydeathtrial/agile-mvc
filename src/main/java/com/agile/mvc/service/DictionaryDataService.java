package com.agile.mvc.service;

import com.agile.common.base.RETURN;
import com.agile.common.base.poi.Cell;
import com.agile.common.base.poi.ExcelFile;
import com.agile.common.base.poi.SheetData;
import com.agile.common.mvc.service.BusinessService;
import com.agile.common.util.POIUtil;
import com.agile.mvc.entity.SysUsersEntity;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import com.agile.mvc.entity.DictionaryDataEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 佟盟
 */
@Service
public class DictionaryDataService extends BusinessService<DictionaryDataEntity> {
    public void task1(){
        System.out.println("我是定时任务1-------------------------------------");
    }
    public void task2(){
        System.out.println("我是定时任务2-------------------------------------");
    }

    public RETURN a0(){
        dao.save(SysUsersEntity.builder().setName("sdad").setSysUsersId("443").build());
        return RETURN.SUCCESS;
    }

    public RETURN a5(){
        setOutParam("key","value");
        setOutParam("key2","value");
        return RETURN.SUCCESS;
    }

    public String a1(){
        return "123123123";
    }

    public Object a2(){
        setOutParam("ke","sdf");
        return DictionaryDataEntity.builder().setValue("123").build();
    }

    public Object a4(){
        SheetData d = new SheetData();
        d.setName("我是Sheet页");
        Map<String,String> headColumn = new HashMap<>();
        headColumn.put("first1","第一列");
        headColumn.put("first2","第二列");
        headColumn.put("first3","第三列");
        headColumn.put("first4","第四列");
        headColumn.put("first5","第五列");
        headColumn.put("first6","第六列");
        d.addCell(Cell.builder().setKey("first1").setShowName("第一列").setSort(6).build())
                .addCell(Cell.builder().setKey("first2").setShowName("第二列").setSort(5).build())
                .addCell(Cell.builder().setKey("first3").setShowName("第三列").setSort(4).build())
                .addCell(Cell.builder().setKey("first4").setShowName("第四列").setSort(3).build())
                .addCell(Cell.builder().setKey("first5").setShowName("第五列").setSort(2).build())
                .addCell(Cell.builder().setKey("first6").setShowName("第六列").setSort(1).build());
        List<Object> list = new ArrayList<>();
        list.add(POITest.builder().setFirst1("1").setFirst2("11").setFirst3("111").setFirst4("1111").setFirst5("11111").setFirst6("111111"));
        list.add(POITest.builder().setFirst1("2").setFirst2("22").setFirst3("222").setFirst4("2222").setFirst5("22222").setFirst6("222222"));
        d.setData(list);
        Workbook workbook = POIUtil.creatExcel(POIUtil.VERSION.V2008, d);
        return new ExcelFile("测试",workbook);
    }

//    @Init
//    public Map<String,Object> a3(){
//        // 获取查询es工具实体类
//        ClientEngine.build().start();
//        //Map<String,Object> map=dataSearch.fetch(index,type,hgid);
//        //Map<String,Object> map=dataSearch.
//        // return new HashMap<>();
//
//        DataSearch dataSearch = DataSearchFactory.buildDataSearch();
//        //查询条件
//        SearchCri cri = new SearchCri();
//        cri.setIndexName("security_log_");
//        cri.setTypes("log");
////        cri.setStartTime(startTime);
////        cri.setEndTime(endTime);
//        cri.setDateFormat("yyyy.MM.dd");
////        String exp = " id='"+hgid+"'  " ;
////        cri.setExpression(exp);
//        List<String> fields = new ArrayList<>();
//        fields.add("cve_id");
//        fields.add("cnve_id");
//        fields.add("vuln_name");
//        fields.add("create_time");
//        fields.add("severity");
//        fields.add("vuln_type");
//        fields.add("vuln_description");
//        fields.add("url");
//        fields.add("solution");
//        fields.add("vuln_type");
//        fields.add("dst_port");
//        Map<String, Object> res = null;
//        List<Map<String, Object>> result=dataSearch.query(fields,cri,"id","desc",1);
//        if(result.size()>0)
//        {
//            res=result.get(0);
//        }
//        return res;
//    }
}
