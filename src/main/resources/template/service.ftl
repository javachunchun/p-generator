package ${servicePackage}<#--.${sign}-->;

<#--刘春春修改，自动生成代码约束方法名 import com.hoze.pf.common.base.BaseService;-->
import com.hoze.pf.common.vo.PaginationVO;
import com.hoze.pf.common.vo.BaseRequestVO;
import java.util.List;
import com.hoze.pf.common.exception.CommonException;
import ${modelPackage}.${modelNameUpperCamel};
<#--import ${basePackage}.model&lt;#&ndash;.${sign}&ndash;&gt;.${modelNameUpperCamel};
import ${basePackage}.service.Service;-->

/**
 * ${modelNameLowerCamel}服务类
 * Created by ${author} on ${date}.
 */
public interface ${modelNameUpperCamel}Service<#--刘春春修改，自动生成代码约束方法名 extends BaseService<${modelNameUpperCamel}>--> {
    List<${modelNameUpperCamel}> findAll() throws CommonException;

    List<${modelNameUpperCamel}> findByModel(${modelNameUpperCamel} ${modelNameLowerCamel}) throws CommonException;

    PaginationVO<${modelNameUpperCamel}> findPage(BaseRequestVO requestVO) throws CommonException;

    ${modelNameUpperCamel} getById(${primaryKeyType} ${primaryKeyName}) throws CommonException;

    int isExist(${modelNameUpperCamel} ${modelNameLowerCamel}) throws CommonException;

    int insert(${modelNameUpperCamel} ${modelNameLowerCamel}) throws CommonException;

    int insertSelective(${modelNameUpperCamel} ${modelNameLowerCamel}) throws CommonException;

    int updateByIdSelective(${modelNameUpperCamel} ${modelNameLowerCamel}) throws CommonException;

    int updateById(${modelNameUpperCamel} ${modelNameLowerCamel}) throws CommonException;

    int deleteById(${primaryKeyType} ${primaryKeyName}) throws CommonException;
}
