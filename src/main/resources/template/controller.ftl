package ${controllerPackage}<#--.${sign}-->;

import javax.servlet.http.HttpServletRequest;
import com.alibaba.dubbo.config.annotation.Reference;
import com.hoze.pf.common.constant.CommonResult;
import com.hoze.pf.common.constant.CommonResultConstant;
import com.hoze.pf.common.exception.CommonException;
import ${servicePackage}.${modelNameUpperCamel}Service;
import ${modelPackage}.${modelNameUpperCamel};
import com.hoze.pf.common.vo.BaseRequestVO;
import org.springframework.util.StringUtils;
import com.hoze.pf.common.utils.SelectUtil;
import com.hoze.pf.common.vo.PaginationVO;
import com.hoze.pf.common.scan.log.annotation.ControllerAdapter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

/**
 * ${modelNameLowerCamel}控制器
 * Created by ${author} on ${date}.
 */
@RestController
<#--@RequestMapping("${baseRequestMapping}")-->
@RequestMapping("/pf/ci/${modelNameLowerCamel}/")
@Api(description="开发中", tags="开发中")
@ControllerAdapter
public class ${modelNameUpperCamel}Controller {

    @Reference
    ${modelNameUpperCamel}Service ${modelNameLowerCamel}Service;

    @ApiOperation(value="查询")
    @RequestMapping(value = "find",method = RequestMethod.POST)
    public CommonResult find(@RequestBody BaseRequestVO requestVO, HttpServletRequest request) throws CommonException {
        PaginationVO<${modelNameUpperCamel}> page = ${modelNameLowerCamel}Service.findPage(requestVO);
        return new CommonResult(CommonResultConstant.SUCCESS,page);
    }

    @ApiOperation(value="${modelNameUpperCamel}下拉")
    //@ApiImplicitParam(name = "yourSelectId", value = "Id解释", paramType = "query", required = false, dataType = "String")
    @RequestMapping(value = "/select${leafName}",method = RequestMethod.GET)
    public CommonResult select${leafName}(HttpServletRequest request/*String yourSelectId*/) throws CommonException{
        ${modelNameUpperCamel} ${modelNameLowerCamel} = new ${modelNameUpperCamel}();
        //${modelNameLowerCamel}.your_set_method(yourSelectId);
        List<${modelNameUpperCamel}> ${modelNameLowerCamel}List = ${modelNameLowerCamel}Service.findByModel(${modelNameLowerCamel});
        List<Map<String,Object>> selectListMap = SelectUtil.getSelectData(${modelNameLowerCamel}List,${modelNameUpperCamel}.class,"${primaryKeyName}","${selectName}");
        return new CommonResult(CommonResultConstant.SUCCESS,selectListMap);
    }

    @ApiOperation(value="保存")
    @ApiImplicitParam(name = "${modelNameLowerCamel}", value = "实体", paramType = "body",required = false,  dataType = "${modelNameUpperCamel}")
    @RequestMapping(value = "insert",method = RequestMethod.POST)
    public CommonResult save(@RequestBody ${modelNameUpperCamel} ${modelNameLowerCamel}, HttpServletRequest request) throws CommonException {
        int num = ${modelNameLowerCamel}Service.insertSelective(${modelNameLowerCamel});
        return new CommonResult(CommonResultConstant.SUCCESS, num);
    }

    @ApiOperation(value="详情")
    @ApiImplicitParam(name = "${primaryKeyName}", value = "主键", paramType = "query",required = true,  dataType = "${primaryKeyType}")
    @RequestMapping(value = "detail",method = RequestMethod.GET)
    public CommonResult detail(${primaryKeyType} ${primaryKeyName}, HttpServletRequest request) throws CommonException {
        if(StringUtils.isEmpty(${primaryKeyName})){
            throw new CommonException("请选择要编辑的项");
        }
        ${modelNameUpperCamel} ${modelNameLowerCamel} = ${modelNameLowerCamel}Service.selectByPrimaryKey(${primaryKeyName});
        return new CommonResult(CommonResultConstant.SUCCESS, ${modelNameLowerCamel});
    }

    @ApiOperation(value="修改")
    @ApiImplicitParam(name = "${modelNameLowerCamel}", value = "实体", paramType = "body",required = false,  dataType = "${modelNameUpperCamel}")
    @RequestMapping(value = "update",method = RequestMethod.POST)
    public CommonResult update(@RequestBody ${modelNameUpperCamel} ${modelNameLowerCamel}, HttpServletRequest request) throws CommonException {
        int num = ${modelNameLowerCamel}Service.updateByPrimaryKeySelective(${modelNameLowerCamel});
        return new CommonResult(CommonResultConstant.SUCCESS, num);
    }

    @ApiOperation(value="根据主键删除")
    @ApiImplicitParam(name = "${primaryKeyName}", value = "主键", paramType = "query", required = true, dataType = "${primaryKeyType}")
    @RequestMapping(value = "delete",method = RequestMethod.GET)
    public CommonResult delete(${primaryKeyType} ${primaryKeyName}, HttpServletRequest request) throws CommonException {
        if(StringUtils.isEmpty(${primaryKeyName})){
            throw new CommonException("请选择要删除的项");
        }
        int num = ${modelNameLowerCamel}Service.deleteByPrimaryKey(${primaryKeyName});
        return new CommonResult(CommonResultConstant.SUCCESS, num);
    }

}
