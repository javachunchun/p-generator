package ${servicePackage};

import ${modelPackage}.${modelNameUpperCamel};
import com.hoze.pf.common.vo.PaginationVO;
import com.hoze.pf.common.vo.BaseRequestVO;
import java.util.List;

/**
* 降级实现${modelNameUpperCamel}Service接口
* Created by ${author} on ${date}.
*/
public class ${modelNameUpperCamel}ServiceMock implements ${modelNameUpperCamel}Service {

    @Override
    public int deleteById(${primaryKeyType} ${primaryKeyName}) {
       return 0;
    }

    @Override
    public int insert(${modelNameUpperCamel} ${modelNameLowerCamel}) {
       return 0;
    }

    @Override
    public int insertSelective(${modelNameUpperCamel} ${modelNameLowerCamel}) {
       return 0;
    }

    @Override
    public ${modelNameUpperCamel} getById(${primaryKeyType} ${primaryKeyName}) {
       return null;
    }

    @Override
    public int updateByIdSelective(${modelNameUpperCamel} ${modelNameLowerCamel}) {
       return 0;
    }

    @Override
    public int updateById(${modelNameUpperCamel} ${modelNameLowerCamel}) {
       return 0;
    }

    @Override
    public List<${modelNameUpperCamel}> findByModel(${modelNameUpperCamel} ${modelNameLowerCamel}) {
        return null;
    }

    @Override
    public int isExist(${modelNameUpperCamel} ${modelNameLowerCamel}) {
        return 0;
    }

    @Override
    public List<${modelNameUpperCamel}> findAll() {
        return null;
    }

    @Override
    public PaginationVO<${modelNameUpperCamel}> findPage(BaseRequestVO requestVO) {
        return null;
    }
}
