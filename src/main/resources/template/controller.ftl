package ${controllerPackage}<#--.${sign}-->;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.resafety.webioc.sys.common.BaseController;
import com.alibaba.fastjson.JSONObject;
import com.resafety.webioc.utils.ResultDto;
import com.resafety.util.RequestUtils;
import ${servicePackage}.${modelNameUpperCamel}Service;
import ${modelPackage}.${modelNameUpperCamel};

/**
 * ${modelAlias}控制器
 * Created by ${author} on ${date}.
 */
@RestController
@RequestMapping("/${modelName}")
public class ${modelNameUpperCamel}Controller extends BaseController {

    @Autowired
    ${modelNameUpperCamel}Service ${modelNameLowerCamel}Service;

    /**
    * 查询列表
    * @param pageNum 第几页
    * @param pageSize 每页显示数量
    * TODO @param yourField 查询字段
    * @return
    */
    @RequestMapping("/list")
    public JSONObject list(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam(defaultValue = "") String yourField) {
        return ${modelNameLowerCamel}Service.list(pageNum,pageSize,yourField);
    }

    /**
    * 查询详情
    * @return
    */
    @RequestMapping("/data")
    public JSONObject data() {
        String id = RequestUtils.get("id");
        return ${modelNameLowerCamel}Service.find${modelNameUpperCamel}ById(id);
    }

    /**
    * 新增数据
    * @return
    */
    @RequestMapping("/insert")
    public ResultDto insert(${modelNameUpperCamel} ${modelNameLowerCamel}) {
        int insert = ${modelNameLowerCamel}Service.insert(${modelNameLowerCamel});
        if (insert == 1) {
            String msg = "新增成功";
            return new ResultDto(true, msg);
        } else {
            String msg = "新增失败";
            return new ResultDto(false, msg);
        }
    }

    /**
    * 更新数据
    * @return
    */
    @RequestMapping("/update")
    public ResultDto update(${modelNameUpperCamel} ${modelNameLowerCamel}) {
        int update = ${modelNameLowerCamel}Service.update(${modelNameLowerCamel});
        if (update == 1) {
            return new ResultDto(true, "修改成功");
        } else {
            return new ResultDto(false, "修改失败");
        }
    }

    /**
    * 物理删除
    * @return
    */
    @RequestMapping("/deleteByIds")
    public ResultDto deleteByIds() {
        int delete = ${modelNameLowerCamel}Service.deleteByIds(RequestUtils.getArray("id"));
        if (delete >0 ) {
            return new ResultDto(true, "删除成功");
        } else {
            return new ResultDto(false, "删除失败");
        }
    }

}
