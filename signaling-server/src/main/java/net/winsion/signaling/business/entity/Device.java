package net.winsion.signaling.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.util.Date;
import lombok.Data;

/**
 * @Author：ct.liu
 * @Company: 北京万相融通科技股份有限公司
 * @Date：2023/02/08
 * @Version: 1.0
 * @Description: 
 */
@Data
public class Device {
    private Long id;

    private Date ctime;

    private Date utime;

    private String password;

    private String salt;

    //设备状态：0-未连接，1-振铃，2-已接通
    private Integer status;

    //在线状态：0-离线，1-在线
    private Integer online;

    private Integer del;
}