package ${servicePackage}<#--.${sign}-->;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import ${modelPackage}.${modelNameUpperCamel};
import ${mapperPackage}.${modelNameUpperCamel}Dao;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.resafety.util.IDUtils;
import com.resafety.util.DateUtils;
import java.util.List;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * ${modelNameLowerCamel}服务类
 * Created by ${author} on ${date}.
 */
@Service
public class ${modelNameUpperCamel}Service {
    @Autowired
    private ${modelNameUpperCamel}Dao ${modelNameLowerCamel}Dao;

    /***
    * 查询列表
    * @param pageNum
    * @param pageSize
    * TODO @param yourField
    * @return
    */
    public JSONObject list(Integer pageNum, Integer pageSize,String yourField){
        JSONObject result = new JSONObject();
        ${modelNameUpperCamel} ${modelNameLowerCamel} = new ${modelNameUpperCamel}();
        //${modelNameLowerCamel}.setYourField(yourField);
        PageHelper.startPage(pageNum, pageSize);
        List<${modelNameUpperCamel}> list = ${modelNameLowerCamel}Dao.list(${modelNameLowerCamel});
        PageInfo<${modelNameUpperCamel}> pageInfo = new PageInfo<${modelNameUpperCamel}>(list);
        JSONArray root = (JSONArray)JSON.toJSON(list);
        result.put("root", root);
        result.put("totalCount", Long.valueOf(pageInfo.getTotal()));
        return result;
    }

    /**
    * 新增数据
    * @return 是否成功
    */
    public int insert(${modelNameUpperCamel} ${modelNameLowerCamel}) {
        ${modelNameLowerCamel}.setId(IDUtils.get());
        ${modelNameLowerCamel}.setCreateTime(DateUtils.getNowDate());
        return ${modelNameLowerCamel}Dao.add(${modelNameLowerCamel});
    }

    /**
    * 修改数据
    * @return 是否成功
    */
    public int update(${modelNameUpperCamel} ${modelNameLowerCamel}) {
        ${modelNameLowerCamel}.setUpdateTime(DateUtils.getNowDate());
        return ${modelNameLowerCamel}Dao.update${modelNameUpperCamel}ById(${modelNameLowerCamel});
    }

    /**
    * 通过主键删除数据
    * @param ids 主键
    * @return 是否成功
    */
    public int deleteByIds(String[] ids) {
        if (ids != null && ids.length >0){
            for (String id : ids){
                ${modelNameLowerCamel}Dao.deleteById(id);
            }
            return 1;
        }else{
            return 0;
        }
    }

    /**
    *  根据 查询详情
    * @Param id
    * @Return template
    * @Exception
    */
    public JSONObject find${modelNameUpperCamel}ById(String id) {
        JSONObject result = new JSONObject();
        ${modelNameUpperCamel} ${modelNameUpperCamel} = ${modelNameLowerCamel}Dao.selectById(id);
        JSONObject data = (JSONObject)JSON.toJSON(${modelNameUpperCamel});
        result.put("data", data);
        result.put("code", Integer.valueOf(200));
        return result;
    }
}
