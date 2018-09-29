package cn.hangar.agpflow.sms.model;

import java.util.Date;


import cn.hangar.util.data.PropertyEntity;
import cn.hangar.util.ioc.Id;
import cn.hangar.util.ioc.Table;
import lombok.Getter;
import lombok.Setter;

/**
* @author Eoge E-mail:18802012501@139.com
* @version 创建时间：2018年1月15日 下午6:18:35
* 类说明
*/
@Getter
@Setter
@Table(name = "SYS_SMSLIST")
public class SMSListInfo extends PropertyEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	public String RecId;
	public String userId;
	public String UserName;
	public String Mobile;
	public String Contents;
	public String ResId;
	public String ResInstId;
	public String TaskId;
	public Date RecDate;
	public String AttachIds;
	public String SendTimeSpan;
	public Date SendTime;
	public String SMSFlag;//短信标识(短信回复使用)
	public String Results;//发送结果
	public int EndFlag;//发送标志    0:待发送，1:已发送
	
	public static String DbTableName = "SYS_SMSLIST";		
    
    public static String DbResId = "SYS_SMSLIST";
    
    public static final String _SendFlag = "SendFlag";
    public static final String _Results = "Results";
    public static final String _EndFlag = "EndFlag";
    public static final String _SendTime = "SendTime";
    /// <summary>
    /// 
    /// </summary>		
    public static final String _RecId = "RecId";

    /// <summary>
    /// 
    /// </summary>		
    public static final String _UserId = "userId";
  /// <summary>
    /// 
    /// </summary>		
    public static final String _UserName = "UserName";

    /// <summary>
    /// 
    /// </summary>		
    public static final String _Mobile = "Mobile";

    /// <summary>
    /// 
    /// </summary>		
    public static final String _Contents = "Contents";

    /// <summary>
    /// 
    /// </summary>		
    public static final String _ResId = "ResId";

    /// <summary>
    /// 
    /// </summary>		
    public static final String _ResInstId = "ResInstId";

    /// <summary>
    /// 
    /// </summary>		
    public static final String _TaskId = "TaskId";

    /// <summary>
    /// 
    /// </summary>		
    public static final String _RecDate = "RecDate";

    /// <summary>
    /// 
    /// </summary>		
    public static final String _AttachIds = "AttachIds";

    /// <summary>
    /// 
    /// </summary>		
    public static final String _SendTimeSpan = "SendTimeSpan";
}
