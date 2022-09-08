package ${serviceImplPackage};

<#--import com.alibaba.dubbo.config.annotation.Service;-->
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
<#--import org.slf4j.Logger;-->
<#--import org.slf4j.LoggerFactory;-->
<#--import javax.annotation.Resource;-->
<#--import com.bjsdzk.common.core.AbstractService;-->
import ${servicePackage}.${modelNameUpperCamel}Service;
<#--import ${mapperPackage}.${modelNameUpperCamel}Mapper;-->
import ${mapperPackage}.${modelNameUpperCamel}Mapper;
import ${modelPackage}.${modelNameUpperCamel};
import lombok.extern.slf4j.Slf4j;
<#--import com.hoze.pf.common.vo.PaginationVO;
import com.hoze.pf.common.vo.BaseRequestVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hoze.pf.common.exception.CommonException;
import com.hoze.pf.common.utils.UUIDUtil;
import com.hoze.pf.common.utils.StringUtil;
import java.util.List;-->

/**
* @Author: ${author}
* @Company: 北京万相融通科技股份有限公司
* @Date: ${date}
* @Version: 1.0
* @Description: ${modelAlias}服务实现类
*/
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ${modelNameUpperCamel}ServiceImpl extends ServiceImpl<${modelNameUpperCamel}Mapper, ${modelNameUpperCamel}> implements ${modelNameUpperCamel}Service {

    <#--private static final Logger LOGGER = LoggerFactory.getLogger(${modelNameUpperCamel}ServiceImpl.class);-->

    <#--@Resource-->
    <#--private ${modelNameUpperCamel}Mapper ${modelNameLowerCamel}Mapper;-->

    <#--@Override
    public int insert(${modelNameUpperCamel} ${modelNameLowerCamel}) throws CommonException {
        if(StringUtil.isNull(${modelNameLowerCamel}.get${primaryKeyNameUpperFirst}())){
            String uuid32 = UUIDUtil.getUUID32();
            ${modelNameLowerCamel}.set${primaryKeyNameUpperFirst}(uuid32);
        }

        return ${modelNameLowerCamel}Mapper.insert(${modelNameLowerCamel});
    }

    @Override
    public int insertSelective(${modelNameUpperCamel} ${modelNameLowerCamel}) throws CommonException {
        if(StringUtil.isNull(${modelNameLowerCamel}.get${primaryKeyNameUpperFirst}())){
            String uuid32 = UUIDUtil.getUUID32();
            ${modelNameLowerCamel}.set${primaryKeyNameUpperFirst}(uuid32);
        }
        this.check(${modelNameLowerCamel},false);
        return ${modelNameLowerCamel}Mapper.insertSelective(${modelNameLowerCamel});
    }

    @Override
    public int deleteById(${primaryKeyType} ${primaryKeyName}) throws CommonException {
        return ${modelNameLowerCamel}Mapper.deleteByPrimaryKey(${primaryKeyName});
    }

    @Override
    public int updateByIdSelective(${modelNameUpperCamel} ${modelNameLowerCamel}) throws CommonException {
        this.check(${modelNameLowerCamel},true);
        return ${modelNameLowerCamel}Mapper.updateByPrimaryKeySelective(${modelNameLowerCamel});
    }

    @Override
    public int updateById(${modelNameUpperCamel} ${modelNameLowerCamel}) throws CommonException {
        return ${modelNameLowerCamel}Mapper.updateByPrimaryKey(${modelNameLowerCamel});
    }

    @Override
    public ${modelNameUpperCamel} getById(${primaryKeyType} ${primaryKeyName}) throws CommonException {
        return ${modelNameLowerCamel}Mapper.selectByPrimaryKey(${primaryKeyName});
    }

    @Override
    public List<${modelNameUpperCamel}> findByModel(${modelNameUpperCamel} ${modelNameLowerCamel}) throws CommonException {
        return ${modelNameLowerCamel}Mapper.findByModel(${modelNameLowerCamel});
    }

    @Override
    public int isExist(${modelNameUpperCamel} ${modelNameLowerCamel}) {
        return ${modelNameLowerCamel}Mapper.isExist(${modelNameLowerCamel});
    }

    @Override
    public List<${modelNameUpperCamel}> findAll() {
        return ${modelNameLowerCamel}Mapper.findAll();
    }

    @Override
    public PaginationVO<${modelNameUpperCamel}> findPage(BaseRequestVO requestVO) {
        /*
        * 根据编码和名称模糊查询
        * */
        ${modelNameUpperCamel} ${modelNameLowerCamel} = new ${modelNameUpperCamel}();
        <#list codeList as code>
        ${code}
        </#list>

        PageHelper.startPage(requestVO.getPageNum(), requestVO.getPageSize());

        List<${modelNameUpperCamel}> ${modelNameLowerCamel}List = ${modelNameLowerCamel}Mapper.findByLike(${modelNameLowerCamel});

        PageInfo<${modelNameUpperCamel}> pageInfo = new PageInfo<>(${modelNameLowerCamel}List);

        PaginationVO<${modelNameUpperCamel}> paginationVo = new PaginationVO(pageInfo.getTotal(), pageInfo.getList());

        return paginationVo;
    }

    /*
    * 校验参数，updateFlag为是否是update校验，若是，则判断是否存在查询的数量应该由0改为1
    * 校验的是同一机构下的参数是否重复
    * */
    private void check(${modelNameUpperCamel} record,boolean updateFlag) throws CommonException {
        int count = 0;
        if (updateFlag) {
            count = 1;
        }

        //TODO 这里编写判定逻辑
        /*if (record.getCd() != null || record.getNa() != null) {
            if (StringUtil.isNull(record.getCd(), record.getNa())) {
                throw new CommonException("编码、名称不能为空");
            }
            ${modelNameUpperCamel} ${modelNameLowerCamel} = new ${modelNameUpperCamel}();
            ${modelNameLowerCamel}.setCd(record.getCd());

            List<${modelNameUpperCamel}> byModel = ${modelNameLowerCamel}Mapper.findByModel(${modelNameLowerCamel});
            if (byModel != null && byModel.size() > count) {
                throw new CommonException("编码全局唯一不能重复");
            }

            ${modelNameUpperCamel} ${modelNameLowerCamel}2 = new ${modelNameUpperCamel}();
            ${modelNameLowerCamel}2.setNa(record.getNa());

            List<${modelNameUpperCamel}> byModel2 = ${modelNameLowerCamel}Mapper.findByModel(${modelNameLowerCamel}2);
            if (byModel2 != null && byModel2.size() > count) {
                throw new CommonException("名称全局唯一不能重复");
            }
        }*/
    }-->

}
