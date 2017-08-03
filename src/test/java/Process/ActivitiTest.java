package Process;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:activiti-context.xml",  
        "classpath*:applicationContext.xml","classpath*:mybatis-config.xml",
        "classpath*:spring-mvc.xml"})
public class ActivitiTest {

	//------------------------------------��������start-----------------------------------
	/** 
	 * �������̶��� act_re_deployment
	 */  
	@Test
	public void deploymentProcessDefinition() {
	    //��������������� 
	    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    Deployment deployment = processEngine.getRepositoryService()// �����̶���Ͳ��������ص�service  
	            .createDeployment()// ����һ���������  
	            .name("activitiemployeeProcess")// ��Ӳ��������  
	            .addClasspathResource("activiti/activitiEmployeeProcess.bpmn")// classpath����Դ�м��أ�һ��ֻ�ܼ���һ���ļ�    
	            .addClasspathResource("activiti/activitiEmployeeProcess.png")// classpath����Դ�м��أ�һ��ֻ�ܼ���һ���ļ�    
	            .deploy();// ��ɲ���  
	    System.out.println("����ID:" + deployment.getId());  
	    System.out.println("�������ƣ�" + deployment.getName());  
	}
	
	/** 
	 * �������̶��� zip 
	 */  
	@Test  
	public void deploymentProcessDefinition_zip() {  
		//��������������� 
	    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    InputStream in = this.getClass().getClassLoader()  
	            .getResourceAsStream("activiti/activitiEmployeeProcess.zip");
	    ZipInputStream zipInputStream = new ZipInputStream(in);  
	    Deployment deployment = processEngine.getRepositoryService()// �����̶���Ͳ��������ص�service  
	            .createDeployment()// ����һ���������  
	            .name("���̶���")// ��Ӳ���
	            .addZipInputStream(zipInputStream)// ָ��zip��ʽ���ļ���ɲ���  
	            .deploy();// ��ɲ���  
	    System.out.println("����ID��" + deployment.getId());  
	    System.out.println("��������:" + deployment.getName());  
	}
	
	//------------------------------------��������end-----------------------------------
	//------------------------------------���̶���start-----------------------------------
	
	/** 
	 * ��ѯ���е�act_re_procdef���̶����
	 */  
	@Test  
	public void findProcessDefinition() {
		//��������������� 
	    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    List<ProcessDefinition> list = processEngine.getRepositoryService()// �����̶���Ͳ����������ص�service  
	            .createProcessDefinitionQuery()// ����һ�����̶���Ĳ�ѯ  
	            /** ָ����ѯ������where���� */  
	            // .deploymentId(deploymentId) //ʹ�ò������ID��ѯ  
	            // .processDefinitionId(processDefinitionId)//ʹ�����̶���ID��ѯ  
	            // .processDefinitionNameLike(processDefinitionNameLike)//ʹ�����̶��������ģ����ѯ  
	            /* ���� */  
	            .orderByProcessDefinitionVersion().asc()  
	            // .orderByProcessDefinitionVersion().desc()  
	            /* ���صĽ���� */  
	            .list();// ����һ�������б���װ���̶���  
	    // .singleResult();//����Ωһ�����  
	    // .count();//���ؽ��������  
	    // .listPage(firstResult, maxResults);//��ҳ��ѯ  
	    if (list != null && list.size() > 0) {  
	        for (ProcessDefinition pd : list) {  
	            System.out.println("���̶���ID:" + pd.getId());// ���̶����key+�汾+���������  
	            System.out.println("���̶��������:" + pd.getName());// ��Ӧhelloworld.bpmn�ļ��е�name����ֵ  
	            System.out.println("���̶����key:" + pd.getKey());// ��Ӧhelloworld.bpmn�ļ��е�id����ֵ  
	            System.out.println("���̶���İ汾:" + pd.getVersion());// �����̶����keyֵ��ͬ����ͬ�£��汾������Ĭ��1  
	            System.out.println("��Դ����bpmn�ļ�:" + pd.getResourceName());  
	            System.out.println("��Դ����png�ļ�:" + pd.getDiagramResourceName());  
	            System.out.println("�������ID��" + pd.getDeploymentId());  
	            System.out.println("#########################################################");  
	        }  
	    }  
	}
	
