package ${servicePackage}<#--.${sign}-->;

import com.hoze.pf.common.base.BaseService;
import com.hoze.pf.common.vo.PaginationVO;
import com.hoze.pf.common.vo.BaseRequestVO;
import ${modelPackage}.${modelNameUpperCamel};
<#--import ${basePackage}.model&lt;#&ndash;.${sign}&ndash;&gt;.${modelNameUpperCamel};
import ${basePackage}.service.Service;-->

/**
 * ${modelNameLowerCamel}服务类
 * Created by ${author} on ${date}.
 */
public interface ${modelNameUpperCamel}Service extends BaseService<${modelNameUpperCamel}> {

    int isExist(${modelNameUpperCamel} ${modelNameLowerCamel});

    PaginationVO<${modelNameUpperCamel}> findPage(BaseRequestVO requestVO);
}
