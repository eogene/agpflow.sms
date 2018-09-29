package cn.hangar.agpflow.sms.service;

import org.apache.axis.client.Call;  
import org.apache.axis.client.Service;  
import javax.xml.namespace.QName;  

public class InnerAdapterService {
	String endpoint;
	public InnerAdapterService(String endpoint)
	{
		this.endpoint = endpoint;
	}

	 public String process(String operation, String systemName, String xmlData) throws Exception
	 {
		 Service service = new Service();
         Call call = (Call) service.createCall();
         call.setOperationName(new QName("http://adapter.nsm.huawei.com", "process")); 
         call.setTargetEndpointAddress( new java.net.URL(endpoint) );
          
         //设置发送字段类型，名字等
         call.addParameter("operation", org.apache.axis.Constants.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
         call.addParameter("systemName", org.apache.axis.Constants.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
         call.addParameter("xmlData", org.apache.axis.Constants.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
         call.setReturnType(org.apache.axis.Constants.XSD_STRING);//设置返回类型
          
         call.setUseSOAPAction(true);
         call.setSOAPActionURI("");
         String msg = (String)call.invoke(new Object[]{operation,systemName,xmlData});
         return msg;
	 }
}