	/** 
	 * ���ӹ��ܣ���ѯ���°汾�����̶��� 
	 */  
	@Test  
	public void findLastVersionProcessDefinition() {
		//��������������� 
	    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    List<ProcessDefinition> list = processEngine.getRepositoryService()  
	            .createProcessDefinitionQuery()  
	            .orderByProcessDefinitionVersion().asc() // ʹ�����̶���İ汾��������  
	            .list();  
	  
	    /** 
	     * Map<String,ProcessDefinition> map���ϵ�key�����̶����key map���ϵ�value�����̶���Ķ��� 
	     * map���ϵ��ص㣺��map����keyֵ��ͬ������£���һ�ε�ֵ���滻ǰһ�ε�ֵ 
	     */  
	    Map<String, ProcessDefinition> map = new LinkedHashMap<String, ProcessDefinition>();  
	    if (list != null && list.size() > 0) {  
	        for (ProcessDefinition pd : list) {  
	            map.put(pd.getKey(), pd);  
	        }  
	    }  
	  
	    List<ProcessDefinition> pdList = new ArrayList<ProcessDefinition>(
	            map.values());  
	    if (pdList != null && pdList.size() > 0) {  
	        for (ProcessDefinition pd : pdList) {
	            System.out.println("���̶���ID:" + pd.getId());// ���̶����key+�汾+���������  
	            System.out.println("���̶��������:" + pd.getName());// ��Ӧhelloworld.bpmn�ļ��е�name����ֵ  
	            System.out.println("���̶����key:" + pd.getKey());// ��Ӧhelloworld.bpmn�ļ��е�id����ֵ  
	            System.out.println("���̶���İ汾:" + pd.getVersion());// �����̶����keyֵ��ͬ����ͬ�£��汾������Ĭ��1  
	            System.out.println("��Դ����bpmn�ļ�:" + pd.getResourceName());  
	            System.out.println("��Դ����png�ļ�:" + pd.getDiagramResourceName());  
	            System.out.println("�������ID��" + pd.getDeploymentId());  
	            System.out  
	                    .println("#########################################################");  
	        }  
	    }  
	  
	}
	
	/** 
	 * �鿴����ͼ 
	 */  
	@Test  
	public void viewPic() throws IOException {
		//��������������� 
	    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    // ��������ͼƬ�ŵ��ļ�����  
	    String deploymentId = "401";
	    // ��ȡͼƬ��Դ����  
	    List<String> list = processEngine.getRepositoryService()  
	            .getDeploymentResourceNames(deploymentId);  
	    // ����ͼƬ��Դ����  
	    String resourceName = "";  
	    if (list != null && list.size() > 0) {  
	        for (String name : list) {  
	            if (name.indexOf(".png") >= 0) {  
	                resourceName = name;  
	            }  
	        }  
	    }  
	    // ��ȡͼƬ��������  
	    InputStream in = processEngine.getRepositoryService()  
	            .getResourceAsStream(deploymentId, resourceName);  
	    File file = new File("D:/" + resourceName);  
	    // ����������ͼƬд��D����  
	    FileUtils.copyInputStreamToFile(in, file);  
	}
	
	/** 
	 * ɾ�����̶���(ɾ��key��ͬ�����в�ͬ�汾�����̶���) 
	 */  
	@Test  
	public void delteProcessDefinitionByKey() {
		//��������������� 
	    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    // ���̶����Key  
	    String processDefinitionKey = "activitiemployeeProcess";
	    // ��ʹ�����̶����key��ѯ���̶��壬��ѯ�����еİ汾  
	    List<ProcessDefinition> list = processEngine.getRepositoryService()  
	            .createProcessDefinitionQuery()  
	            .processDefinitionKey(processDefinitionKey)// ʹ�����̶����key��ѯ  
	            .list();  
	    // ��������ȡÿ�����̶���Ĳ���ID  
	    if (list != null && list.size() > 0) {  
	        for (ProcessDefinition pd : list) {  
	            // ��ȡ����ID  
	            String deploymentId = pd.getDeploymentId();  
	            /**  
                 * ����������ɾ���� ֻ��ɾ��û�����������̣���������������ͻ��׳��쳣  
                 */
                //processEngine.getRepositoryService().deleteDeployment(deploymentId); 
	            /** 
	             * ����ɾ�� ���������Ƿ�������������ɾ�� 
	             */
	            processEngine.getRepositoryService().deleteDeployment(deploymentId, true);  
	        }
	    }
	}
	
	//------------------------------------���̶���end-----------------------------------
	
	/**
	 * ��������ʵ��
	 * 1)�����ݿ��act_ru_execution����ִ�е�ִ�ж�����в���һ����¼
     * 2)�����ݿ��act_hi_procinst��ʵ������ʷ���в���һ����¼
     * 3)�����ݿ��act_hi_actinst��ڵ����ʷ���в���һ����¼
     * 4)����ͼ�нڵ㶼������ڵ㣬����ͬʱҲ����act_ru_task����ʵ������ʷ�����һ����¼
     * 5)�����ݿ��act_hi_taskinst������ʷ����Ҳ����һ����¼��
	 */
	@Test  
	public void startProcessInstance() {
		//��������������� 
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    // ���̶����key  
	    String processDefinitionKey = "activitiemployeeProcess";
	    ProcessInstance pi = processEngine.getRuntimeService()// ������ִ�е�����ʵ����ִ�ж�����ص�Service  
	            .startProcessInstanceByKey(processDefinitionKey);// ʹ�����̶����key��������ʵ����key��Ӧhellworld.bpmn�ļ���id������ֵ��ʹ��keyֵ������Ĭ���ǰ������°汾�����̶�������  
	    System.out.println("����ʵ��ID:" + pi.getId());// ����ʵ��ID 101  
	    System.out.println("���̶���ID:" + pi.getProcessDefinitionId()); // ���̶���ID HelloWorld:1:4
	}
	
