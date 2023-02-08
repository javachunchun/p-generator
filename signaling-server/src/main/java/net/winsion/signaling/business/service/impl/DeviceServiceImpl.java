package net.winsion.signaling.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.winsion.signaling.business.service.DeviceService;
import net.winsion.signaling.business.mapper.DeviceMapper;
import net.winsion.signaling.business.entity.Device;
import lombok.extern.slf4j.Slf4j;

/**
* @Author: ct.liu
* @Company: 北京万相融通科技股份有限公司
* @Date: 2023/02/08
* @Version: 1.0
* @Description: 设备TVM信息服务实现类
*/
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {


}
