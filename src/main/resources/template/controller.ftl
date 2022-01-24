package ${controllerPackage}<#--.${sign}-->;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.resafety.twin.cloud.application.controller.base.BaseController;
import com.resafety.twin.cloud.common.api.CommonResult;
import org.springframework.web.bind.annotation.*;
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
    @GetMapping("/list")
    public CommonResult<${modelNameUpperCamel}> list(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam(defaultValue = "") String yourField) {
        return CommonResult.success(${modelNameLowerCamel}Service.pageList(yourField,pageNum,pageSize));
    }

    /**
    * 查询详情
    * @return
    */
    @GetMapping("/detail")
    public CommonResult<${modelNameUpperCamel}> detail() {
        return CommonResult.success(${modelNameLowerCamel}Service.find${modelNameUpperCamel}ById(id));
    }

    /**
    * 新增数据
    * @return
    */
    @PostMapping("/add")
    public CommonResult add(${modelNameUpperCamel} ${modelNameLowerCamel}) {
        ${modelNameLowerCamel}Service.insert(${modelNameLowerCamel});
        return CommonResult.success("成功");
    }

    /**
    * 更新数据
    * @return
    */
    @PostMapping("/update")
    public CommonResult update(${modelNameUpperCamel} ${modelNameLowerCamel}) {
        ${modelNameLowerCamel}Service.update(${modelNameLowerCamel});
        return CommonResult.success("成功");
    }

    /**
    * 物理删除
    * @return
    */
    @PostMapping("/remove/{id}")
    public CommonResult remove() {
        ${modelNameLowerCamel}Service.deleteByIds(RequestUtils.getArray("id"));
        return CommonResult.success("成功");
    }

}