	/** 
	 * ��ѯ��ʷ����ʵ�� 
	 */  
	@Test  
	public void findHistoryProcessInstance(){
		//��������������� 
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    String processInstanceId="45001";
	    HistoricProcessInstance hpi = processEngine.getHistoryService()  
	            .createHistoricProcessInstanceQuery()  
	            .processInstanceId(processInstanceId)  
	            .singleResult();  
	    System.out.println(hpi.getId() +"    "+hpi.getProcessDefinitionId()+"   "+ hpi.getStartTime()+"   "+hpi.getDurationInMillis());  
	}
	
	/** 
	 * ��ѯ��ǰ�˵ĸ������� 
	 */  
	@Test  
	public void findMyPersonTask() {
		//���������������
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//	    String assignee = "zhangsan";
//	    List<Task> list = processEngine.getTaskService()// ������ִ�е���Ϊ������ص�Service
//	            .createTaskQuery()// ���������ѯ����
//	            .taskAssignee(assignee)// ָ��������Ϊ��ѯ��ָ��������
//	            .list();
		List<Task> list= processEngine.getTaskService().createTaskQuery()
				 // �����û�id��ѯ
				.taskCandidateGroup("wangba").list();
	    if (list != null && list.size() > 0) { 
	        for (Task task:list) {
	            System.out.println("����ID:"+task.getId());  
	            System.out.println("��������:"+task.getName());  
	            System.out.println("����Ĵ���ʱ��"+task);  
	            System.out.println("����İ�����:"+task.getAssignee());  
	            System.out.println("����ʵ��ID:"+task.getProcessInstanceId());  
	            System.out.println("ִ�ж���ID:"+task.getExecutionId());  
	            System.out.println("���̶���ID:"+task.getProcessDefinitionId());  
	            System.out.println("#################################");  
	        }
	    }
	}
	
	/** 
	 * ����ҵ����� 
	 */  
	@Test  
	public void completeMyPersonTask(){  
		//��������������� 
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    //����Id  
	    String taskId="104";  
	    processEngine.getTaskService()//������ִ�е���Ϊ������ص�Service  
	            .complete(taskId);  
	    System.out.println("�������:����ID:"+taskId);  
	}
	
	/** 
	 * ��ѯ��ʷ���� 
	 */  
	@Test  
	public void findHistoryTask(){
		//��������������� 
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    String processInstanceId="501";  
	    List<HistoricTaskInstance> list = processEngine.getHistoryService()//����ʷ���ݣ���ʷ����ص�service  
	            .createHistoricTaskInstanceQuery()//������ʷ����ʵ����ѯ  
	            .processInstanceId(processInstanceId)  
//	              .taskAssignee(taskAssignee)//ָ����ʷ����İ�����  
	            .list();  
	    if(list!=null && list.size()>0){  
	        for(HistoricTaskInstance hti:list){  
	            System.out.println(hti.getId()+"    "+hti.getName()+"    "+hti.getProcessInstanceId()+"   "+hti.getStartTime()+"   "+hti.getEndTime()+"   "+hti.getDurationInMillis());  
	            System.out.println("################################");  
	        }  
	    }     
	} 
	
	/** 
	 * ��ѯ����״̬���ж���������ִ�У����ǽ����� 
	 */  
	@Test  
	public void isProcessEnd(){  
		//��������������� 
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    String processInstanceId =  "501";  
	    ProcessInstance pi = processEngine.getRuntimeService()//��ʾ����ִ�е�����ʵ����ִ�ж���  
	            .createProcessInstanceQuery()//��������ʵ����ѯ  
	            .processInstanceId(processInstanceId)//ʹ������ʵ��ID��ѯ  
	            .singleResult();  
	    if(pi==null){  
	        System.out.println("�����Ѿ�����");  
	    }  
	    else{  
	        System.out.println("����û�н���");  
	    }  
	}
	
	/** 
	 * �������̱��� 
	 */  
	@Test  
	public void setVariables() {  
		//��������������� 
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    // ��������ص�service,����ִ�е�service  
	    TaskService taskService = processEngine.getTaskService();  
	    // ����ID  
	    String taskId = "804";  
	    // 1.�������̱�����ʹ�û�����������  
	    taskService.setVariable(taskId, "�������", 7);// ������ID���  
	    taskService.setVariable(taskId, "�������", new Date());  
	    taskService.setVariableLocal(taskId, "���ԭ��", "��ȥ̽�ף�һ��Ը���123");  
	    System.out.println("�������̱����ɹ���");
	}
	
	/** 
	 * ��ȡ���̱��� 
	 */  
	@Test  
	public void getVariables() {
		//��������������� 
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    // ����������ִ�е�service��  
	    TaskService taskService = processEngine.getTaskService();  
	    // ����Id  
	    String taskId = "804";  
	    // 1.��ȡ���̱�����ʹ�û�����������  
	    Integer days = (Integer) taskService.getVariable(taskId, "�������");  
	    Date date = (Date) taskService.getVariable(taskId, "�������");  
	    String reason = (String) taskService.getVariable(taskId, "���ԭ��");  
	  
	    System.out.println("���������" + days);  
	    System.out.println("������ڣ�" + date);  
	    System.out.println("���ԭ��" + reason);  
	  
	}
}
